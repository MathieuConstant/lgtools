/**
 * 
 */
package fr.upem.lgtools.parser.features;

import fr.upem.lgtools.parser.Configuration;

/**
 * @author Mathieu Constant
 *
 */
public interface FeatureExtractor<T> {
      public FeatureVector perform(Configuration<T> config);
}
