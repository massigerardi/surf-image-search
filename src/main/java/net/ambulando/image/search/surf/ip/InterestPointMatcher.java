package net.ambulando.image.search.surf.ip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ambulando.image.search.heuristic.Heuristic;
/**
 * a Matcher for interest points.
 * 
 * 
 * @author Massimiliano Gerardi
 * Mar 12, 2015
 */
public class InterestPointMatcher {

	public static class Point2D {
		public int x, y;

		public Point2D() {}

		public Point2D(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static class Point2Df {
		public float x, y;

		public Point2Df() {}

		public Point2Df(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}

	public static Map<InterestPoint, InterestPoint> findMatches(List<InterestPoint> ipts1, List<InterestPoint> ipts2, boolean doReverseComparisonToo, Heuristic heuristic) {

		Map<InterestPoint, InterestPoint> matchedPoints = InterestPointMatcher.findMathes(ipts1, ipts2, heuristic);

		if (doReverseComparisonToo) {
			Map<InterestPoint, InterestPoint> matchedPointsReverse = InterestPointMatcher.findMathes(ipts2, ipts1, heuristic);

			// take only those points that matched in the reverse comparison too
			Map<InterestPoint, InterestPoint> matchedPointsBoth = new HashMap<InterestPoint, InterestPoint>();
			for (InterestPoint ipt1 : matchedPoints.keySet()) {
				InterestPoint ipt2 = matchedPoints.get(ipt1);
				if (ipt1 == matchedPointsReverse.get(ipt2))
					matchedPointsBoth.put(ipt1, ipt2);
			}
			matchedPoints = matchedPointsBoth;
		}
		return matchedPoints;
	}

	/**
	 * 
	 * @param ipts1
	 * @param ipts2
	 * @param heuristic
	 * @return
	 */
	public static Map<InterestPoint, InterestPoint> findMathes(List<InterestPoint> ipts1, List<InterestPoint> ipts2, Heuristic heuristic) {
		Map<InterestPoint, InterestPoint> res = new HashMap<InterestPoint, InterestPoint>();
		float distance, bestDistance, secondBest;
		InterestPoint bestMatch;

		for (InterestPoint p1 : ipts1) {
			bestDistance = secondBest = Float.MAX_VALUE;
			bestMatch = null;

			intLoop: for (InterestPoint p2 : ipts2) {

				// (NB: There is no check fo sign of laplacian in OpenSURF)
				if (p1.sign != p2.sign)
					continue;

				// Compare descriptors (based on calculating of squared distance between two vectors)
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

			
			// Threshold values in other implementations:
			// OpenSURF:                    0.65
			// OpenCV-2.0.0 (find_obj.cpp): 0.6
			// Orig. SURF:                  0.5
			
			if (bestDistance < 0.5f * secondBest) {
				
				// Matching point found.
				res.put(p1, bestMatch);
				// Store the change in position (p1 -> p2) into the
				// matchingPoint:
				bestMatch.dx = bestMatch.x - p1.x;
				bestMatch.dy = bestMatch.y - p1.y;

				// debug
				// System.out.printf("%3d)  %5.1f, %5.1f   (%7.5f)   %5.1f, %5.1f\n",
				// res.size(), p1.x, p1.y, bestDistance, bestMatch.x, bestMatch.y);
				// System.out.println(bestDistance);
			}
		}

		return res;
	}

}
