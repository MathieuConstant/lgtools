/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

/**
 * @author Matthieu Constant
 *
 */

public abstract class LabeledTransition<T> extends AbstractTransition<T> {
    final String label;
	
	public LabeledTransition(String type,String label) {
		super(type);
		this.label = label;
	}

	@Override
	public String id() {		
		return Transitions.constructTransitionId(super.type, label);
	}

	
	

		
	
}
