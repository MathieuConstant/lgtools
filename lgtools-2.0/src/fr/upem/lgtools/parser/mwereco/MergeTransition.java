/**
 * 
 */
package fr.upem.lgtools.parser.mwereco;

import java.util.ArrayList;
import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.transitions.LabeledTransition;
import fr.upem.lgtools.parser.transitions.TransitionUtils;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu
 *
 */
public class MergeTransition extends LabeledTransition<DepTree> {

	public MergeTransition(String type, String label) {
		super(type, label);		
	}

	
	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		if(stack.size() < 3){
			return false;
		}
		return true;
	}                           

	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		ArrayList<Deque<Unit>> stacks = new ArrayList<Deque<Unit>>();
		Unit u0 = stack.pop();
		Unit u1 = stack.pop();
		stacks.add(stack);
		return TransitionUtils.performMerge(configuration,label,u1,u0,stacks);
	}

}
