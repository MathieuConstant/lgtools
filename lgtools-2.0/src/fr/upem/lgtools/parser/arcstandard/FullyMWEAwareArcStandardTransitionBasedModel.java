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
import fr.upem.lgtools.parser.model.ProjectiveTransitionBasedDependencyParserModel;
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

	
	@Override
	public Transition<DepTree> staticOracle(Configuration<DepTree> configuration){
		//COMPLETE
		
		//MERGE_BOTH
		
		//MERGE
		
		//static oracle du arc-standard
		

		//if ME is possible, then ME
		String label;
		
		/*if((label = getMerge(configuration)) != null){
			return transitions.getTransition(MERGE,label);
		}*/
		
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

		
		//return super.staticOracle(configuration);
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
				return super.createLabelDependentTransition(unit, s);
	}


	@Override
	protected Transition<DepTree> createTransition(String type, String label) {
		if(MERGE_BOTH.equals(type)){
			return new MergeBothTransition(MERGE,label); 
		}
		if(MERGE.equals(type)){
			//return new MergeTransition(MERGE_BOTH,label);
		}
		if(COMPLETE.equals(type)){
			//return new CompleteTransition(COMPLETE,null);
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
