/**
 * 
 */
package net.ambulando.code.image.search.heuristic;

/**
 * @author mgerardi
 *
 */
public class ManhattanHeuristic implements Heuristic {

	/* (non-Javadoc)
	 * @see net.ambulando.code.image.search.heuristic.Heuristic#distance(float[], float[])
	 */
	@Override
	public float distance(float[] p1, float[] p2) {
		float distance = 0f;
		for (int i = 0; i < p1.length; i++) {
			distance += Math.abs(p2[i] - p1[i]);
		}
		return distance;
	}

}
