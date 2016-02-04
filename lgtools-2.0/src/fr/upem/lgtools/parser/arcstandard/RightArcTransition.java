/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepArc;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.transitions.LabeledTransition;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class RightArcTransition extends LabeledTransition<DepTree> {


	
	
	/**
	 * @param label
	 */
	public RightArcTransition(String type, String label) {
		super(type, label);
	}

	
	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		Unit s0 = stack.pop();
		Unit s1 = stack.peek();
		DepTree t = configuration.getAnalyses();		
		t.addArc(new DepArc(s1.getId(),label,s0.getId()));
		//configuration.getHistory().add();		
		return configuration;
	}

	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		return stack.size() >= 2;
	}

	
  
	
}
