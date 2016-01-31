/**
 * 
 */
package fr.upem.lgtools.parser;

import fr.upem.lgtools.parser.features.FeatureVector;

/**
 * 
 * @author Mathieu Constant
 *
 */
public class ParseHypothesis<T> {
	private final Configuration<T> configuration;
	private final double score;
	private final FeatureVector features;
	
	/**
	 * @param configuration
	 * @param score
	 * @param features
	 */
	public ParseHypothesis(Configuration<T> configuration, double score, FeatureVector features) {
		super();
		this.configuration = configuration;
		this.score = score;
		this.features = features;
	}
	
	
	public Configuration<T> getConfiguration() {
		return configuration;
	}
	
	public double getScore() {
		return score;
	}
	
	public FeatureVector getFeatures() {
		return features;
	}

	
	
	
	
}
