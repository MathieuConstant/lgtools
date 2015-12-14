/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

/**
 * @author Mathieu
 *
 */
public interface SystemTransitions<T> {
     Transition<T> getTransition(String transitionId);
}
