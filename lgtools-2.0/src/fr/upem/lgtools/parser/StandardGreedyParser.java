package fr.upem.lgtools.parser;

import java.util.List;

import fr.upem.lgtools.parser.transitions.ArcStrandardParsingTransitions;
import fr.upem.lgtools.parser.transitions.SystemTransitions;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.Unit;

public class StandardGreedyParser<T> {
	//final private Configuration<T> configuration; //no new instance, just update
	final private TransitionBasedModel<T> model;
	final private SystemTransitions<T> map = new ArcStrandardParsingTransitions(labels);

	public StandardGreedyParser(TransitionBasedModel<T> model) {		
		this.model = model;
	}
	
	public void parse(List<Unit> units,T result){
		Configuration<T> c = new Configuration<T>(units,result,1,1); // initial configuration with one buffer and one stack
		
		while(!c.isTerminal()){
			Transition<T> t = map.getTransition(model.getBestLabel()); 
			c = t.perform(c);
		}
		
	}
	

}
