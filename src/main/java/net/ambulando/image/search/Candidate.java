/**
 * 
 */
package net.ambulando.image.search;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.ambulando.image.search.surf.ip.InterestPoint;

import com.google.common.collect.ComparisonChain;

/**
 * @author massi
 *
 */
@Getter
@AllArgsConstructor
public class Candidate implements Comparable<Candidate> {

	File image;
	
	final Map<InterestPoint, InterestPoint> matchedPoint;

	public int score() {
		return matchedPoint.size();
	}

	
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Candidate other) {
		return ComparisonChain.start().compare(other.score(), this.score()).result();
	}



	@Override
	public String toString() {
		return MessageFormat.format("Candidate({0},{1})", this.image.getName(), this.matchedPoint.size());
	}

}
