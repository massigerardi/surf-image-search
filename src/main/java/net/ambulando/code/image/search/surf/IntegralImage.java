package net.ambulando.code.image.search.surf;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;
import lombok.Getter;

@Getter
public class IntegralImage {

	private float[][] data;
	
	/** Sets internal array to the argument <code>a</code> and updates attributes width, height, maxX, maxY. */
	private void setData(float[][] a) {
		data = a;
		width = data.length;
		height = data[0].length;
		maxX = width - 1;
		maxY = height - 1;
	}
	
	private int width;
	private int height;
	/** Max valid X coordinate. */
	private int maxX;
	/** Max valid Y coordinate. */
	private int maxY;

	public float get(int x, int y) {
		return data[x][y];
	}

	/** Compute the integral image. */
	private void convertInternalBufferToIntegralImage() {
		float rowSum = 0;

		// first row:
		for (int x = 0; x < width; x++) {
			rowSum += data[x][0];
			data[x][0] = rowSum;
		}

		// the rest:
		for (int y = 1; y < height; y++) {
			rowSum = 0;
			for (int x = 0; x < width; x++) {
				rowSum += data[x][y];
				data[x][y] = rowSum + data[x][y - 1];
			}
		}
	}

	public IntegralImage(ImageProcessor src, boolean weightedAndNormalizedConversion) {
		// TODO: make weightedAndNormalizedConversion the default and remove other constructors! 
		int width = src.getWidth();
		int height = src.getHeight();
		float[][] a = new float[width][height];
		float val, min = Float.MAX_VALUE, max = Float.MIN_VALUE;
		int i, x, y;
		float[] col;

		// Convert to float and compute min and max values
		
		if (src instanceof ByteProcessor || src instanceof ShortProcessor || src instanceof FloatProcessor) {
			for (i = 0, y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
					val = src.getf(i++);
					a[x][y] = val; 
					if      (val < min) min = val;
					else if (val > max)	max = val;
				}
			}

		} else if (src instanceof ColorProcessor) { // weighted conversion
			int intVal, r, g, b;
			float rw = 0.299f, gw = 0.587f, bw = 0.114f;

			for (i = 0, y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
					
					intVal = src.get(i++);
					r = (intVal & 0xff0000) >> 16;
					g = (intVal & 0xff00) >> 8;
					b = intVal & 0xff;
					val = r * rw + g * gw + b * bw;
						
					a[x][y] = val; 
					if      (val < min) min = val;
					else if (val > max)	max = val;
				}
			}
			
		} else {			// Should never happen.
			return;
		}
		
		
		// Normalize values (i.e. scale max-min range to 0..1 range)
		
		float scale = 1f/(max-min);
		for (i = 0, x = 0; x < width; x++) {
			col = a[x];
			for (y = 0; y < height; y++) {
				val = col[y] - min;
				if (val < 0) val = 0;
				val *= scale;
				if (val > 1) val = 1;
				col[y] = val;
			}
		}
		
		setData(a);
		
		convertInternalBufferToIntegralImage();

	}



	/**
	 * Computes the sum of pixels in an integral image <code>img</code> within
	 * the rectangle specified by the top-left start coordinate (inclusive) and
	 * size.<br>
	 */
	float area(int x1, int y1, int rectWidth, int rectHeight) {
		x1--; y1--;                  //  A +--------+ B
		int x2 = x1 + rectWidth;     //    |        |        A(x1,y1)
		int y2 = y1 + rectHeight;    //  C +--------+ D

		// bounds check
		if (x1 > maxX) x1 = maxX;
		if (y1 > maxY) y1 = maxY;
		if (x2 > maxX) x2 = maxX;
		if (y2 > maxY) y2 = maxY;

		float A = (x1 < 0 || y1 < 0) ? 0 : data[x1][y1];
		float B = (x2 < 0 || y1 < 0) ? 0 : data[x2][y1];
		float C = (x1 < 0 || y2 < 0) ? 0 : data[x1][y2];
		float D = (x2 < 0 || y2 < 0) ? 0 : data[x2][y2];

		return D - B - C + A;

	}

	/** A speed optimized version of {@link #area(FloatProcessor, int, int, int, int)} 
	 * without bounds check (for 0 < x1 < width and 0 < y1 < height). */
	float area2(int x1, int y1, int rectWidth, int rectHeight) {
		x1--; y1--;                  //  A +--------+ B
		int x2 = x1 + rectWidth;     //    |        |        A(x1,y1)
		int y2 = y1 + rectHeight;    //  C +--------+ D
		return data[x2][y2] - data[x2][y1] - data[x1][y2] + data[x1][y1]; // D - B - C + A 
	}






}
