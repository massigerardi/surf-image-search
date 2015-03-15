/**
 * 
 */
package net.ambulando.code.image.search.surf.ip;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.ambulando.image.search.surf.ip.InterestPoint;
import net.ambulando.image.search.surf.ip.InterestPointsFinder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @author massi
 *
 */
@Slf4j
public class InterestPointsFinderTest {

    InterestPointsFinder finder;
    
    ImageProcessor processor;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        finder = new InterestPointsFinder();
        ImagePlus imagePlus = new Opener().openImage("src/test/resources/search-images/DG4X0095.jpg");
        processor = imagePlus.getProcessor();
    }

    @Test
    public void testFindInterestingPointsImageProcessorNormalized() throws Exception {
        Stopwatch stopwatch = new Stopwatch().start();
        List<InterestPoint> points = finder.findInterestingPoints(processor, true);
        log.debug("A.Execution time: {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        Assert.assertNotNull(points);
        Assert.assertFalse(points.isEmpty());
        log.debug("A.found {} points", points.size());
    }

    @Test
    public void testFindInterestingPointsImageProcessor() throws Exception {
        Stopwatch stopwatch = new Stopwatch().start();
        List<InterestPoint> points = finder.findInterestingPoints(processor, false);
        log.debug("A1.Execution time: {} ms", stopwatch.stop().elapsed(TimeUnit.MILLISECONDS));
        Assert.assertNotNull(points);
        Assert.assertFalse(points.isEmpty());
        log.debug("A1.found {} points", points.size());
    }

}
