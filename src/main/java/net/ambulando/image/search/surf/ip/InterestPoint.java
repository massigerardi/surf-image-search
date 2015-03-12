package net.ambulando.image.search.surf.ip;

import java.util.Formatter;
import java.util.Locale;

/** Interest Point class. */
public class InterestPoint {

	/** Interpolated X-coordinate. */
	public float x;

	/** Interpolated Y-coordinate. */
	public float y;

	/**
	 * Value of the hessian determinant (blob response) means the strength of
	 * the interest point.
	 */
	public float strength;

	/** Trace of the hessian determinant. */
	float trace;

	/**
	 * Sign of hessian traces (laplacian sign).<br>
	 * <code>true</code> means >= 0, <code>false</code> means < 0. (Signs are
	 * saved separately for better matching performance.)
	 */
	public boolean sign;

	/** Detected scale. */
	public float scale;

	/**
	 * Orientation measured anti-clockwise from +ve x-axis. The default is 0
	 * (i.e. upright SURF).
	 */
	public float orientation;

	/** Vector of descriptor components. */
	public float[] descriptor;

	/** Point motion (can be used for frame to frame motion analysis). */
	public float dx;

	/** Point motion (can be used for frame to frame motion analysis). */
	public float dy;

	public InterestPoint() {
	}

	public InterestPoint(float x, float y, float strength, float trace,
			float scale) {
		this.x = x;
		this.y = y;
		this.strength = strength;
		this.trace = trace;
		this.scale = scale;
		this.sign = (trace >= 0);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb, Locale.US); // (all output will be sent
													// to sb)
		try {

			f.format("%12f %12f %12f %12f %12f %12f ", x, y, strength, trace,
					scale, orientation);
			int descSize = (descriptor == null) ? 0 : descriptor.length;
			f.format("%12d ", descSize);
			if (descSize > 0) {
				for (int i = 0; i < descSize; i++) {
					if (i % 8 == 0)
						f.format("\n"); // 8 numbers per line
					f.format("%12f ", descriptor[i]);
				}
				f.format("\n");
			}
			return f.toString();
		} finally {
			f.close();
		}
	}

}
