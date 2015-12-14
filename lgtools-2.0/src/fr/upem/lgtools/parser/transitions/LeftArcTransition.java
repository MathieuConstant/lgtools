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
public class LeftArcTransition implements Transition<DepTree> {

	private final String label;
	
	
	/**
	 * @param label
	 */
	public LeftArcTransition(String label) {
		this.label = label;
	}

	
	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Stack<Unit> stack = configuration.getFirstStack();
		Unit s0 = stack.pop();
		Unit s1 = stack.pop();
		DepTree t = configuration.getAnalyses();
		t.addArc(new DepArc(s0.getId(),label,s1.getId()));
		stack.push(s0);
		return configuration;
	}

	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Stack<Unit> stack = configuration.getFirstStack();
		return stack.size() >= 2 && !stack.get(stack.size() - 2).isRoot();
	}

	@Override
	public String id() {
		
		return "LEFT-"+label;
	}
  
	
}
