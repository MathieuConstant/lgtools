/*
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import fr.upem.lgtools.parser.Buffer;
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
		feats.add(fid+"_ft="+u.getForm()+"#"+u.getPos());
	}
	
	private void addUnitPairFeatures(String fid,Unit u1,Unit u2,List<String> feats){
		feats.add(fid+"_t_t="+u1.getPos()+"#"+u2.getPos());
		feats.add(fid+"_f_t="+u1.getForm()+"#"+u2.getPos());
		feats.add(fid+"_t_f="+u1.getPos()+"#"+u2.getForm());
		
	}	
	
	private void addUnitTripletFeatures(String fid,Unit u1,Unit u2,Unit u3,List<String> feats){
		feats.add(fid+"_t_t="+u1.getPos()+"#"+u2.getPos()+"#"+u3.getPos());		
		
	}
	
	
	@Override
	public List<String> perform(Configuration<DepTree> configuration) {
		LinkedList<String> list = new LinkedList<String>();
		
		Stack<Unit> stack = configuration.getFirstStack();
		Buffer buffer = configuration.getFirstBuffer();
		
		Unit s0u = stack.peek();
		addUnitFeatures("s0u", s0u,list);
		if(stack.size() - 2 >= 0){
		  Unit s1u = stack.get(stack.size() - 2);
		  addUnitFeatures("s1u", s1u,list);
		  addUnitPairFeatures("s0u_s1u",s0u,s1u,list);
		  if(buffer.size() > 0){
			  Unit b0u = buffer.get(0);
			  addUnitTripletFeatures("s0u_s1u_b0u",s0u,s1u,b0u,list);			  
		  }
		}
		
		
		if(buffer.size() > 0){
			Unit b0u = buffer.get(0);
			addUnitFeatures("b0u", b0u,list);
			addUnitPairFeatures("s0u_b0u",s0u,b0u,list);
		}
		if(buffer.size() > 1){
			Unit b0u = buffer.get(0);
			Unit b1u = buffer.get(1);
			addUnitFeatures("b1u", b1u,list);
			addUnitPairFeatures("b0u_b1u",b0u,b1u,list);
			addUnitTripletFeatures("b0u_b1u_s0u",b0u,b1u,s0u,list);
		}
		return list;
	}
	
	
	
	

}
