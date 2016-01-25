/*
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import fr.upem.lgtools.parser.Buffer;
import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepArc;
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
		if(u == null || u == NULL_UNIT){
			return;
		}
		feats.add(fid+"_f="+u.getForm());
		feats.add(fid+"_l="+u.getLemma());
		feats.add(fid+"_t="+u.getPos());
		feats.add(fid+"_ft="+u.getForm()+"/"+u.getPos());
		feats.add(fid+"_lt="+u.getLemma()+"/"+u.getPos());
		
		
	}
	
	private void addUnitPairFeatures(String fid,Unit u1,Unit u2,FeatureVector feats){
		if(u1 == null || u1 == NULL_UNIT){
			return;
		}
		if(u2 == null || u2 == NULL_UNIT){
			return;
		}
		
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
	
	
	private void addHistoryFeatures(Configuration<DepTree> configuration,FeatureVector feats){
		List<String> history = configuration.getHistory();
		if(history.size() >= 1){
		   feats.add("history1:"+history.get(history.size() -1));
		}
		if(history.size() >= 2){
			feats.add("history2:"+history.get(history.size() -1)+"#"+history.get(history.size() -2));
		}
		if(history.size() >= 3){
			feats.add("history3:"+history.get(history.size() -1)+"#"+history.get(history.size() -2)+"#"+history.get(history.size() -3));
		}
		
	}
	
	
    private void addDistanceFeature(String name, Unit u1, Unit u2,FeatureVector feats){
		feats.add("dist-"+name+":"+(u1.getId() - u2.getId()));
		
	}
	
    
    private void addSubtreeFeatures(String name,Unit root, Configuration<DepTree> configuration, FeatureVector feats){
    	DepTree a = configuration.getAnalyses();
    	DepArc[] arcs = a.getArcs();
    	
    	//System.err.println(Arrays.toString(arcs));
    	boolean empty = true;
    	for(int c:a.getChildren(root.getId())){
    		feats.add("child:"+name+":"+arcs[c].getLabel());
    		empty = false;
    	}
    	if(empty){
    		feats.add("nochildren:"+name);
    	}
    	
    }
    
    
    private Unit getLeftMostDependencyElement(Unit unit,Configuration<DepTree> configuration){
    	DepTree tree = configuration.getAnalyses();
    	DepArc[] lefts = tree.getLeftMostDependencies();
    	int h = unit.getId();
    	if(h < 0 || lefts[h] == null){
    		return NULL_UNIT;
    	}
        return configuration.getUnit(lefts[h].getDep());
    }
    
    private Unit getRightMostDependencyElement(Unit unit,Configuration<DepTree> configuration){
    	DepTree tree = configuration.getAnalyses();
    	DepArc[] rights = tree.getRightMostDependencies();
    	int h = unit.getId();
    	if(h < 0 || rights[h] == null){
    		return NULL_UNIT;
    	}
        return configuration.getUnit(rights[h].getDep());
    }
    
    
    private void addDependencyFeatures(String fid,Configuration<DepTree> configuration, Unit head,DepArc[] arcs,FeatureVector feats){
    	if(head == NULL_UNIT){
    		return;
    	}
    	int h = head.getId();
    	DepArc arc = arcs[h];
    	if(arc == null){
    		return;
    	}
    	int d = arc.getDep();
    	Unit dep = configuration.getUnit(d);
    	
    	/*feats.add(fid+"_hpos_label_dpos:"+head.getPos()+"#"+arc.getLabel()+"#"+dep.getPos());
    	feats.add(fid+"_hform_label_dpos:"+head.getForm()+"#"+arc.getLabel()+"#"+dep.getPos());
    	feats.add(fid+"_hpos_label_dform:"+head.getPos()+"#"+arc.getLabel()+"#"+dep.getForm());
    	feats.add(fid+"_hform_label_dform:"+head.getForm()+"#"+arc.getLabel()+"#"+dep.getForm());
    	
    	
    	feats.add(fid+"_hpos_label:"+head.getPos()+"#"+arc.getLabel());
    	feats.add(fid+"_hform_label:"+head.getForm()+"#"+arc.getLabel());
    	feats.add(fid+"_hform_hpos_label:"+head.getForm()+"/"+head.getPos()+"#"+arc.getLabel());
    	
    	feats.add(fid+"_label_dpos:"+arc.getLabel()+"#"+dep.getPos());
    	feats.add(fid+"_label_dform:"+arc.getLabel()+"#"+dep.getForm());
    	feats.add(fid+"_label_dform_dpos:"+arc.getLabel()+"#"+dep.getForm()+"#"+dep.getPos());
    	*/
    	feats.add(fid+"_hpos_label_dpos:"+head.getPos()+"#"+arc.getLabel()+"#"+dep.getPos());
    	feats.add(fid+"_hpos_label:"+head.getPos()+"#"+arc.getLabel());
    	feats.add(fid+"_label_dpos:"+arc.getLabel()+"#"+dep.getPos());
    	feats.add(fid+"_label:"+arc.getLabel());
    	
    	
    	addUnitFeatures(fid+"#dep", dep, feats);
    	addUnitPairFeatures(fid+"#head#dep", head, dep, feats);
    	
    }
    
    
    
    private void addLeftMostDependencyFeatures(String fid, Configuration<DepTree> configuration, Unit head, FeatureVector feats){
    	DepTree tree = configuration.getAnalyses();
    	addDependencyFeatures(fid, configuration, head, tree.getLeftMostDependencies(), feats);
    }
    
    private void addRightMostDependencyFeatures(String fid, Configuration<DepTree> configuration, Unit head, FeatureVector feats){
    	DepTree tree = configuration.getAnalyses();
    	addDependencyFeatures(fid, configuration, head, tree.getRightMostDependencies(), feats);
    }
    
	
	@Override
	public FeatureVector perform(Configuration<DepTree> configuration) {
		FeatureVector feats = new FeatureVector(fm);
		
		Deque<Unit> stack = configuration.getFirstStack();
		Buffer buffer = configuration.getFirstBuffer();
		feats.add("BIAS");
		//feats.add("EMPTY2");
		Unit s0u = stack.peek();
		Unit s1u = getSecondElementInStack(stack);
		Unit s2u = getThirdElementInStack(stack);
		Unit b0u = getFirstElementInBuffer(buffer);
		Unit b1u = getSecondElementInBuffer(buffer);
		
		
		//addSubtreeFeatures("s0u",s0u,configuration,feats);
		//addSubtreeFeatures("s1u",s1u,configuration,feats);
		addDistanceFeature("s0us1u",s0u,s1u,feats);
		addDistanceFeature("s1us2u",s1u,s2u,feats);
		addDistanceFeature("s0ub0u",s0u,b0u,feats);
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
		
	
		addHistoryFeatures(configuration, feats);
		addLeftMostDependencyFeatures("lmd_s0u", configuration, s0u, feats);
		addLeftMostDependencyFeatures("lmd_s1u", configuration, s1u, feats);
		
		addRightMostDependencyFeatures("rmd_s0u", configuration, s0u, feats);
		addRightMostDependencyFeatures("rmd_s1u", configuration, s1u, feats);
		
		
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
