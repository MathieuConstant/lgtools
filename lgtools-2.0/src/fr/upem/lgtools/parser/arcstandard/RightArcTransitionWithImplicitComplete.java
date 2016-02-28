/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.util.Deque;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class RightArcTransitionWithImplicitComplete extends RightArcTransition {
	boolean withImplicit = false;



	
	public RightArcTransitionWithImplicitComplete(String type, String label) {
		super(type, label);
	}
	
	Unit getTargetUnitForCompletion(Configuration<DepTree> configuration){
		Deque<Unit> stack = configuration.getFirstStack();
		return ParserUtils.getLexicalUnitFromComponent(stack.peek(), configuration.getAnalyses(), configuration.getSentence());
	}
	
	void completeUnit(Unit u,Configuration<DepTree> configuration){
		Deque<Unit> stack = configuration.getSecondStack();
		stack.pop();
	}
	
	
	@Override
	public Configuration<DepTree> perform(Configuration<DepTree> configuration) {
		withImplicit = false;
		Unit u = getTargetUnitForCompletion(configuration);
	
		configuration = super.perform(configuration);
		
		if(ParserUtils.unitIsSyntacticallyCompleted(u, configuration.getAnalyses(),configuration.getSentence())){
			completeUnit(u,configuration);
			withImplicit = true;
		}
		return configuration;
	}
	
	@Override
	public String toString(){
		String res = super.toString();
		return withImplicit?res+"-CMP":res;
	}
	

	
}
