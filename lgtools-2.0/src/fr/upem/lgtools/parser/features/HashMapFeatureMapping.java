/**
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.HashMap;

/**
 * @author Mathieu
 *
 */
public class HashMapFeatureMapping implements FeatureMapping{
	private final HashMap<String, Integer> features = new HashMap<String,Integer>();
	
	public int getFeatureId(String feat){
		Integer id = features.get(feat);
		if(id == null){
			id = features.size();
			features.put(feat, id);
		}
		return id;
	}

}
