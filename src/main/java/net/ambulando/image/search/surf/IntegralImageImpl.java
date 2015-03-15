package net.ambulando.image.search.surf;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import lombok.Getter;

@Getter
public class IntegralImageImpl extends AbstractIntegralImage {

    public IntegralImageImpl(ImageProcessor src,
            boolean weightedAndNormalizedConversion) {
        super(src.getWidth(), src.getHeight());
        float[][] a = new float[getWidth()][getHeight()];
        float val, min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        float[] col;

        if (src instanceof ByteProcessor || src instanceof ShortProcessor
                || src instanceof FloatProcessor) {
            for (int x = 0; x < getWidth(); x++) {
                float sum = 0;
                for (int y = 0; y < getHeight(); y++) {
                    val = src.getf(x, y);
                    a[x][y] = val;
                    if (val < min)
                        min = val;
                    else if (val > max)
                        max = val;
                    sum += val;
                    set(x, y, value(x, y, sum, getData()));
                }
            }

        } else if (src instanceof ColorProcessor) { // weighted conversion
            int intVal, r, g, b;
            float rw = 0.299f, gw = 0.587f, bw = 0.114f;
            for (int x = 0; x < getWidth(); x++) {
                float sum = 0;
                for (int y = 0; y < getHeight(); y++) {
                    intVal = src.get(x, y);
                    r = (intVal & 0xff0000) >> 16;
                    g = (intVal & 0xff00) >> 8;
                    b = intVal & 0xff;
                    val = r * rw + g * gw + b * bw;
                    a[x][y] = val;
                    if (val < min)
                        min = val;
                    else if (val > max)
                        max = val;
                    sum += val;
                    set(x, y, value(x, y, sum, getData()));
                }
            }
            setOriginalData(a);
        } else { // Should never happen.
            return;
        }
        if (weightedAndNormalizedConversion) {
            // Normalize values (i.e. scale max-min range to 0..1 range)
            float scale = 1f / (max - min);
            for (int x = 0; x < getWidth(); x++) {
                col = a[x];
                for (int y = 0; y < getHeight(); y++) {
                    val = col[y] - min;
                    if (val < 0)
                        val = 0;
                    val *= scale;
                    if (val > 1)
                        val = 1;
                    col[y] = val;
                }
            }
            setData(convert(a, getWidth(), getHeight()));
        }
    }



}
