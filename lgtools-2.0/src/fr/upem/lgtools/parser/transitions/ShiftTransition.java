/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

import java.util.ArrayDeque;
import java.util.Stack;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class ShiftTransition<T> extends AbstractTransition<T>{
	
	
	

	public ShiftTransition(String id) {
		super(id);
		
	}

	@Override
	public Configuration<T> perform(Configuration<T> configuration) {
		if(!isValid(configuration)){
			throw new IllegalStateException("The buffer should not be empty before performing a SHIFT!");
		}
		ArrayDeque<Unit> buffer = configuration.getFirstBuffer();
		Unit u = buffer.poll();
		Stack<Unit> stack = configuration.getFirstStack();
		stack.push(u);
		return configuration;
	}

	@Override
	public boolean isValid(Configuration<T> configuration) {
		ArrayDeque<Unit> buffer = configuration.getFirstBuffer();
		return !buffer.isEmpty();
	}

	
}
