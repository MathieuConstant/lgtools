package fr.upem.lgtools.parser.arcstandard;

import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.transitions.AbstractTransition;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.Utils;

public class MergeTransition extends AbstractTransition<DepTree> {

	public MergeTransition(String type) {
		super(type);
	}

	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		DepTree tree = configuration.getAnalyses();
		Unit u0 = stack.pop();
		Unit u1 = stack.pop();
		Unit u = Utils.mergeUnits(u1,u0,configuration.getUnits());
		configuration.addUnit(u);
		tree.addEdgeWithLinks(u1.getId(),u0.getId());
		return null;
	}

	@Override
	public boolean isValid(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		DepTree tree = configuration.getAnalyses();
		if(stack.size() <= 2){
			return false;
		}
		boolean res = true;
		Unit u0 = stack.pop();
		Unit u1 = stack.pop();
		if(tree.getChildren(u0.getId()).size() > 0){ //if u0 has children, transition not valid
			res = false;
		}
		if(tree.getChildren(u1.getId()).size() > 0){ //if u1 has children, transition not valid
			res = false;
		}
		stack.push(u1);  //stack in initial state of this method call
		stack.push(u0);
		
		return res;
	}

	
	
	
}
