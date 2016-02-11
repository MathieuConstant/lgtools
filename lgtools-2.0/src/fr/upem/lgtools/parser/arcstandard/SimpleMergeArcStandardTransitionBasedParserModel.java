/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.model.ProjectiveTransitionBasedDependencyParserModel;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Unit;

/**
 * @author mconstant
 *
 */
public class SimpleMergeArcStandardTransitionBasedParserModel extends ArcStandardTransitionBasedParserModel {

	final String MERGE = "ME";
	
	
	public SimpleMergeArcStandardTransitionBasedParserModel(FeatureMapping fm,
			DepTreebank tb) {
		super(fm, tb);		
	}



	public SimpleMergeArcStandardTransitionBasedParserModel(String filename)  throws IOException {
		super(filename);		
	}


	private String getMerge(Configuration<DepTree> configuration){
		Deque<Unit> stack = configuration.getFirstStack();
		if(stack.size() > 2){
			Unit u1 = stack.pop();
			Unit u2 = stack.peek();
			stack.push(u1);
			int l1 = u2.getGoldLHead();
			int l2 = u1.getGoldLHead();
			if(l1 <= 0 || l2 <= 0){
				return null;
			}
			if(l1 == l2){	
				return "";
			}
		}
		return null;
	}
	
	

	/* (non-Javadoc)
	 * @see fr.upem.lgtools.parser.model.TransitionBasedModel2#staticOracle(fr.upem.lgtools.parser.Configuration)
	 */
	@Override
	public Transition<DepTree> staticOracle(Configuration<DepTree> configuration) {
		String label;
		
		//if ME is possible, then ME
		
		if((label = getMerge(configuration)) != null){
			return transitions.getTransition(MERGE,null);
		}
		
		// if LA is possible, then LA
		if((label = getLeftArcLabel(configuration)) != null){
			//System.err.println("Oracle: LA+"+label);
			return transitions.getTransition(LEFT_ARC, label);
		}
		
		//if RA is possible, then RA
		if((label = getRightArcLabel(configuration)) != null){
			//System.err.println("Oracle: RA+"+label);
			return transitions.getTransition(RIGHT_ARC, label);
		}
		//System.err.println("Oracle: SH");
		//default: SH				
		return transitions.getTransition(SHIFT, null);
	}

	
	
	

	/* (non-Javadoc)
	 * @see fr.upem.lgtools.parser.model.TransitionBasedModel2#createTransition(java.lang.String, java.lang.String)
	 */
	@Override
	protected Transition<DepTree> createTransition(String type, String label) {
		if(MERGE.equals(type)){
			return new MergeTransition(MERGE); 
		}
		return super.createTransition(type, label);
	}

	
	/* (non-Javadoc)
	 * @see fr.upem.lgtools.parser.model.TransitionBasedModel2#createLabelIndependentTransitions()
	 */
	@Override
	protected Collection<Transition<DepTree>> createLabelIndependentTransitions() {
		Collection<Transition<DepTree>> transitions = new LinkedList<Transition<DepTree>>();
		transitions.add(createTransition(SHIFT,null));
		transitions.add(createTransition(MERGE,null));
        return transitions;
	}

}
