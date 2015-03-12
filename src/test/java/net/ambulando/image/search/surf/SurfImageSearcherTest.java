/**
 * 
 */
package net.ambulando.image.search.surf;

import ij.io.Opener;
import ij.process.ImageProcessor;

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
import net.ambulando.image.search.surf.ip.InterestPointDrawer;
import net.ambulando.image.search.surf.ip.InterestPointsFinder;
import net.ambulando.image.search.utils.ImageDisplay;

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

	private final static String SOURCES = "src/test/resources/images";
	private final static String SEARCH = "src/test/resources/search-images";
	private final static String IMAGE_A = "DG4X0095.jpg";
	private final static String IMAGE_B = "elena-1.jpg";
	private final static String IMAGE_C = "1970Courtroom.jpg";
	private final static String EXPECTED_A = "DG4X0095.jpg,DG4X0095B.jpg";
	private final static String EXPECTED_B = "elena-1.jpeg,elena-1b.jpeg";
	private final static String IMAGE_D = "elena-3.jpeg";
	private final static String EXPECTED_D = "elena-3.jpeg";

	private static SurfImageSearcher searcher;

	@Before
	public void before() {
		if (searcher == null) {
			Stopwatch stopwatch = new Stopwatch().start();
			searcher = new SurfImageSearcher(SOURCES);
			log.debug("Time to init searcher: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
		}

	}
	
	@Test
	public void testSearch() {
		log.debug("EuclideanHeuristic");
		searcher.setHeuristic(new EuclideanHeuristic());
		search();
	}

	@Test
	public void testSearchA() {
		log.debug("SquaredEuclideanHeuristic with initial step = 1");
		searcher.setHeuristic(new SquaredEuclideanHeuristic());
		search();
	}

	@Test
	public void testSearchA2() {
		log.debug("SquaredEuclideanHeuristic with initial step = 2");
		InterestPointsFinder.SetInitStep(2);
		SurfImageSearcher searcher = new SurfImageSearcher(SOURCES);
		searcher.setHeuristic(new SquaredEuclideanHeuristic());
		search();
	}

	@Test
	public void testSearchB() {
		log.debug("ManhattanHeuristic");
		searcher.setHeuristic(new ManhattanHeuristic());
		search();
	}

	@Test
	public void testSearchC() {
		log.debug("ChebyshevHeuristic");
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

	@Test
	public void testResizeAndFindInterestPointsWithStepOne() throws Exception {
		File file = new File(SOURCES, "elena-1.jpeg");
		Stopwatch stopwatch = new Stopwatch().start();
		ImageDescriptor descriptor = searcher.resizeAndFindInterestPoints(file);
		log.debug("[testResizeAndFindInterestPointsWithStepOne]Time to find interesting points: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
		Assert.assertNotNull(descriptor);
		Assert.assertFalse(descriptor.getPoints().isEmpty());
		log.debug("[testResizeAndFindInterestPointsWithStepOne]found " +descriptor.getPoints().size() + " points" );
		display(descriptor);
	}
	
	@Test
	public void testResizeAndFindInterestPointsWithStepTwo() throws Exception {
		InterestPointsFinder.SetInitStep(2);
		File file = new File(SOURCES, "elena-1.jpeg");
		Stopwatch stopwatch = new Stopwatch().start();
		ImageDescriptor descriptor = searcher.resizeAndFindInterestPoints(file);
		log.debug("[testResizeAndFindInterestPointsWithStepOne]Time to find interesting points: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
		Assert.assertNotNull(descriptor);
		Assert.assertFalse(descriptor.getPoints().isEmpty());
		log.debug("[testResizeAndFindInterestPointsWithStepOne]found " +descriptor.getPoints().size() + " points" );
	}

	private void display(ImageDescriptor descriptor)  throws Exception {
		ImageProcessor processor = new Opener().openImage(descriptor.getImage().getAbsolutePath()).getProcessor();
		InterestPointDrawer.drawInterestPoints(processor, descriptor.getPoints());
		ImageDisplay display = new ImageDisplay(processor.getBufferedImage());
		display.display();
		while(display.isShowing()){
			Thread.sleep(1000);
		}

	}
}
