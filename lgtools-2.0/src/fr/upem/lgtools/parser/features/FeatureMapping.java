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
   final protected int max;  
	
   FeatureMapping(int max){
	   this.max = max;
   }
	
   public int getFeatureId(String feat){
	   	return getFeatureId(feat,false);
   }
   
   public int getFeatureIdWithMemory(String feat){
	   	return getFeatureId(feat,true);
  }
   
   abstract public int getFeatureId(String feat, boolean withMemoryIfPossible);
   abstract public Collection<String> getFeatures();
   
   public int featureCapacity(){
	   return max;
   }
   
   @Override
	public String toString() {
	   StringBuilder sb = new StringBuilder("{");
		for(String f:getFeatures()){
			sb.append(f).append("->").append(getFeatureId(f)).append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
   
}
