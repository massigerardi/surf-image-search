/**
 * 
 */
package net.ambulando.code.image.search.surf.ip;

import ij.process.ImageProcessor;

import java.util.List;

import net.ambulando.code.image.search.surf.Descriptor;
import net.ambulando.code.image.search.surf.Detector;
import net.ambulando.code.image.search.surf.IntegralImage;


/**
 * @author massi
 *
 */
public class InterestPointsFinder {

	public List<InterestPoint> findInterestingPoints(ImageProcessor processor) {
		
		IntegralImage image = new IntegralImage(processor, true);
		
		// Detect interest points with Fast-Hessian
		List<InterestPoint> ipts = Detector.fastHessian(image);
		
		// Describe interest points with SURF-descriptor
		for (InterestPoint ipt: ipts)
			Descriptor.computeAndSetOrientation(ipt, image);
		for (InterestPoint ipt: ipts)
			Descriptor.computeAndSetDescriptor(ipt, image);

		return ipts;

	}
	
	
}
