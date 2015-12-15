/**
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class DefaultFeatureExtractor implements FeatureExtractor<DepTree> {

	private final FeatureMapping fm = new HashFeatureMapping();
	
	
	
	private void addUnitFeatures(String fid,Unit u,List<String> feats){
		feats.add(fid+"_f="+u.getForm());
		feats.add(fid+"_t="+u.getPos());
		
	}
	
		
	
	@Override
	public List<Feature> perform(Configuration<DepTree> configuration) {
		LinkedList<String> list = new LinkedList<String>();
		
		Stack<Unit> stack = configuration.getFirstStack(); 
		Unit s0u = stack.peek();
		addUnitFeatures("s0u", s0u,list);
		
		LinkedList<Feature> feats = new LinkedList<Feature>();
		for(String f:list){
			feats.add(new Feature(fm.getFeatureId(f)));
		}
		
		return feats;
	}
	
	
	
	

}
