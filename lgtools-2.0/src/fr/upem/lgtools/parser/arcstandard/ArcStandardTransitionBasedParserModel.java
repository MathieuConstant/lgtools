/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.model.ProjectiveTransitionBasedDependencyParserModel;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.Utils;

/**
 * @author mconstant
 *
 */
public class ArcStandardTransitionBasedParserModel extends
		ProjectiveTransitionBasedDependencyParserModel {

	final String SHIFT = "SH";
	final String LEFT_ARC = "LA";
	final String RIGHT_ARC = "RA";
	
	
	public ArcStandardTransitionBasedParserModel(FeatureMapping fm,
			DepTreebank tb) {
		super(fm, tb);		
	}



	public ArcStandardTransitionBasedParserModel(String filename) throws IOException {
		super(filename);		
	}

	/**
	 * 
	 * @param configuration
	 * @return the label of the LA transition to be applied; null when LA transition is not valid
	 */
	
	String getLeftArcLabel(Configuration<DepTree> configuration){
		Deque<Unit> stack = configuration.getFirstStack();
		if(stack.size() > 2){
			Unit u1 = stack.pop();
			Unit u2 = stack.peek();
			stack.push(u1);
			if(u2.getGoldSheadId() == u1.getId()){
				return u2.getGoldSlabel();
			}
		}
		return null;
	}
	
	public static boolean rightDependentHasAllItsDependents(Unit d,Configuration<DepTree> c){
		int id = d.getId();
		Sentence s = c.getSentence();
		List<Unit> buffer = Utils.getTokenSequence(true,s.getUnits().size(),s,c.getFirstBuffer());
		for(Unit u:buffer){
			//System.err.println("RA:"+u);
			Unit r = u;
			if(!u.hasGoldLexicalHead()){
			  r = u.findGoldLexicalRoot(c.getSentence());
			}
			if(id == r.getGoldSheadId()) return false;
		}
		
		return true;
	}
	
	
	/**
	 * 
	 * @param configuration
	 * @return The label of the RA transition to be applied; null when LA transition is not valid
	 */
	
	
	String getRightArcLabel(Configuration<DepTree> configuration){
		Deque<Unit> stack = configuration.getFirstStack();
		if(stack.size() >= 2){
			Unit u1 = stack.pop();
			Unit u2 = stack.peek();
			stack.push(u1);
			if(u1.getGoldSheadId() == u2.getId() && rightDependentHasAllItsDependents(u1,configuration)){
				
				return u1.getGoldSlabel();
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
	 * @see fr.upem.lgtools.parser.model.TransitionBasedModel2#createLabelDependentTransition(fr.upem.lgtools.text.Unit)
	 */
	@Override
	protected Transition<DepTree> createLabelDependentTransition(Unit unit,Sentence s) {
		if(!unit.hasGoldSyntacticHead()){
			return null;
		} 
		String label = unit.getGoldSlabel();
		int head = unit.getGoldSheadId();
		int current = unit.getId();
		
		
		if(head > current){ // head after current:: LA
			return createTransition(LEFT_ARC, label);			
		}
		// head before current:: RA
		return createTransition(RIGHT_ARC, label);		
	}

	/* (non-Javadoc)
	 * @see fr.upem.lgtools.parser.model.TransitionBasedModel2#createTransition(java.lang.String, java.lang.String)
	 */
	@Override
	protected Transition<DepTree> createTransition(String type, String label) {
		if(SHIFT.equals(type)){
		    return new ShiftTransition<DepTree>(SHIFT);
		}
		if(LEFT_ARC.equals(type)){
			return new LeftArcTransition(LEFT_ARC, label);
		}
		if(RIGHT_ARC.equals(type)){
			return new RightArcTransition(RIGHT_ARC, label);
		}
		throw new IllegalStateException("Transition "+type + " is not allowed!");
	}

	/* (non-Javadoc)
	 * @see fr.upem.lgtools.parser.model.TransitionBasedModel2#createLabelIndependentTransitions()
	 */
	@Override
	protected Collection<Transition<DepTree>> createLabelIndependentTransitions() {
		Collection<Transition<DepTree>> transitions = new LinkedList<Transition<DepTree>>();
		transitions.add(createTransition(SHIFT,null));
        return transitions;
	}

}
