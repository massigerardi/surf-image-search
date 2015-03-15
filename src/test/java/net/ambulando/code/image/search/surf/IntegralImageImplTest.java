/**
 * 
 */
package net.ambulando.code.image.search.surf;

import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import net.ambulando.image.search.surf.IntegralImageImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Stopwatch;

/**
 * @author massi
 *
 */
@Slf4j
public class IntegralImageImplTest {

    ImageProcessor src;
    
    @Before
    public void setUp() {
        Opener opener = new Opener();
        ImagePlus image = opener.openImage("src/test/resources/images/elena-1.jpeg");
        src = image.getProcessor();
    }

    @Test
    public void testNewIntegralImageNormalized() {
        Stopwatch stopwatch = new Stopwatch().start();
        IntegralImageImpl image = new IntegralImageImpl(src, true);
        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        Assert.assertNotNull(image);
        log.debug("testNewIntegralImageNormalized \t- Execution time: {} ms", elapsedTime);
    }

    @Test
    public void testNewIntegralImageNotNormalized() {
        Stopwatch stopwatch = new Stopwatch().start();
        IntegralImageImpl image = new IntegralImageImpl(src, false);
        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        Assert.assertNotNull(image);
        log.debug("testNewIntegralImageNotNormalized \t- Execution time: {} ms", elapsedTime);
    }

    void print(float[][] data) {
        StringBuffer buffer = new StringBuffer();
        for (int x = 0; x < data.length; x++) {
            float[] row = data[x];
            buffer.append("{");
            for (int y = 0; y < row.length; y++) {
                float v = row[y];
                buffer.append(v).append(", ");
            }
            buffer.append("}\n");
        }
        log.debug("\n"+buffer.toString());
    }
    
    @Test
    public void testConvert() {
        float[][] data = new float[5][5];
        data[0] = new float[] {1, 2, 2, 4, 1};
        data[1] = new float[] {3, 4, 1, 5, 2};
        data[2] = new float[] {2, 3, 3, 2, 4};
        data[3] = new float[] {4, 1, 5, 4, 6};
        data[4] = new float[] {6, 3, 2, 1, 3};
        float[][] expected = new float[5][5];
        expected[0] = new float[] {1, 3, 5, 9, 10, };
        expected[1] = new float[] {4, 10, 13, 22, 25, };
        expected[2] = new float[] {6, 15, 21, 32, 39, };
        expected[3] = new float[] {10, 20, 31, 46, 59, };
        expected[4] = new float[] {16, 29, 42, 58, 74, };
        float[][] result = IntegralImageImpl.convert(data, 5, 5);
        Assert.assertArrayEquals(expected, result);
    }
}
