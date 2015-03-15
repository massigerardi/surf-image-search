/**
 * 
 */
package net.ambulando.image.search.surf;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.ambulando.image.search.Candidate;
import net.ambulando.image.search.heuristic.ChebyshevHeuristic;
import net.ambulando.image.search.heuristic.EuclideanHeuristic;
import net.ambulando.image.search.heuristic.ManhattanHeuristic;
import net.ambulando.image.search.heuristic.SquaredEuclideanHeuristic;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @author mgerardi
 *
 */
@Slf4j
public class SurfImageSearcherTest {

    private enum TestImage {
        
        IMAGE_A("elena-1.jpg", "elena-1.jpeg,elena-1b.jpeg"),
        IMAGE_B("DG4X0095.jpg", "DG4X0095.jpg,DG4X0095B.jpg"),
        IMAGE_C("elena-3.jpeg", "elena-3.jpeg"),
        IMAGE_D("1970Courtroom.jpg", null);
        
        String src;
        String expectedResults;
        private TestImage(String src, String expectedResults) {
            this.src = src;
            this.expectedResults = expectedResults;
        }
    }

    private final static String SOURCES = "src/test/resources/images";
    private final static String SEARCH = "src/test/resources/search-images";

    private static SurfImageSearcher searcher;

    @Before
    public void before() {
        if (searcher == null) {
            Stopwatch stopwatch = new Stopwatch().start();
            searcher = new SurfImageSearcher(SOURCES);
            log.debug("Time to init searcher: {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @Test
    public void testSearchEuclideanHeuristicA() {
        log.debug("EuclideanHeuristic");
        searcher.setHeuristic(new EuclideanHeuristic());
        search(TestImage.IMAGE_A);
    }

    @Test
    public void testSearchEuclideanHeuristicB() {
        log.debug("EuclideanHeuristic");
        searcher.setHeuristic(new EuclideanHeuristic());
        search(TestImage.IMAGE_B);
    }

    @Test
    public void testSearchEuclideanHeuristicC() {
        log.debug("EuclideanHeuristic");
        searcher.setHeuristic(new EuclideanHeuristic());
        search(TestImage.IMAGE_C);
    }

    @Test
    public void testSearchEuclideanHeuristicD() {
        log.debug("EuclideanHeuristic");
        searcher.setHeuristic(new EuclideanHeuristic());
        search(TestImage.IMAGE_D);
    }

    @Test
    public void testSearchSquaredEuclideanHeuristicA() {
        log.debug("SquaredEuclideanHeuristic");
        searcher.setHeuristic(new SquaredEuclideanHeuristic());
        search(TestImage.IMAGE_A);
    }

    @Test
    public void testSearchSquaredEuclideanHeuristicB() {
        log.debug("SquaredEuclideanHeuristic");
        searcher.setHeuristic(new SquaredEuclideanHeuristic());
        search(TestImage.IMAGE_B);
    }

    @Test
    public void testSearchSquaredEuclideanHeuristicC() {
        log.debug("SquaredEuclideanHeuristic");
        searcher.setHeuristic(new SquaredEuclideanHeuristic());
        search(TestImage.IMAGE_C);
    }

    @Test
    public void testSearchSquaredEuclideanHeuristicD() {
        log.debug("SquaredEuclideanHeuristic");
        searcher.setHeuristic(new SquaredEuclideanHeuristic());
        search(TestImage.IMAGE_D);
    }

    @Test
    public void testSearchManhattanHeuristicA() {
        log.debug("ManhattanHeuristic");
        searcher.setHeuristic(new ManhattanHeuristic());
        search(TestImage.IMAGE_A);
    }

    @Test
    public void testSearchManhattanHeuristicB() {
        log.debug("ManhattanHeuristic");
        searcher.setHeuristic(new ManhattanHeuristic());
        search(TestImage.IMAGE_B);
    }

    @Test
    public void testSearchManhattanHeuristicC() {
        log.debug("ManhattanHeuristic");
        searcher.setHeuristic(new ManhattanHeuristic());
        search(TestImage.IMAGE_C);
    }

    @Test
    public void testSearchManhattanHeuristicD() {
        log.debug("ManhattanHeuristic");
        searcher.setHeuristic(new ManhattanHeuristic());
        search(TestImage.IMAGE_D);
    }

    @Test
    public void testSearchChebyshevHeuristicA() {
        log.debug("ChebyshevHeuristic");
        searcher.setHeuristic(new ChebyshevHeuristic());
        search(TestImage.IMAGE_A);
    }

    @Test
    public void testSearchChebyshevHeuristicB() {
        log.debug("ChebyshevHeuristic");
        searcher.setHeuristic(new ChebyshevHeuristic());
        search(TestImage.IMAGE_B);
    }

    @Test
    public void testSearchChebyshevHeuristicC() {
        log.debug("ChebyshevHeuristic");
        searcher.setHeuristic(new ChebyshevHeuristic());
        search(TestImage.IMAGE_C);
    }

    @Test
    public void testSearchChebyshevHeuristicD() {
        log.debug("ChebyshevHeuristic");
        searcher.setHeuristic(new ChebyshevHeuristic());
        search(TestImage.IMAGE_D);
    }

    private void search(TestImage testImage) {
        File src = new File(SEARCH, testImage.src);
        String expected = testImage.expectedResults;
        Stopwatch stopwatch = new Stopwatch().start();
        Collection<Candidate> candidates = searcher.search(src);
        log.debug("Time for search: {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        log.debug("candidates " + candidates);
        Assert.assertNotNull(candidates);
        boolean hasMatches = expected != null;
        Assert.assertEquals(hasMatches, !candidates.isEmpty());
        if (hasMatches) {
            String[] images = StringUtils.split(expected, ',');
            Assert.assertTrue("Candidates: " + candidates + "; expected: "+expected, candidates.size() >= images.length);
            boolean found = true;
            for (Iterator<Candidate> iterator = candidates.iterator(); iterator
                    .hasNext();) {
                Candidate candidate = iterator.next();
                found = expected.contains(candidate.getImage().getName()) && found;
                if (!found) {
                    break;
                }
            }
            Assert.assertEquals("Candidates: " + candidates, hasMatches, found);
        }
    }

    @Test
    public void testResizeAndFindInterestPoints() throws Exception {
        File file = new File(SOURCES, "elena-1.jpeg");
        Stopwatch stopwatch = new Stopwatch().start();
        ImageDescriptor descriptor = searcher.resizeAndFindInterestPoints(file);
        log.debug("Time for search: {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        Assert.assertNotNull(descriptor);
        Assert.assertFalse(descriptor.getPoints().isEmpty());
        log.debug("[testResizeAndFindInterestPointsWithStepOne] found {} points", descriptor.getPoints().size());
    }

}
