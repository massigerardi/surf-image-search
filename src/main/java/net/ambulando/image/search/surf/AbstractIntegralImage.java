/**
 * 
 */
package net.ambulando.image.search.surf;

import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

/**
 * @author massi
 *
 */
@Getter
public class AbstractIntegralImage implements IntegralImage{

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + height;
        result = prime * result + Arrays.hashCode(originalData);
        result = prime * result + width;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractIntegralImage other = (AbstractIntegralImage) obj;
        if (!Arrays.deepEquals(data, other.data))
            return false;
        if (height != other.height)
            return false;
        if (!Arrays.deepEquals(originalData, other.originalData))
            return false;
        if (width != other.width)
            return false;
        return true;
    }

    @Setter
    private float[][] data;
    @Setter
    private float[][] originalData;
    private int width;
    private int height;

    public float get(int x, int y) {
        return data[x][y];
    }

    public void set(int x, int y, float value) {
        this.data[x][y] = value;
    }

    public AbstractIntegralImage(int width, int height) {
        super();
        this.data = new float[width][height];
        this.width = width;
        this.height = height;
    }

    public static float[][] convert(float[][] data, int w, int h) {
        float[][] result = new float[w][h];
        for (int x = 0; x < w; x++) {
            float sum = 0;
            for (int y = 0; y < h; y++) {
                sum += data[x][y];
                result[x][y] = value(x, y, sum, result);
            }
        }
        return result;
    }

    protected static float value(int x, int y, float val, float[][] data) {
        if (x == 0) {
            return val;
        }
        return val + data[x - 1][y];
    }
    
    /**
     * Computes the sum of pixels in an integral image <code>img</code> within
     * the rectangle specified by the top-left start coordinate (inclusive) and
     * size.<br>
     */
    public float area(int x1, int y1, int rectWidth, int rectHeight) {
        x1--;
        y1--; 
        int x2 = x1 + rectWidth;
        int y2 = y1 + rectHeight;

        // bounds check
        if (x1 >= width)
            return 0;
        if (y1 >= height)
            return 0;
        if (x2 >= width)
            return 0;
        if (y2 >= height)
            return 0;

        float A = (x1 < 0 || y1 < 0) ? 0 : data[x1][y1];
        float B = (x2 < 0 || y1 < 0) ? 0 : data[x2][y1];
        float C = (x1 < 0 || y2 < 0) ? 0 : data[x1][y2];
        float D = (x2 < 0 || y2 < 0) ? 0 : data[x2][y2];

        return D - B - C + A;
    }

}
