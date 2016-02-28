/**
 * 
 */
package fr.upem.lgtools.parser.mwereco;

import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.transitions.AbstractTransition;
import fr.upem.lgtools.text.Unit;

/**
 * @author Matthieu Constant
 *
 */
public class CompleteTransition extends AbstractTransition<DepTree> {

	public CompleteTransition(String type) {
		super(type);
	}

	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();

		//more than one element in stack (including root)
		if(stack.size() < 2){
			return false;
		}
		
		return true;
	}

	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		stack.pop();
		return configuration;
	}
	
	

}
