package fr.upem.lgtools.parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

public abstract class StandardGreedyParser<T> {
	//final private Configuration<T> configuration; //no new instance, just update
	final private TransitionBasedModel<T> model;

	public StandardGreedyParser(TransitionBasedModel<T> model) {		
		this.model = model;
	}
	
	//put abstrast
	private Configuration<T> getInitialConfiguration(List<Unit> units,T result){
		return new Configuration<T>(units,result,1,1); // initial configuration with one buffer and one stack
	}
	
	protected abstract T getInitialAnalysis();
	
	public void parse(List<Unit> units,T result){
		Configuration<T> c = getInitialConfiguration(units,result);
		
		while(!c.isTerminal()){
			Transition<T> t = model.getBestTransition(c,getValidTransitions(c)); 
			c = t.perform(c);
		}		
	}
	
	private Set<Transition<T>> getValidTransitions(Configuration<T> config){
		Set<Transition<T>> set = new HashSet<Transition<T>>();
		for(Transition<T> t:model.getTransitions()){
			if(t.isValid(config)){
				set.add(t);
			}
		}
		return set;
	}
	
	private Set<Transition<T>> getCorrectTransitions(Configuration<T> config,Sentence gold){
		Set<Transition<T>> set = new HashSet<Transition<T>>();
		
		return set;
	}
	
	
	//Algorithm from Goldberg and Nivre TACL 2013
	
	public void train(DepTreebank tb, int N){
	      for(int i = 0 ; i < N ; i++){
	       		for(Sentence gold:tb){
	       			T result = getInitialAnalysis();
	       			Configuration<T> c = getInitialConfiguration(gold.getTokens(),result);
	       			while(!c.isTerminal()){
	       				Transition<T> tp = model.getBestTransition(c,getValidTransitions(c));
	       				Set<Transition<T>> correct = getCorrectTransitions(c,gold);
	       				Transition<T> to = model.getBestTransition(c,correct);
	       				if(correct.contains(tp)){//good prediction
	       					c = tp.perform(c);
	       				}
	       				else{
	       					//update model
	       					
	       					c = to.perform(c);
	       				}
	       				
	       			}
	       		}	       		
	      }
	}
	

}
