package fr.upem.lgtools.parser;

import java.util.List;

import fr.upem.lgtools.text.Unit;

public class StandardGreedyParser<T> {
	//final private Configuration<T> configuration; //no new instance, just update
	final private Model model;

	public StandardGreedyParser(Model model) {		
		this.model = model;
	}
	
	public void parse(List<Unit> units,T result){
		Configuration<T> c = new Configuration<T>(units,result,1,1);
		
	}
	

}
