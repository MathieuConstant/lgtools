/**
 * 
 */
package fr.upem.lgtools.parser.model;

import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.DepTreebankFactory;

/**
 * @author Matthieu Constant
 *
 */
public abstract class ProjectiveTransitionBasedDependencyParserModel extends
		TransitionBasedDependencyParserModel {

	

	public ProjectiveTransitionBasedDependencyParserModel(FeatureMapping fm,
			DepTreebank tb) {
		super(fm, tb);
		
	}


	public ProjectiveTransitionBasedDependencyParserModel(String filename) {
		super(filename);
		
	}


	/* (non-Javadoc)
	 * @see fr.upem.lgtools.parser.model.TransitionBasedModel2#filter(fr.upem.lgtools.text.DepTreebank)
	 */
	@Override
	public DepTreebank filter(DepTreebank tb) {		
		return DepTreebankFactory.filterNonProjective(tb);
	}

	
}