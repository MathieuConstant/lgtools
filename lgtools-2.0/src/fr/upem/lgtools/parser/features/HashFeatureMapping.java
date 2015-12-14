/**
 * 
 */
package fr.upem.lgtools.parser.features;

/**
 * @author Mathieu
 *
 */
public class HashFeatureMapping implements FeatureMapping {

	/* (non-Javadoc)
	 * @see fr.upem.lgtools.parser.features.Features#getFeatureId(java.lang.String)
	 */
	@Override
	public int getFeatureId(String feat) {
		/* implementation of string.hashcode() -- optimized  */
		int hashcode = 0;
		 
		for(int i=0;i<feat.length();i++) {
		  //hashcode = 31*hashcode + feat.charAt(i); //non-optimized too much
		  hashcode = (hashcode << 5) - hashcode + feat.charAt(i);
		}
		return hashcode;
	}
	
	

}
