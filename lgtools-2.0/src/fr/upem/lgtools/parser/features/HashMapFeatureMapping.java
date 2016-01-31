/**
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mathieu
 *
 */
public class HashMapFeatureMapping extends FeatureMapping{
	

	private final Map<String, Integer> features;
	
	public HashMapFeatureMapping(int max) {
		super(max);
		features = new HashMap<String,Integer>();
	}
	
	public HashMapFeatureMapping(int max, Map<String,Integer> map){
		super(max);
		features = map;
	}
	
	
	
	// to be used at parsing time
	public int getFeatureId(String feat){		
		return getFeatureId(feat,false);
	}

	
	@Override
	public int getFeatureId(String feat, boolean withMemoryIfPossible) {
		Integer id = features.get(feat);
		if(id == null){
			if(!withMemoryIfPossible){
				return -1;
			}
			else{
				if(features.size() == featureCapacity()){
					throw new IllegalStateException("Number of features is too large: it should not exceed "+features.size());
				}
			}
			
			id = features.size();
			features.put(feat, id);
		}
		
		return id;
		
	}

	@Override
	public Collection<String> getFeatures() {		
		return features.keySet();
	}

}
