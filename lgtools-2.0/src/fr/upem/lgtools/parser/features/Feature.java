/**
 * 
 */
package fr.upem.lgtools.parser.features;

/**
 * @author Mathieu Constant
 *
 */
public class Feature {
	private final int feat;
	private final double value;
	
	/**
	 * @param attribute
	 * @param value
	 */
	public Feature(int feat, double value) {
		this.feat = feat;
		this.value = value;

	}
	
	public int getFeat() {
		return feat;
	}
	
	

	public double getValue() {
		return value;
	}
	
	
	

}
