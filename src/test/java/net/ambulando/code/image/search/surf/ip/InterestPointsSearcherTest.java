/**
 * 
 */
package net.ambulando.code.image.search.surf.ip;

import org.junit.Assert;

import lombok.extern.slf4j.Slf4j;
import net.ambulando.code.image.search.Candidate;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import java.util.Iterator;
import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @author mgerardi
 *
 */
@Slf4j
public class InterestPointsSearcherTest {

	InterestPointsSearcher searcher;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		InterestPointsSearcher.setUseCache(false);
		String sources = "src/test/resources/images";
		Stopwatch stopwatch = new Stopwatch().start();
		searcher = new InterestPointsSearcher(sources);
		log.debug("Time for init: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
	}

	@Test
	public void testSearch() {
		File src = new File("src/test/resources/search-images/DG4X0099.jpg");
		Stopwatch stopwatch = new Stopwatch().start();
		Collection<Candidate> candidates = searcher.search(src);
		log.debug("Time for search: " +stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) +" ms" );
		Assert.assertNotNull(candidates);
		Assert.assertFalse(candidates.isEmpty());
		boolean found = false;
		for (Iterator<Candidate> iterator = candidates.iterator(); iterator.hasNext();) {
			Candidate candidate = iterator.next();
			found = candidate.getImage().getName().startsWith("DG4X0099");
			if (found) {
				break;
			}
		}
		Assert.assertTrue(found);
	}

}
