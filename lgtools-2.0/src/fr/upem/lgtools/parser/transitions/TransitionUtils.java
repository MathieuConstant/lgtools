/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class TransitionUtils {
	
	
	//for now, it only deals with MWE component positions and not POS of the MWE 
	
			/**
			 * 
			 * 
			 * 
			 * @param mwePositions
			 * @param s
			 * @return the existing mwe unit, retun null if not found
			 */
			
			public static Unit findExistingMweUnitByPosition(int[] mwePositions, List<Unit> units){
				
				for(Unit u:units){
					if(u != null && Arrays.equals(u.getPositions(), mwePositions)){
						return u;
					}
				}
				
				return null;
			}
		
		public static Unit mergeUnitsAndAdd(Unit u1, Unit u2,Sentence sentence){
			String form = u1.getForm()+"_"+u2.getForm();
			//String lemma = u1.getLemma()+"_"+u2.getLemma();
			String cat = u1.getPos()+"_"+u2.getPos();
			int [] pos1 = u1.getPositions();
			int [] pos2 = u2.getPositions();
			int[] positions = new int[pos1.length+pos2.length];
			
			// fill positions
			for(int i = 0 ; i < pos1.length ; i++){
				positions[i] = pos1[i];
			}
			for(int i = 0 ; i < pos2.length ; i++){
				positions[i+pos1.length] = pos2[i];
			}
			
			
			int id = sentence.getUnits().size() + 1;
			//System.err.println(form);
			//System.err.println(Arrays.toString(positions));
			//System.err.println(units);
			
			//System.err.println(units);	
			Unit mwe = findExistingMweUnitByPosition(positions, sentence.getUnits());
			//System.err.println(mwe);
			
			
			if(mwe == null){
				mwe = new Unit(id,form, positions);
			    sentence.add(mwe);
			    //System.err.println(mwe);
			    
			}
			//mwe.setLemma(lemma);
			
			mwe.setPos(cat);
			return mwe;
		}
	
	public static Configuration<DepTree> performMerge(Configuration<DepTree> configuration, String label, Unit u1, Unit u0, Collection<Deque<Unit>> stacks){

		Unit u = mergeUnitsAndAdd(u1,u0,configuration.getSentence());
		if(label != null && !label.equals("")){
			u.setPos(label);
		}
		DepTree tree = configuration.getAnalyses();
		tree.addLinks(u.getId(),u1.getId(),u0.getId());
		for(Deque<Unit> stack:stacks){
			stack.push(u);
		}

		return configuration;
	}
	
}
