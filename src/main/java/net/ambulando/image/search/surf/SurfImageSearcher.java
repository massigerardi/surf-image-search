/**
 * 
 */
package net.ambulando.image.search.surf;

import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ambulando.image.search.Candidate;
import net.ambulando.image.search.ImageSearcher;
import net.ambulando.image.search.heuristic.EuclideanHeuristic;
import net.ambulando.image.search.heuristic.Heuristic;
import net.ambulando.image.search.surf.ip.InterestPoint;
import net.ambulando.image.search.surf.ip.InterestPointMatcher;
import net.ambulando.image.search.surf.ip.InterestPointsFinder;
import net.ambulando.image.search.utils.ImageHelper;
import net.ambulando.image.search.utils.ImageUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.google.common.base.Objects;

/**
 * @author massi
 *
 */
@Getter
@Slf4j
public class SurfImageSearcher implements ImageSearcher {

    private static final float FIRST_LEVEL = 0.3f;
    private static final float SECOND_LEVEL = 0.6f;

    private ImageHelper imageHelper = new ImageHelper();

    private final Map<String, ImageDescriptor> imageDescriptors = new TreeMap<String, ImageDescriptor>();

    private String sources;

    private Cache interestPoints;

    private final InterestPointsFinder finder = new InterestPointsFinder();

    private final Opener opener = new Opener();

    private static boolean useCache = false;

    @Setter
    private Heuristic heuristic;

    public Collection<Candidate> search(File file) {
        return search(file, imageDescriptors.values());
    }

    public Collection<Candidate> search(File file,
            Collection<ImageDescriptor> imageDescriptors) {
        Collection<Candidate> candidates = new TreeSet<Candidate>();
        ImageDescriptor search = resizeAndFindInterestPoints(file);
        float firstLevel = (float) (FIRST_LEVEL * search.getPoints().size());
        float secondLevel = (float) (SECOND_LEVEL * search.getPoints().size());
        Candidate firstBest = null;
        Candidate secondBest = null;
        for (ImageDescriptor candidateDescriptor : imageDescriptors) {
            final Map<InterestPoint, InterestPoint> matchedPoints = InterestPointMatcher
                    .findMatches(search.getPoints(),
                            candidateDescriptor.getPoints(), heuristic);
            Candidate candidate = new Candidate(candidateDescriptor.getImage(),
                    matchedPoints);
            if (candidate.score() < firstLevel) {
                continue;
            }
            if (firstBest == null) {
                firstBest = candidate;
                secondBest = null;
            } else {
                if (candidate.score() > firstBest.score()) {
                    secondBest = firstBest;
                    firstBest = candidate;
                } else if (secondBest == null
                        || candidate.score() > secondBest.score()) {
                    secondBest = candidate;
                }
            }
            if (candidate.score() < secondLevel) {
                break;
            }
        }
        if (firstBest != null) {
            candidates.add(firstBest);
        }
        if (secondBest != null) {
            candidates.add(secondBest);
        }
        return candidates;
    }

    public SurfImageSearcher(String sources) {
        this(sources, new EuclideanHeuristic());
    }

    public SurfImageSearcher(String sources, Heuristic heuristic) {
        this.sources = new File(sources).getAbsolutePath();
        this.heuristic = heuristic;
        init();
    }

    protected void init() {
        List<File> files = imageHelper.getImages(new File(sources));
        if (useCache) {
            System.setProperty("net.sf.ehcache.enableShutdownHook", "true");
            interestPoints = CacheManager.getInstance().getCache(
                    "interestPoints");
            try {
                for (File file : files) {
                    loadImageInterestPointsFromCache(file);
                }
            } finally {
                CacheManager.getInstance().shutdown();
            }
        } else {
            for (File file : files) {
                ImageDescriptor imageDescriptor = resizeAndFindInterestPoints(file);
                imageDescriptors.put(file.getAbsolutePath(),
                        new ImageDescriptor(file, imageDescriptor.getPoints()));
            }
        }
    }

    private void loadImageInterestPointsFromCache(File file) {
        ImageDescriptor imageDescriptor = null;
        Element element = interestPoints.get(file.getAbsolutePath());
        if (element != null) {
            log.debug("file {} was found in cache", file.getAbsolutePath());
            imageDescriptor = (ImageDescriptor) element.getObjectValue();
        } else {
            log.debug("file {} was NOT found in cache", file.getAbsolutePath());
            imageDescriptor = resizeAndFindInterestPoints(file);
            interestPoints.put(new Element(file.getAbsolutePath(),
                    imageDescriptor));
            interestPoints.flush();
        }
        imageDescriptors.put(file.getAbsolutePath(), imageDescriptor);
    }

    public ImageDescriptor resizeAndFindInterestPoints(File file) {
        ImagePlus image = opener.openImage(file.getAbsolutePath());
        try {
            image = ImageUtils.resize(600, image);
        } catch (Exception e) {
            log.error("error while resizing", e);
        }
        List<InterestPoint> interestPoints = findInterestPoints(image);
        return new ImageDescriptor(file, interestPoints);
    }

    private List<InterestPoint> findInterestPoints(ImagePlus image) {
        return finder.findInterestingPoints(image.getProcessor(), false);
    }

    public static void setUseCache(boolean b) {
        useCache = b;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass())
                .add("sources", sources)
                .add("images", imageDescriptors.size()).toString();
    }

}
