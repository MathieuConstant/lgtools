/**
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.Deque;
import java.util.List;
import java.util.Map;

import fr.upem.lgtools.parser.Buffer;
import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepArc;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.UnitFactory;

/**
 * @author Mathieu Constant
 *
 */
public class FeatureUtils {
	public final static Unit NULL_UNIT = UnitFactory.createNullUnit();
	
	public static void addUnitFeatures(String fid,Unit u,FeatureVector feats,Configuration<DepTree> configuration){

		feats.add(fid+"_f="+u.getForm());
		feats.add(fid+"_l="+u.getLemma());
		feats.add(fid+"_t="+u.getPos());
		feats.add(fid+"_ft="+u.getForm()+"/"+u.getPos());
		feats.add(fid+"_lt="+u.getLemma()+"/"+u.getPos());
		
		//feats.add(fid+"_mwe="+u.isMWE());
		//feats.add(fid+"_length="+u.getPositions().length);
		//feats.add(fid+"_pattern="+u.getPOSPattern(configuration.getSentence()));
		

		for(Map.Entry<String,String> e:u.getFeatures().entrySet()){
			String att = e.getKey();
			String val = e.getValue();
			feats.add(fid+"_feat="+att+":"+val);
			feats.add(fid+"_featt="+att+":"+val+"/"+u.getPos());
		}	
	}
	

	
	static void addUnitFeaturesTmp(String fid,Unit u,FeatureVector feats){
		
		feats.add(fid+"_f="+u.getForm());
		//feats.add(fid+"_l="+u.getLemma());
		feats.add(fid+"_t="+u.getPos());
		feats.add(fid+"_ft="+u.getForm()+"/"+u.getPos());
		//feats.add(fid+"_lt="+u.getLemma()+"/"+u.getPos());	
	}

		

	
	public static void addUnitPairFeatures(String fid,Unit u1,Unit u2,FeatureVector feats, Configuration<DepTree> configuration){
		
		
		feats.add(fid+"_t_t="+u1.getPos()+"#"+u2.getPos());
		feats.add(fid+"_f_t="+u1.getForm()+"#"+u2.getPos());
		feats.add(fid+"_ft_t="+u1.getForm()+"/"+u1.getPos()+"#"+u2.getPos());
		
		
		feats.add(fid+"_t_f="+u1.getPos()+"#"+u2.getForm());
		feats.add(fid+"_t_ft="+u1.getPos()+"#"+u2.getForm()+"/"+u2.getPos());
		
		/*
		if(u1.isMWE() || u2.isMWE()){
			Unit u10 = configuration.getUnit(u1.getUnitFirstTokenPosition());
			Unit u20 = configuration.getUnit(u2.getUnitFirstTokenPosition());
			addUnitPairFeatures(fid+"-first",u10,u20,feats,configuration);
			
		}*/
		
		
	}	
	
	public static void addUnitTripletFeatures(String fid,Unit u1,Unit u2,Unit u3,FeatureVector feats,Configuration<DepTree> configuration){
		
		feats.add(fid+"_t_t_t="+u1.getPos()+"#"+u2.getPos()+"#"+u3.getPos());		
		feats.add(fid+"_f_t_t="+u1.getForm()+"#"+u2.getPos()+"#"+u3.getPos());
		feats.add(fid+"_t_f_t="+u1.getPos()+"#"+u2.getForm()+"#"+u3.getPos());
		feats.add(fid+"_t_t_f="+u1.getPos()+"#"+u2.getPos()+"#"+u3.getForm());
		/*
		if(u1.isMWE() || u2.isMWE() || u3.isMWE()){
			Unit u10 = configuration.getUnit(u1.getUnitFirstTokenPosition());
			Unit u20 = configuration.getUnit(u2.getUnitFirstTokenPosition());
			Unit u30 = configuration.getUnit(u3.getUnitFirstTokenPosition());
			addUnitTripletFeatures(fid+"-first",u10,u20,u20,feats,configuration);
			
		}*/
	}
	
	
	public static void addHistoryFeatures(Configuration<DepTree> configuration,FeatureVector feats){
		List<String> history = configuration.getHistory();
		//System.err.println(history );
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
	
	public static void addDistanceFeature(String fid, Unit u1, Unit u2,FeatureVector feats){
    	int dist = u2.getId() - u1.getId();
    	
    	if(dist < 0){
    		dist = -1;
    	}
    	else{
    		if(dist == 1){
    			dist = 1;    			
    		}
    		else{
    			if(dist == 2){
    				dist = 2;
    			}
    			else{
    				if(dist > 2 & dist <=5){
    					dist = 3;
    				}
    				else{
    					dist = 6;
    				}
    				
    			}
    			
    		}
    		
    	}
    	
		feats.add("dist-"+fid+":"+dist);
		
	}
	
    
    
    
    private static Unit getLeftMostDependencyElement(Unit unit,Configuration<DepTree> configuration){
    	DepTree tree = configuration.getAnalyses();
    	DepArc[] lefts = tree.getLeftMostDependencies();
    	int h = unit.getId();
    	//System.err.println(lefts.length+"---"+h+"--"+unit);
    	if(h < 0 || lefts[h] == null){
    		return NULL_UNIT;
    	}
        return configuration.getUnit(lefts[h].getDep());
    }
    
    private static Unit getRightMostDependencyElement(Unit unit,Configuration<DepTree> configuration){
    	DepTree tree = configuration.getAnalyses();
    	DepArc[] rights = tree.getRightMostDependencies();
    	//System.err.println(Arrays.toString(rights));
    	int h = unit.getId();
    	//System.err.println(h);
    	//if(h >=0){
    	//	System.err.println(rights[h]);
    	//}
    	if(h < 0 || rights[h] == null){
    		return NULL_UNIT;
    	}
   
        return configuration.getUnit(rights[h].getDep());
    }
    
    
    public static void addDependencyFeatures(String fid,Configuration<DepTree> configuration, Unit head,DepArc[] arcs,FeatureVector feats){
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
    	
    	
    	addUnitFeatures(fid+"#dep", dep, feats,configuration);
    	addUnitPairFeatures(fid+"#head#dep", head, dep, feats,configuration);
    	
    }
    
    
    
   
    
        
    
    public static void addSubTreeFeatures(String fid,Unit unit,Configuration<DepTree> configuration,FeatureVector feats){
    	if(unit == NULL_UNIT){    		
    		return;    		
    	}
    	
    	Unit lmdu = getLeftMostDependencyElement(unit,configuration);
    	Unit rmdu = getRightMostDependencyElement(unit,configuration);
    	
    	
    	DepTree tree = configuration.getAnalyses();
    	
    	String lmd = lmdu == NULL_UNIT?"_":tree.getlabel(lmdu.getId());
    	feats.add(fid+"_lmd:"+lmd);
    	String rmd = rmdu == NULL_UNIT?"_":tree.getlabel(rmdu.getId());
    	feats.add(fid+"_rmd:"+rmd);
    	feats.add(fid+"_t_lmd:"+unit.getPos()+"#"+lmd);
    	feats.add(fid+"_t_rmd:"+unit.getPos()+"#"+rmd);
    	feats.add(fid+"_t_lmd_rmd:"+unit.getPos()+"#"+lmd+"#"+rmd);
    	
    	
    	
    	
    	//System.err.println(fid+"_t_lmd_rmd:"+unit.getPos()+"#"+lmd+"#"+rmd);
    }
    
    public static void addUnitComponents(String fid,Unit u, Configuration<DepTree> configuration, FeatureVector feats){
    	if(u.isMWE()){
    		
    	   Sentence s = configuration.getSentence();
    	   int[] positions = u.getPositions();
    	   Unit lc = s.get(positions[0]);
    	   //addUnitFeatures(fid+"-left", lc,feats);
    	   feats.add(fid+"-left_f="+lc.getForm());
   		   feats.add(fid+"-left_l="+lc.getLemma());
   		   feats.add(fid+"-left_t="+lc.getPos());
    	   Unit rc = s.get(positions[positions.length-1]);
    	   //addUnitFeatures(fid+"-right", rc,feats);
    	   feats.add(fid+"-right_t="+rc.getPos());
    	}
    	
    }
    
	
	
	public static Unit getSecondElementInStack(Deque<Unit> stack){
		if(stack.size() < 2){
			return NULL_UNIT;
		}
		Unit u1 = stack.pop();
		Unit u2 = stack.peek();
		stack.push(u1);
		return u2;
	}
	
	public static Unit getThirdElementInStack(Deque<Unit> stack){
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
	
	 public static Unit getFirstElementInBuffer(Buffer b){
		if(b.size() ==0 ){
			return NULL_UNIT;
		}
		return b.get(0);
	}
	
	public static Unit getSecondElementInBuffer(Buffer b){
		if(b.size() <= 1 ){
			return NULL_UNIT;
		}
		return b.get(1);
	}
	
	public static Unit getThirdElementInBuffer(Buffer b){
		if(b.size() <= 2 ){
			return NULL_UNIT;
		}
		return b.get(2);
	}
	
	
	
}
