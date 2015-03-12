/**
 * 
 */
package net.ambulando.image.search.surf;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import net.ambulando.image.search.surf.ip.InterestPoint;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.google.common.base.Objects;

/**
 * An imag Descriptor to keep track of image and its ip.
 * @author Massimiliano Gerardi
 * Mar 12, 2015
 */
@Getter
@AllArgsConstructor
public class ImageDescriptor implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 835075804493712156L;

	private File image;
	
	private List<InterestPoint> points;

	@Override
	public String toString() {
		return Objects.toStringHelper(getClass())
				.add("image", image.getAbsolutePath())
				.add("points", points.size())
				.toString();
	}
	
	
}
