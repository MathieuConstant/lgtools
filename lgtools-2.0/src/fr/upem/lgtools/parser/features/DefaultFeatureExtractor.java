/*
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

	
	
	
	private void addUnitFeatures(String fid,Unit u,List<String> feats){
		feats.add(fid+"_f="+u.getForm());
		feats.add(fid+"_t="+u.getPos());
		
	}
	
		
	
	@Override
	public List<String> perform(Configuration<DepTree> configuration) {
		LinkedList<String> list = new LinkedList<String>();
		
		Stack<Unit> stack = configuration.getFirstStack(); 
		Unit s0u = stack.peek();
		addUnitFeatures("s0u", s0u,list);
		
		
		
		return list;
	}
	
	
	
	

}
