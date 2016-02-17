package fr.upem.lgtools.parser.arcstandard;

import java.util.ArrayList;
import java.util.Collection;
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

	public static Configuration<DepTree> performMerge(Configuration<DepTree> configuration, String label, Unit u1, Unit u0, Collection<Deque<Unit>> stacks){
				
		Unit u = Utils.mergeUnitsAndAdd(u1,u0,configuration.getUnits());
		if(label != null && !label.equals("")){
			u.setPos(label);
		}
		DepTree tree = configuration.getAnalyses();
		tree.addEdgeWithLinks(u1.getId(),u0.getId());
		for(Deque<Unit> stack:stacks){
			stack.push(u);
		}

		return configuration;
	}
	
	
	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		Deque<Unit> stack = configuration.getFirstStack();
		Deque<Unit> lexStack = null;
		boolean hasMoreStacks = configuration.stackCount() >= 2;
		if(hasMoreStacks){
			lexStack = configuration.getSecondStack();	
		}
		
		ArrayList<Deque<Unit>> stacks = new ArrayList<Deque<Unit>>();
		Unit u0 = stack.pop();
		Unit u1 = stack.pop();
		stacks.add(stack);
		if(hasMoreStacks){
			lexStack.pop();
			lexStack.pop();
			stacks.add(lexStack);
		}
		
		//System.err.println(stacks.size());
		return performMerge(configuration,label,u1,u0,stacks);
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
		if(tree.nodeHasChildren(u0.getId()) || tree.nodeHasChildren(u1.getId())){ //if u0 or u1 has children, transition not valid (as we're dealing with fixed expressions)
			res = false;
		}
		
		stack.push(u1);  //stack in initial state of this method call
		stack.push(u0);
		
		boolean hasMoreStacks = configuration.stackCount() >= 2;
		if(hasMoreStacks){
			Deque<Unit> lexStack = configuration.getSecondStack();
			if(lexStack.size() <= 2){
				res = false;
			}
		}
		/*if(res){
			System.err.println("ICI");
		}*/
		return res;
	}

	
	
	
}
