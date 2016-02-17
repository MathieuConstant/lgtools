/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.transitions.LabeledTransition;
import fr.upem.lgtools.text.Unit;

/**
 * @author Matthieu Constant
 *
 */
public class MergeTransition extends LabeledTransition<DepTree> {

	public MergeTransition(String type, String label) {
		super(type, label);
	}

	
	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		
		return null;
	}

	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getSecondStack();
		
		return stack.size() >= 3;
	}

	
	
}
