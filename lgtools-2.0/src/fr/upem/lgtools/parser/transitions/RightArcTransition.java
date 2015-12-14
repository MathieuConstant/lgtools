/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

import java.util.Stack;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepArc;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class RightArcTransition implements Transition<DepTree> {

	private final String label;
	
	
	/**
	 * @param label
	 */
	public RightArcTransition(String label) {
		this.label = label;
	}

	
	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Stack<Unit> stack = configuration.getFirstStack();
		Unit s0 = stack.pop();
		Unit s1 = stack.pop();
		DepTree t = configuration.getAnalyses();
		t.addArc(new DepArc(s1.getId(),label,s0.getId()));
		stack.push(s1);
		return configuration;
	}

	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Stack<Unit> stack = configuration.getFirstStack();
		return stack.size() >= 2;
	}

	@Override
	public String id() {
		
		return "RIGHT-"+label;
	}
  
	
}
