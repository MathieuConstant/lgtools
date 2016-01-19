/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepArc;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class LeftArcTransition extends LabeledTransition<DepTree> {
		
	public LeftArcTransition(String type, String label) {
		super(type, label);
	}

	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		Unit s0 = stack.pop();
		Unit s1 = stack.pop();
		DepTree t = configuration.getAnalyses();
		t.addArc(new DepArc(s0.getId(),label,s1.getId()));
		stack.push(s0);
		return configuration;
	}

	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		if(stack.size() < 2){
			return false;
		}
		if(stack.size() == 2 && stack.peekLast().isRoot()){
			return false;
		}
		return true;
	}

	
  
	
}
