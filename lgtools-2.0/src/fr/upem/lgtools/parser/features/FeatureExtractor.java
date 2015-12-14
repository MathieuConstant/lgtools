/**
 * 
 */
package fr.upem.lgtools.parser.features;

/**
 * @author Mathieu Constant
 *
 */
public interface FeatureExtractor {
      public Iterable<Integer> perform(String label);
}
