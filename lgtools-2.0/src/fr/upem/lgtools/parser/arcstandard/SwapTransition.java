/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.util.Deque;

import fr.upem.lgtools.parser.Analysis;
import fr.upem.lgtools.parser.Buffer;
import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.transitions.AbstractTransition;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu
 *
 */
public class SwapTransition<T extends Analysis> extends AbstractTransition<T>{

	public SwapTransition(String type) {
		super(type);
	}

	@Override
	public boolean isValid(Configuration<T> configuration) {
		//0 < i < j
		Deque<Unit> stack = configuration.getFirstStack();
		if(stack.size() < 3){
			return false;
		}
		Unit s0 = stack.pop();
		Unit s1 = stack.peek();
		stack.push(s0);
		int s0p = s0.getUnitFirstTokenPosition();
		int s1p = s1.getUnitFirstTokenPosition();
		
		return s1p < s0p;
	}

	@Override
	public Configuration<T> perform(Configuration<T> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		Buffer buffer = configuration.getFirstBuffer();
		Unit s0 = stack.pop();
		Unit s1 = stack.pop();
		stack.push(s0);
		buffer.push(s1);
		
		return configuration;
	}

	
	
}
