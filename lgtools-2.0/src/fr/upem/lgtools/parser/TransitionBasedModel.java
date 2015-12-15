/**
 * 
 */
package fr.upem.lgtools.parser;

import java.util.List;
import java.util.Set;

import fr.upem.lgtools.parser.features.Feature;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.transitions.Transition;

/**
 * @author Mathieu Constant
 *
 */
public abstract class TransitionBasedModel<T> {
	private final Model model;
	private final FeatureExtractor<T> extractor;
	private final List<Transition<T>> transitions;
	private final FeatureMapping featureMapping;
	
	
	
	
	/**
	 * @param model
	 * @param extractor
	 * @param transitions
	 * @param featureMapping
	 */
	public TransitionBasedModel(Model model, FeatureExtractor<T> extractor, List<Transition<T>> transitions,
			FeatureMapping featureMapping) {
		this.model = model;
		this.extractor = extractor;
		this.transitions = transitions;
		this.featureMapping = featureMapping;
	}
	
	public Model getModel() {
		return model;
	}
	public FeatureExtractor<T> getExtractor() {
		return extractor;
	}
	public List<Transition<T>> getTransitions() {
		return transitions;
	}
	public FeatureMapping getFeatureMapping() {
		return featureMapping;
	}
	
	public abstract Transition<T> staticOracle(Configuration<T> t);
	
	
	public Transition<T> getBestTransition(Configuration<T> config,Set<Transition<T>> authorizedTransitions){
		List<Feature> feats = extractor.perform(config);
		double bestSc = Double.MIN_VALUE;
		int bestLabel =-1;
		for(int l = 0 ; l < transitions.size(); l++){
			if(authorizedTransitions.contains(transitions.get(l))){ 
			  double sc = model.score(feats,l);
			  if(sc > bestSc){
				bestSc = sc;
				bestLabel = l;
			  }
			}
		}
		
		return transitions.get(bestLabel);
	}
	
	

}
