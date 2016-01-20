/**
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.Collection;

/**
 * @author Mathieu
 *
 */
public class HashFeatureMapping extends FeatureMapping {
     
     
	
	public HashFeatureMapping(int max) {
		super(max);
	}



	@Override
	public Collection<String> getFeatures() {		
		return null;
	}



	@Override
	public int getFeatureId(String feat, boolean withMemoryIfPossible) {
		/* implementation of string.hashcode() -- optimized  */
		int hashcode = 0;
		 
		for(int i=0;i<feat.length();i++) {
		  //hashcode = 31*hashcode + feat.charAt(i); //non-optimized too much
		  hashcode = (hashcode << 5) - hashcode + feat.charAt(i);
		}

		/* hascode must be comprised between 0 and max */
		
		hashcode = hashcode%featureCapacity(); //hashcode must be lower than max
		hashcode = hashcode<0?-hashcode:hashcode; //hashcode must be positive
		
		
		return hashcode;
	}


	
	
	

}
