/*
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.Deque;

import fr.upem.lgtools.parser.Buffer;
import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class DefaultFeatureExtractor implements FeatureExtractor<DepTree> {
	private final FeatureMapping fm;
	
	public DefaultFeatureExtractor(FeatureMapping featureMapping){
		this.fm = featureMapping;
	}
	
	
	
	
	private void addUnitFeatures(String fid,Unit u,FeatureVector feats){
		feats.add(fid+"_f="+u.getForm());
		feats.add(fid+"_t="+u.getPos());
		feats.add(fid+"_ft="+u.getForm()+"#"+u.getPos());
	}
	
	private void addUnitPairFeatures(String fid,Unit u1,Unit u2,FeatureVector feats){
		feats.add(fid+"_t_t="+u1.getPos()+"#"+u2.getPos());
		feats.add(fid+"_f_t="+u1.getForm()+"#"+u2.getPos());
		feats.add(fid+"_t_f="+u1.getPos()+"#"+u2.getForm());
		
	}	
	
	private void addUnitTripletFeatures(String fid,Unit u1,Unit u2,Unit u3,FeatureVector feats){
		feats.add(fid+"_t_t="+u1.getPos()+"#"+u2.getPos()+"#"+u3.getPos());		
		
	}
	
	
	@Override
	public FeatureVector perform(Configuration<DepTree> configuration) {
		FeatureVector feats = new FeatureVector(fm);
		
		Deque<Unit> stack = configuration.getFirstStack();
		Buffer buffer = configuration.getFirstBuffer();
		
		Unit s0u = stack.peek();
		addUnitFeatures("s0u", s0u,feats);
		/*if(stack.size() - 2 >= 0){
		  Unit s1u = stack.get(stack.size() - 2);
		  addUnitFeatures("s1u", s1u,feats);
		  addUnitPairFeatures("s0u_s1u",s0u,s1u,feats);
		  if(buffer.size() > 0){
			  Unit b0u = buffer.get(0);
			  addUnitTripletFeatures("s0u_s1u_b0u",s0u,s1u,b0u,feats);			  
		  }
		}*/
		
		
		if(buffer.size() > 0){
			Unit b0u = buffer.get(0);
			addUnitFeatures("b0u", b0u,feats);
			addUnitPairFeatures("s0u_b0u",s0u,b0u,feats);
		}
		if(buffer.size() > 1){
			Unit b0u = buffer.get(0);
			Unit b1u = buffer.get(1);
			addUnitFeatures("b1u", b1u,feats);
			addUnitPairFeatures("b0u_b1u",b0u,b1u,feats);
			addUnitTripletFeatures("b0u_b1u_s0u",b0u,b1u,s0u,feats);
		}
		return feats;
	}
	
	
	
	

}
