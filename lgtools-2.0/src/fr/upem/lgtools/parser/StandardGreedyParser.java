package fr.upem.lgtools.parser;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.upem.lgtools.parser.features.Feature;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.DepTreebankFactory;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

public class StandardGreedyParser<T> {
	//final private Configuration<T> configuration; //no new instance, just update
	final private TransitionBasedModel<T> model;

	public StandardGreedyParser(TransitionBasedModel<T> model) {		
		this.model = model;
	}
	
	
	
	
	
	
	public T parse(List<Unit> units){
		Configuration<T> c = model.getInitialConfiguration(units);
		//System.out.println(model); 
		while(!c.isTerminal()){
			FeatureExtractor<T> extractor = model.getFeatureExtractor();	       				
			List<Feature> feats = model.getFeatures(extractor.perform(c));	       				
				
			Transition<T> t = model.getBestTransition(c,getValidTransitions(c),feats); 
			c = t.perform(c);
		}
		return c.getAnalyses();
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
		set.add(model.staticOracle(config));
		return set;
	}
	
	
	//Algorithm from Goldberg and Nivre TACL 2013
	
	public void train(DepTreebank tb, int N,String modelFilename) throws IOException{
		  tb = DepTreebankFactory.filterNonProjective(tb);
		  
	      for(int i = 0 ; i < N ; i++){
	    	   System.err.println("Iteration "+(i+1));
	    	    int cnt = 0;
	    	    int sent = 0;
	    	    int total =0;
	       		for(Sentence gold:tb){
	       			sent++;
	       			if(sent % 1000 == 0){System.err.println("Processed "+ sent+ " sentences");}
	       			Configuration<T> c = model.getInitialConfiguration(gold.getTokens());
	       			while(!c.isTerminal()){
	       				FeatureExtractor<T> extractor = model.getFeatureExtractor();	       				
	       				List<Feature> feats = model.getFeatures(extractor.perform(c));	       				
	       				
	       				 
	       				int tpidx = model.getBestTransitionIdx(c,getValidTransitions(c),feats);
	       				Transition<T> tp = model.getTransitions().get(tpidx);
	       				Set<Transition<T>> correct = getCorrectTransitions(c,gold);
	       				total++; 
	       				int toidx = model.getBestTransitionIdx(c,correct,feats);
	       				if(correct.contains(tp)){//good prediction	       					
	       					c = tp.perform(c);
	       					cnt++;
	       					//System.out.println("pred:"+tp.id());	       					
	       				}
	       				else{
	       					//update model
	       					model.getModel().update(feats, toidx);
	       					model.getModel().update(feats, tpidx,false);
	       					Transition<T> to = model.getTransitions().get(toidx);
	       					
	       					c = to.perform(c);
	       					//System.out.println("oracle:"+to.id());
	       				}
	       				//System.err.println("stack:"+c.getFirstStack());
	       				//System.err.println("buffer:"+c.getFirstBuffer());
	       				//cnt++;
	       				//if(cnt == 16){
	       					//System.exit(0);
	       				//}
	       			}	       			
	       		}
	       		
	       		model.saveModel(modelFilename+"."+i);
	       		System.err.println("Accuracy on training transition sequence: "+((double)cnt)/total+ "  ("+cnt+"/"+total+")");
	       		System.err.println("Number of sentences: "+sent);
	      }
	      model.saveModel(modelFilename+".final");
	      System.err.println("Done.");
	}
	

}
