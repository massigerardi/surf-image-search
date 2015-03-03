/**
 * 
 */
package net.ambulando.code.image.search.surf.ip;

import ij.process.ImageProcessor;

import java.util.List;

import net.ambulando.code.image.search.surf.Detector;
import net.ambulando.code.image.search.surf.IntegralImage;
import net.ambulando.code.image.search.surf.Settings;


/**
 * @author massi
 *
 */
public class InterestPointsFinder {

	public List<InterestPoint> findInterestingPoints(ImageProcessor processor) {
		
		IntegralImage image = new IntegralImage(processor, true);
		
		// Detect interest points with Fast-Hessian
		List<InterestPoint> ipts = Detector.fastHessian(image, Settings.getSettings());
		
//		float[] strengthOfIPs = new float[ipts.size()];
//		for (int i = 0; i < ipts.size(); i++) {
//			strengthOfIPs[i] = ipts.get(i).strength;
//		}
//		Arrays.sort(strengthOfIPs);
		
		// Describe interest points with SURF-descriptor
//		for (InterestPoint ipt: ipts)
//			Descriptor.computeAndSetOrientation(ipt, image);
//		for (InterestPoint ipt: ipts)
//			Descriptor.computeAndSetDescriptor(ipt, image, Settings.getSettings());

		return ipts;

	}
	
	
}
