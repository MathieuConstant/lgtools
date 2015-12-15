/**
 * 
 */
package fr.upem.lgtools.parser.features;

import java.util.List;

import fr.upem.lgtools.parser.Configuration;

/**
 * @author Mathieu Constant
 *
 */
public interface FeatureExtractor<T> {
      public List<String> perform(Configuration<T> config);
}
