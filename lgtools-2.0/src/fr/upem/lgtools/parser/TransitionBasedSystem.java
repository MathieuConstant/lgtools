/**
 * 
 */
package fr.upem.lgtools.parser;

import java.util.List;

import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.transitions.Transition;

/**
 * @author Mathieu
 *
 */
public abstract class TransitionBasedSystem<T> {

	final private Model model;
	final private FeatureMapping featureMapping;
	
	public List<Transition<T>> getTransitions(){
		
		
	}
	
}
