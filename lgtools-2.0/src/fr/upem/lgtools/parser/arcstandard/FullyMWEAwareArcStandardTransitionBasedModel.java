/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * @author Matthieu Constant
 *
 */
public class FullyMWEAwareArcStandardTransitionBasedModel extends
		ArcStandardTransitionBasedParserModel{

	private final String COMPLETE = "CMP";
	private final String MERGE_BOTH = "MB";
	private final String MERGE = "ME"
			;
	
	public FullyMWEAwareArcStandardTransitionBasedModel(FeatureMapping fm,
			DepTreebank tb) {
		super(fm, tb);
	}
	

	public FullyMWEAwareArcStandardTransitionBasedModel(String filename)
			throws IOException {
		super(filename);
	}

	

	@Override
	public Configuration<DepTree> getInitialConfiguration(Sentence s) {		
		return new Configuration<DepTree>(s, new DepTree(s.getUnits().size() + 1), 1, 2);
	}

	
	private String getMergeBoth(Configuration<DepTree> configuration){
		/*
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
				return configuration.getUnit(l1).getPos();
			}
		}*/
		return null;
	}
	
	private String getMerge(Configuration<DepTree> configuration){
		/*
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
				return configuration.getUnit(l1).getPos();
			}
		}*/
		return null;
	}
	
	
	@Override
	public Transition<DepTree> staticOracle(Configuration<DepTree> configuration){
		String label;
		//if COMPLETE is possible
		
		//MERGE_BOTH
		
		//MERGE
		
		//static oracle du arc-standard
				
		return super.staticOracle(configuration);
	}
	
	
	
	
	@Override
	public void updateSentenceAfterAnalysis(Sentence s, DepTree analysis) {
		//TODO
	}
	
	@Override
	protected FeatureExtractor<DepTree> getFeatureExtractor(FeatureMapping fm) {
		//TO MODIFY AT ONE MOMENT
		return new DefaultFeatureExtractor(fm);
	}


	@Override
	protected Transition<DepTree> createLabelDependentTransition(Unit unit,
			Sentence s) {
              String label;
            //MERGE
            //MERGEBOTH
            Unit r = unit.GetGoldLexicalParent(s);
  			
            /* A REVOIR */
            
  			if(r != null){
  				//NE MARCHE POUR LES NON-TOKENS
  				
  				if(unit.getGoldSheadId() == -1){ //unit has no gold syntactic head
  					return createTransition(MERGE_BOTH, r.getPos());
  				}
  				//unit has a gold syntactic head
  				return createTransition(MERGE, r.getPos());
  			}  
              
              
			return super.createLabelDependentTransition(unit, s);
	}


	@Override
	protected Transition<DepTree> createTransition(String type, String label) {
		if(MERGE_BOTH.equals(type)){
			return new MergeBothTransition(MERGE,label); 
		}
		if(MERGE.equals(type)){
			return new MergeTransition(MERGE_BOTH,label);
		}
		if(COMPLETE.equals(type)){
			return new CompleteTransition(COMPLETE);
		}
		return super.createTransition(type, label);
	}


	@Override
	protected Collection<Transition<DepTree>> createLabelIndependentTransitions() {
		Collection<Transition<DepTree>> transitions = new LinkedList<Transition<DepTree>>();
		transitions.add(createTransition(SHIFT,null));
		transitions.add(createTransition(COMPLETE,null));
        return transitions;
	}
	

}
