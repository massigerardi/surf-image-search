/**
 * 
 */
package net.ambulando.image.search.heuristic;

import net.ambulando.image.search.heuristic.Heuristic;

/**
 * @author mgerardi
 *
 */
public class SquaredEuclideanHeuristic implements Heuristic {

    @Override
    public float distance(float[] p1, float[] p2) {
        float distance = 0f;
        for (int i = 0; i < p1.length; i++) {
            float d = p2[i] - p1[i];
            distance += d * d;
        }
        return distance;
    }

}
