package fr.upem.lgtools.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import fr.upem.lgtools.parser.features.DefaultFeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.transitions.LeftArcTransition;
import fr.upem.lgtools.parser.transitions.RightArcTransition;
import fr.upem.lgtools.parser.transitions.ShiftTransition;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.parser.transitions.Transitions;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class ArcStandardSyntacticParserModel extends TransitionBasedModel<DepTree> {
    private final static FeatureExtractor<DepTree> EXTRACTOR = new DefaultFeatureExtractor();
    private final static Transition<DepTree> SHIFT = new ShiftTransition<DepTree>("SHIFT");
    private final static String LA_TYPE = "LA";
    private final static String RA_TYPE = "RA";
    
    
    
	

	public ArcStandardSyntacticParserModel(int nFeats, DepTreebank tb) {
		super(nFeats, tb);		
	}
	
	
	public static boolean rightDependentHasAllItsDependents(Unit d,Configuration<DepTree> c){
		int id = d.getId();
		for(Unit u:c.getFirstBuffer()){
			if(id == u.getSheadId()) return false;
		}
		
		
		return true;
	}
	
	

	@Override
	public Transition<DepTree> staticOracle(Configuration<DepTree> c) {
		
		Stack<Unit> stack = c.getFirstStack();
		if(stack.size() >= 2){
			Unit u1 = stack.get(stack.size() - 1);			
			Unit u2 = stack.get(stack.size() - 2);
			// if can do LA
			//System.out.println(u1);
			//System.out.println(u2);
			//System.out.println(u2.getSheadId() == u1.getId());
			
			if(!u2.isRoot()){
				if(u2.getSheadId() == u1.getId()){				
					return getTransitionFromId(Transitions.constructTransitionId(LA_TYPE, u2.getSlabel()));
				}
			}
			
			//if can do RA			
			
				if(u1.getSheadId() == u2.getId() && rightDependentHasAllItsDependents(u1,c)){
					return getTransitionFromId(Transitions.constructTransitionId(RA_TYPE, u1.getSlabel()));
				}	
			
		}		
		
		//default: SH		
		return SHIFT;
	}

	@Override
	public Configuration<DepTree> getInitialConfiguration(List<Unit> units) {		
		return new Configuration<DepTree>(units, new DepTree(units.size() + 1), 1, 1); //one stack and one buffer
	}



	
	// when loading the model
	@Override
	public Transition<DepTree> constructTransition(String transitionId) {
		String type = Transitions.getType(transitionId);
		String label = Transitions.getLabel(transitionId);
		if(type.equals(SHIFT.id())){
			return new ShiftTransition<DepTree>(SHIFT.id());
		}
		
		if(type.equals(RA_TYPE)){
			return new RightArcTransition(RA_TYPE, Transitions.constructTransitionId(RA_TYPE, label));
		}		
		if(type.equals(LA_TYPE)){
			return new LeftArcTransition(LA_TYPE, Transitions.constructTransitionId(LA_TYPE, label));
		}
		throw new IllegalArgumentException("Transition " + transitionId + " is not allowed in an Arc Standard Syntactic Parser");
	}


    //when training
	@Override
	public List<Transition<DepTree>> constructTransitions(DepTreebank tb) {
		Set<Transition<DepTree>> set = new HashSet<Transition<DepTree>>();
		set.add(SHIFT);
		for(Sentence s:tb){
			for(Unit u:s.getTokens()){
				if(u.getId() > u.getSheadId()){
					set.add(new RightArcTransition(RA_TYPE, u.getSlabel()));
				}
				else{
					set.add(new LeftArcTransition(LA_TYPE, u.getSlabel()));
				}
			}
		}
		return new ArrayList<Transition<DepTree>>(set);
	}



	@Override
	public FeatureExtractor<DepTree> getFeatureExtractor() {	
		return EXTRACTOR;
	}
	

}
