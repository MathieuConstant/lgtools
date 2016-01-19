/**
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.Collection;

/**
 * @author Mathieu Constant
 *
 */
public abstract class FeatureMapping {
   final private int max;  
	
   FeatureMapping(int max){
	   this.max = max;
   }
	
   public int getFeatureId(String feat){
	   	return getFeatureId(feat,false);
   }
   
   public int getFeatureIdWithMemory(String feat){
	   	return getFeatureId(feat,true);
  }
   
   abstract int getFeatureId(String feat, boolean withMemoryIfPossible);
   abstract Collection<String> getFeatures();
   
   public int featureCapacity(){
	   return max;
   }
}
