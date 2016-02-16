package fr.upem.lgtools.parser.arcstandard;

import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.transitions.LabeledTransition;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.Utils;

public class MergeBothTransition extends LabeledTransition<DepTree> {

	public MergeBothTransition(String type,String label) {
		super(type,label);
	}

	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		DepTree tree = configuration.getAnalyses();
		Unit u0 = stack.pop();
		Unit u1 = stack.pop();
		Unit u = Utils.mergeUnitsAndAdd(u1,u0,configuration.getUnits());
		if(label != null && !label.equals("")){
		   u.setPos(label);
		   //System.err.println(label);
		}
		//System.err.println(u+" "+u1+" "+u0);
		//configuration.addUnit(u);
		tree.addEdgeWithLinks(u1.getId(),u0.getId());
		stack.push(u);
		return configuration;
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
		if(tree.nodeHasChildren(u0.getId()) || tree.nodeHasChildren(u1.getId())){ //if u0 or u1 has children, transition not valid
			res = false;
		}
		
		stack.push(u1);  //stack in initial state of this method call
		stack.push(u0);
		
		return res;
	}

	
	
	
}
