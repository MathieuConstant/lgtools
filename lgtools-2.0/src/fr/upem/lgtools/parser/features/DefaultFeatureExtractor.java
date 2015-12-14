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
public class DefaultFeatureExtractor implements FeatureExtractor {

	private final FeatureMapping fm = new HashFeatureMapping();
	private final List<String> partialfeats = new LinkedList<String>();
	
	
	private final Configuration<DepTree> configuration;
	
	public DefaultFeatureExtractor(Configuration<DepTree> configuration) {
		this.configuration = configuration;
		extractPartialFeatures();
	}
	
	private void addUnitFeatures(String fid,Unit u){
		partialfeats.add(fid+"_f="+u.getForm());
		partialfeats.add(fid+"_t="+u.getPos());
		
	}
	
	private void extractPartialFeatures(){
		Stack<Unit> stack = configuration.getFirstStack(); 
		Unit s0u = stack.peek();
		addUnitFeatures("s0u", s0u);
	}
	
	
	
	@Override
	public Iterable<Integer> perform(String label) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		for(String pf:partialfeats){
			String f = pf + "___"+label;
			list.add(fm.getFeatureId(f));
			
		}
		System.err.println(list);
		return list;
	}
	
	
	
	

}
