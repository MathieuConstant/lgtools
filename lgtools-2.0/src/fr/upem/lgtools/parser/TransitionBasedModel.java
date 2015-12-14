/**
 * 
 */
package fr.upem.lgtools.parser;

import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.transitions.Transition;

/**
 * @author Mathieu
 *
 */
public class TransitionBasedModel<T> {
	private final Model model;
	private final FeatureExtractor extractor;
	private final Transition<T> transitions;
	private final FeatureMapping featureMapping;
	
	
	
	
	/**
	 * @param model
	 * @param extractor
	 * @param transitions
	 * @param featureMapping
	 */
	public TransitionBasedModel(Model model, FeatureExtractor extractor, Transition<T> transitions,
			FeatureMapping featureMapping) {
		this.model = model;
		this.extractor = extractor;
		this.transitions = transitions;
		this.featureMapping = featureMapping;
	}
	
	public Model getModel() {
		return model;
	}
	public FeatureExtractor getExtractor() {
		return extractor;
	}
	public Transition<T> getTransitions() {
		return transitions;
	}
	public FeatureMapping getFeatureMapping() {
		return featureMapping;
	}
	
	
	
	

}
