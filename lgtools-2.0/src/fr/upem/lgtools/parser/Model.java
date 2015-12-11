package fr.upem.lgtools.parser;

import java.util.HashMap;
import java.util.Set;

public class Model<T> {
	private final HashMap<String,Double> weights = new HashMap<String, Double>();
	private final Set<Transition<T>> transitions;
	
	public Model(Set<Transition<T>> transitions){
		this.transitions = transitions;
	}
	
	public double get(String feature,Transition<T> transition){
		Double w = weights.get(feature);
		if(w == null){
			return 0.0;
		}
		return w;
	}
	

	public Transition<T> oracle(Configuration<T> configuration){
	    return null;	
	}
	
}
