package net.ambulando.image.search.surf.ip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.ambulando.image.search.heuristic.Heuristic;

/**
 * a Matcher for interest points.
 * 
 * 
 * @author Massimiliano Gerardi Mar 12, 2015
 */
@Slf4j
public class InterestPointMatcher {

    /**
     * 
     * @param ipts1
     * @param ipts2
     * @param heuristic
     * @return
     */
    public static Map<InterestPoint, InterestPoint> findMatches(
            List<InterestPoint> ipts1, 
            List<InterestPoint> ipts2,
            Heuristic heuristic) {
        Map<InterestPoint, InterestPoint> res = new HashMap<InterestPoint, InterestPoint>();
        float distance, bestDistance, secondBest;
        InterestPoint bestMatch;

        for (InterestPoint p1 : ipts1) {
            bestDistance = secondBest = Float.MAX_VALUE;
            bestMatch = null;

            intLoop: for (InterestPoint p2 : ipts2) {

                if (p1.sign != p2.sign)
                    continue;
                distance = heuristic.distance(p1.descriptor, p2.descriptor);
                if (distance > bestDistance) {
                    continue intLoop;
                }
                if (distance < bestDistance) {
                    secondBest = bestDistance;
                    bestDistance = distance;
                    bestMatch = p2;
                } else { // distance < secondBest
                    secondBest = distance;
                }
            }
            if (bestDistance < 0.5f * secondBest) {
                res.put(p1, bestMatch);
            }
        }
        return res;
    }

}
