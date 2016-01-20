/*
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.Deque;

import fr.upem.lgtools.parser.Buffer;
import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.UnitFactory;

/**
 * @author Mathieu Constant
 *
 */
public class DefaultFeatureExtractor implements FeatureExtractor<DepTree> {
	private final static Unit NULL_UNIT = UnitFactory.createNullUnit();
	private final FeatureMapping fm;
	
	public DefaultFeatureExtractor(FeatureMapping featureMapping){
		this.fm = featureMapping;
	}
	
	
	
	
	private void addUnitFeatures(String fid,Unit u,FeatureVector feats){
		if(u == null){
			return;
		}
		feats.add(fid+"_f="+u.getForm());
		feats.add(fid+"_t="+u.getPos());
		feats.add(fid+"_ft="+u.getForm()+"/"+u.getPos());
	}
	
	private void addUnitPairFeatures(String fid,Unit u1,Unit u2,FeatureVector feats){
		
		feats.add(fid+"_t_t="+u1.getPos()+"#"+u2.getPos());
		feats.add(fid+"_f_t="+u1.getForm()+"#"+u2.getPos());
		feats.add(fid+"_ft_t="+u1.getForm()+"/"+u1.getPos()+"#"+u2.getPos());
		feats.add(fid+"_t_f="+u1.getPos()+"#"+u2.getForm());
		feats.add(fid+"_t_ft="+u1.getPos()+"#"+u2.getForm()+"/"+u2.getPos());
		
	}	
	
	private void addUnitTripletFeatures(String fid,Unit u1,Unit u2,Unit u3,FeatureVector feats){
		
		feats.add(fid+"_t_t_t="+u1.getPos()+"#"+u2.getPos()+"#"+u3.getPos());		
		feats.add(fid+"_f_t_t="+u1.getForm()+"#"+u2.getPos()+"#"+u3.getPos());
		feats.add(fid+"_t_f_t="+u1.getPos()+"#"+u2.getForm()+"#"+u3.getPos());
		feats.add(fid+"_t_t_f="+u1.getPos()+"#"+u2.getPos()+"#"+u3.getForm());
	}
	
	private static Unit getSecondElementInStack(Deque<Unit> stack){
		if(stack.size() < 2){
			return NULL_UNIT;
		}
		Unit u1 = stack.pop();
		Unit u2 = stack.peek();
		stack.push(u1);
		return u2;
	}
	
	private static Unit getThirdElementInStack(Deque<Unit> stack){
		if(stack.size() < 3){
			return NULL_UNIT;
		}
		Unit u1 = stack.pop();
		Unit u2 = stack.pop();
		Unit u3 = stack.peek();
		stack.push(u2);
		stack.push(u1);
		return u3;
	}
	
	private static Unit getFirstElementInBuffer(Buffer b){
		if(b.size() ==0 ){
			return NULL_UNIT;
		}
		return b.get(0);
	}
	
	private static Unit getSecondElementInBuffer(Buffer b){
		if(b.size() <= 1 ){
			return NULL_UNIT;
		}
		return b.get(1);
	}
	
	
	@Override
	public FeatureVector perform(Configuration<DepTree> configuration) {
		FeatureVector feats = new FeatureVector(fm);
		
		Deque<Unit> stack = configuration.getFirstStack();
		Buffer buffer = configuration.getFirstBuffer();
		//feats.add("EMPTY");
		//feats.add("EMPTY2");
		Unit s0u = stack.peek();
		Unit s1u = getSecondElementInStack(stack);
		Unit s2u = getThirdElementInStack(stack);
		Unit b0u = getFirstElementInBuffer(buffer);
		Unit b1u = getSecondElementInBuffer(buffer);
		addUnitFeatures("s0u", s0u,feats);
		addUnitFeatures("s1u", s1u,feats);
		addUnitFeatures("s2u", s2u,feats);
		addUnitFeatures("b0u", b0u,feats);
		addUnitFeatures("b1u", b1u,feats);
		addUnitPairFeatures("s0u_s2u",s0u,s2u,feats);
		addUnitPairFeatures("s0u_s1u",s0u,s1u,feats);
		addUnitPairFeatures("s0u_b0u",s0u,b0u,feats);
		addUnitPairFeatures("b0u_b1u",b0u,b1u,feats);
		addUnitTripletFeatures("s2u_s1u_s0u",s2u,s1u,s0u,feats);
		addUnitTripletFeatures("s1u_s0u_b0u",s1u,s0u,b0u,feats);
		addUnitTripletFeatures("s0u_b0u_b1u",s0u,b0u,b1u,feats);
		/*
		if(stack.size() - 2 >= 0){
		  Unit s1u = stack.get(stack.size() - 2);
		  addUnitFeatures("s1u", s1u,feats);
		  addUnitPairFeatures("s0u_s1u",s0u,s1u,feats);
		  if(buffer.size() > 0){
			  Unit b0u = buffer.get(0);
			  addUnitTripletFeatures("s0u_s1u_b0u",s0u,s1u,b0u,feats);			  
		  }
		}
		*/
		/*
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
		}*/
		return feats;
	}
	
	
	
	

}
