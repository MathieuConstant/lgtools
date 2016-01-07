/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

import java.util.Stack;

import fr.upem.lgtools.parser.Buffer;
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
		Buffer buffer = configuration.getFirstBuffer();
		Unit u = buffer.next();
		Stack<Unit> stack = configuration.getFirstStack();
		stack.push(u);
		return configuration;
	}

	@Override
	public boolean isValid(Configuration<T> configuration) {
		Buffer buffer = configuration.getFirstBuffer();
		return buffer.size() != 0;
	}

	
}
