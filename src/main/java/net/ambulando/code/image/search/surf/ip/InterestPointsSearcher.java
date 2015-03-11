/**
 * 
 */
package net.ambulando.code.image.search.surf.ip;

import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ambulando.code.image.search.Candidate;
import net.ambulando.code.image.search.ImageSearcher;
import net.ambulando.code.image.search.heuristic.EuclideanHeuristic;
import net.ambulando.code.image.search.heuristic.Heuristic;
import net.ambulando.code.image.search.surf.Matcher;
import net.ambulando.code.image.search.utils.ImageHelper;
import net.ambulando.code.image.search.utils.ImageUtils;
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
public class InterestPointsSearcher implements ImageSearcher {

	private static final float FIRST_LEVEL = 0.3f;
	private static final float SECOND_LEVEL = 0.6f;

	private ImageHelper imageHelper = new ImageHelper();
	
	private final Map<String, ImageInterestPoints> imagePoints = new TreeMap<String, ImageInterestPoints>();
	
	private String sources;

	private Cache interestPoints;

	private final InterestPointsFinder finder = new InterestPointsFinder();
	
	private final Opener opener = new Opener();
	
	private static boolean useCache = false;

	@Setter
	private Heuristic heuristic;
	
	public Collection<Candidate> search(File file) {
		return search(file, imagePoints.values());
	}
	
	public Collection<Candidate> search(File file, Collection<ImageInterestPoints> imagePointsList) {
		Collection<Candidate> candidates = new TreeSet<Candidate>();
		List<InterestPoint> points = resizeAndFindInterestPoints(file);
		float firstLevel = (float) (FIRST_LEVEL * points.size());
		float secondLevel = (float) (SECOND_LEVEL * points.size());
		Candidate firstBest = null; 
		Candidate secondBest = null;
		for (ImageInterestPoints imagePoints : imagePointsList) {
			final Map<InterestPoint, InterestPoint> matchedPoints = Matcher.findMatches(points, imagePoints.getPoints(), false, heuristic);
			Candidate candidate = new Candidate(imagePoints.getImage(), matchedPoints);
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
				} else if (secondBest==null || candidate.score() > secondBest.score()){
					secondBest = candidate;
				}
			}
			if (candidate.score() < secondLevel) {
				break;
			}
		}
		if (firstBest!=null){
			candidates.add(firstBest);
		}
		if (secondBest!=null){
			candidates.add(secondBest);
		}
		return candidates;
	}


	
	public InterestPointsSearcher(String sources) {
		this(sources, new EuclideanHeuristic());
	}
	
	public InterestPointsSearcher(String sources, Heuristic heuristic) {
		this.sources = new File(sources).getAbsolutePath();
		this.heuristic = heuristic;
		init();
	}
	
	protected void init() {
		List<File> files = imageHelper.getImages(new File(sources));
		if (useCache) {
			System.setProperty("net.sf.ehcache.enableShutdownHook","true");
			interestPoints = CacheManager.getInstance().getCache("interestPoints");
			try {
				for (File file : files) {
					loadImageInterestPointsFromCache(file);
				}
			} finally {
				CacheManager.getInstance().shutdown();			
			}
		} else {
			for (File file : files) {
				List<InterestPoint> points = resizeAndFindInterestPoints(file);
				imagePoints.put(file.getAbsolutePath(), new ImageInterestPoints(file, points));
			}
		}
	}
	
	private void loadImageInterestPointsFromCache(File file) {
		ImageInterestPoints imageInterestPoints = null;
		Element element = interestPoints.get(file.getAbsolutePath());
		if(element != null) {
			log.debug("file {} was found in cache", file.getAbsolutePath());
			imageInterestPoints = (ImageInterestPoints)element.getObjectValue();
		} else {
			log.debug("file {} was NOT found in cache", file.getAbsolutePath());
			List<InterestPoint> points = resizeAndFindInterestPoints(file);
			imageInterestPoints = new ImageInterestPoints(file, points);			
			interestPoints.put(new Element(file.getAbsolutePath(), imageInterestPoints));
			interestPoints.flush();
		}
		imagePoints.put(file.getAbsolutePath(), imageInterestPoints);
	}
	
	protected List<InterestPoint> resizeAndFindInterestPoints(File file) {
		ImagePlus image = opener.openImage(file.getAbsolutePath());
		try {
			image = ImageUtils.resize(600, image);
		} catch (Exception e) {
			log.error("error while resizing", e);
		}
		return findInterestPoints(image);
	}

	private List<InterestPoint> findInterestPoints(ImagePlus image) {
		return finder.findInterestingPoints(image.getProcessor());
	}

	public static void setUseCache(boolean b) {
		useCache = b;
	}

	@Override
	public String toString() {
		return 	Objects.toStringHelper(this.getClass())
				.add("sources", sources)
				.add("images", imagePoints.size()).toString();
	}

	@Override
	public void reload() {
		init();
	}
}
