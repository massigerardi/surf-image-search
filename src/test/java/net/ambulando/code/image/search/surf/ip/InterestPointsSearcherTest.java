/**
 * 
 */
package net.ambulando.code.image.search.surf.ip;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.ambulando.code.image.search.Candidate;
import net.ambulando.code.image.search.heuristic.ChebyshevHeuristic;
import net.ambulando.code.image.search.heuristic.EuclideanHeuristic;
import net.ambulando.code.image.search.heuristic.ManhattanHeuristic;
import net.ambulando.code.image.search.surf.Detector;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @author mgerardi
 *
 */
@Slf4j
public class InterestPointsSearcherTest {

	private final static String SOURCES = "src/test/resources/images";
	private final static String SEARCH = "src/test/resources/search-images";
	private final static String IMAGE_A = "DG4X0095.jpg";
	private final static String IMAGE_B = "elena-1.jpg";
	private final static String IMAGE_C = "1970Courtroom.jpg";
	private final static String EXPECTED_A = "DG4X0095.jpg,DG4X0095B.jpg";
	private final static String EXPECTED_B = "elena-1.jpeg,elena-1b.jpeg";
	private final static String IMAGE_D = "elena-3.jpeg";
	private final static String EXPECTED_D = "elena-3.jpeg";

	InterestPointsSearcher searcher = new InterestPointsSearcher(SOURCES);
	
	@Test
	public void testSearchA() {
		searcher.setHeuristic(new EuclideanHeuristic());
		search();
	}

	@Test
	public void testSearchB() {
		searcher.setHeuristic(new ManhattanHeuristic());
		search();
	}

	@Test
	public void testSearchC() {
		searcher.setHeuristic(new ChebyshevHeuristic());
		search();
	}

	private void search() {
		search(new File(SEARCH, IMAGE_A), EXPECTED_A);
		search(new File(SEARCH, IMAGE_B), EXPECTED_B);
		search(new File(SEARCH, IMAGE_D), EXPECTED_D);
		search(new File(SEARCH, IMAGE_C), null);
		
	}

	private void search(File src, String expected) {
		Stopwatch stopwatch = new Stopwatch().start();
		Collection<Candidate> candidates = searcher.search(src);
		log.debug("Time for search: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
		log.debug("candidates "+candidates);
		Assert.assertNotNull(candidates);
		boolean hasMatches = expected!=null;
		Assert.assertEquals(hasMatches, !candidates.isEmpty());
		if (hasMatches) {
			boolean found = true;
			for (Iterator<Candidate> iterator = candidates.iterator(); iterator.hasNext();) {
				Candidate candidate = iterator.next();
				found = found && expected.contains(candidate.getImage().getName());
				if (!found) {
					break;
				}
			}
			Assert.assertEquals("Candidates: "+candidates, hasMatches, found);
		}
	}

//	@Test
	public void testResizeAndFindInterestPointsWithStepOne() {
		File file = new File(SOURCES, "elena-1.jpeg");
		Stopwatch stopwatch = new Stopwatch().start();
		List<InterestPoint> points = searcher.resizeAndFindInterestPoints(file);
		log.debug("[testResizeAndFindInterestPointsWithStepOne]Time to find interesting points: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
		log.debug("[testResizeAndFindInterestPointsWithStepOne]found " +points.size() + " points" );
		Assert.assertNotNull(points);
		Assert.assertFalse(points.isEmpty());
	}
	
//	@Test
	public void testResizeAndFindInterestPointsWithStepTwo() {
		Detector.SetInitStep(2);
		File file = new File(SOURCES, "elena-1.jpeg");
		Stopwatch stopwatch = new Stopwatch().start();
		List<InterestPoint> points = searcher.resizeAndFindInterestPoints(file);
		log.debug("[testResizeAndFindInterestPointsWithStepTwo]Time to find interesting points: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
		log.debug("[testResizeAndFindInterestPointsWithStepTwo]found " +points.size() + " points" );
		Assert.assertNotNull(points);
		Assert.assertFalse(points.isEmpty());
	}
	
}
