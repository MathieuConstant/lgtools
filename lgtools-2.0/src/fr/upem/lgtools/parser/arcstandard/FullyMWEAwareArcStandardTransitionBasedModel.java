/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;

import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * @author Matthieu Constant
 *
 */
public class FullyMWEAwareArcStandardTransitionBasedModel extends
		ArcStandardTransitionBasedParserModel{

	private final String COMPLETE = "CMP";
	private final String MERGE_BOTH = "MB";
	private final String MERGE = "ME"
			;
	
	public FullyMWEAwareArcStandardTransitionBasedModel(FeatureMapping fm,
			DepTreebank tb) {
		super(fm, tb);
	}
	

	public FullyMWEAwareArcStandardTransitionBasedModel(String filename)
			throws IOException {
		super(filename);
	}

	

	@Override
	public Configuration<DepTree> getInitialConfiguration(Sentence s) {		
		return new Configuration<DepTree>(s, new DepTree(s.getUnits().size() + 1), 1, 2);
	}

	
	
	
	private boolean lexicalTreeReceivesSyntacticHead(Unit u, Sentence s){
		
		if(u.hasGoldSyntacticHead()){
			return true;
		}
		if(!u.isMWE()){
			return false;
		}
		return !u.isFixedMWE(s);
	}
	
	
	
	
	private String getMergeBoth(Configuration<DepTree> configuration){
		
		Deque<Unit> stack = configuration.getFirstStack();
		Deque<Unit> lexStack = configuration.getSecondStack();
		if(stack.size() > 2 && lexStack.size() > 2){
			Unit u1 = stack.pop();
			Unit u2 = stack.peek();
			stack.push(u1);
			//the two units on top of stask must have a lexical head
			if(!u2.hasGoldLexicalHead() || !u1.hasGoldLexicalHead()){
				return null;
			}
			int l1 = u2.getGoldLHead();
			int l2 = u1.getGoldLHead();
			
			Unit u12 = lexStack.pop();
			Unit u22 = lexStack.peek();
			lexStack.push(u12);
			//System.err.println(u1+"??"+u2);
			Sentence s = configuration.getSentence();
			//the two units (and their respective internal components) to be merged cannot have syntactic heads
			if(lexicalTreeReceivesSyntacticHead(u1, s) || lexicalTreeReceivesSyntacticHead(u2, s)){
				
				return null;
			}
			//the units on top-2 must be the same on the two stacks
			if(u12.getId() != u1.getId() || u22.getId() !=  u2.getId()){
			  return null;	
			}
			
			
			//the lexical head of the two units must be the same
			if(l1 == l2){				
				return configuration.getUnit(l1).getPos();
			}
			
		}
		return null;
	}
	
	private String getMerge(Configuration<DepTree> configuration){
		
		Deque<Unit> stack = configuration.getSecondStack();
		if(stack.size() > 2){
			Unit u1 = stack.pop();
			Unit u2 = stack.peek();
			stack.push(u1);
			if(!u2.hasGoldLexicalHead() || !u1.hasGoldLexicalHead()){
				return null;
			}
			
			Sentence s = configuration.getSentence();
			if(!lexicalTreeReceivesSyntacticHead(u1, s) && !lexicalTreeReceivesSyntacticHead(u2, s)){
				return null;
			}
			
			int l1 = u2.getGoldLHead();
			int l2 = u1.getGoldLHead();
			
			
			if(l1 == l2){				
				return configuration.getUnit(l1).getPos();
			}
		}
		return null;
	}
	
	private boolean completeIsPossible(Configuration<DepTree> configuration){
		Deque<Unit> stack = configuration.getSecondStack();
		if(stack.size() > 1){
			Unit u = stack.peek();
			//System.err.println(u+"--"+u.getLheadId());
			if(!u.hasGoldLexicalHead()){
				return true;
			}
		}
		return false;
		
	}
	
	
	@Override
	public Transition<DepTree> staticOracle(Configuration<DepTree> configuration){
		String label;
		
		
		//MERGE_BOTH
		label = getMergeBoth(configuration);
		if(label != null){
			return transitions.getTransition(MERGE_BOTH, label);
		}
		
		//MERGE
		label = getMerge(configuration);
		 //System.err.println(label);
		if(label != null){
			return transitions.getTransition(MERGE, label);
		}
		
		//if COMPLETE is possible
		if(completeIsPossible(configuration)){
			return transitions.getTransition(COMPLETE, null);
		}
		
		
		//static oracle du arc-standard		
		return super.staticOracle(configuration);
	}
	
	
	
	
	
	
	@Override
	protected FeatureExtractor<DepTree> getFeatureExtractor(FeatureMapping fm) {
		//TO MODIFY AT ONE MOMENT
		return new FullDefaultFeatureExtractor(fm);
	}

	
	private String getMergeBothUnitLabel(Unit u, Sentence s){
		
		if(!u.isFixedMWE(s)){
			return null;
		}
		return u.getPos();
	}
	
	
	private String getMergeUnitLabel(Unit u, Sentence s){
		if(!u.isMWE()){
			return null;
		}
		return u.isFixedMWE(s)?null:u.getPos();		
	}
	
	
	
	

	@Override
	protected Transition<DepTree> createLabelDependentTransition(Unit unit,
			Sentence s) {
              String label = getMergeBothUnitLabel(unit,s);
              
              if(label != null){
            	  return createTransition(MERGE_BOTH, label);
              }
              
              label = getMergeUnitLabel(unit, s);
             
              if(label != null){
            	 
            	  return createTransition(MERGE,label);
              }              
              
			return super.createLabelDependentTransition(unit, s);
	}


	@Override
	protected Transition<DepTree> createTransition(String type, String label) {
		if(MERGE_BOTH.equals(type)){
			return new MergeBothTransition(MERGE_BOTH,label); 
		}
		if(MERGE.equals(type)){
			return new MergeTransition(MERGE,label);
		}
		if(COMPLETE.equals(type)){
			return new CompleteTransition(COMPLETE);
		}
		return super.createTransition(type, label);
	}


	@Override
	protected Collection<Transition<DepTree>> createLabelIndependentTransitions() {
		Collection<Transition<DepTree>> transitions = new LinkedList<Transition<DepTree>>();
		transitions.add(createTransition(SHIFT,null));
		transitions.add(createTransition(COMPLETE,null));
        return transitions;
	}
	

}
