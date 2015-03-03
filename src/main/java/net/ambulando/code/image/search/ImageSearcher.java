/**
 * 
 */
package net.ambulando.code.image.search;

import java.io.File;
import java.util.Collection;

/**
 * @author massi
 *
 */
public interface ImageSearcher {

	Collection<Candidate> search(File image);
	
	void reload();
}
