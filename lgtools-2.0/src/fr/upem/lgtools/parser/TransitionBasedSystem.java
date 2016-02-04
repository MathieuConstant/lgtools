/**
 * 
 */
package fr.upem.lgtools.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.upem.lgtools.evaluation.ParsingAccuracy;
import fr.upem.lgtools.evaluation.ParsingResult;
import fr.upem.lgtools.parser.features.FeatureVector;
import fr.upem.lgtools.parser.model.TransitionBasedModel;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;


/**
 * @author Mathieu
 *
 */
public abstract class TransitionBasedSystem<T extends Analysis> {
     final TransitionBasedModel<T> tbm;
     
     
     public TransitionBasedSystem(TransitionBasedModel<T> tbm){
    	 this.tbm = tbm;
     }
	
     
     public T greedyParse(List<Unit> units){
 		Configuration<T> c = tbm.getInitialConfiguration(units);
 		//System.out.println(model); 
 		while(!c.isTerminal()){
 			FeatureVector fv = tbm.extractFeatures(c);
			Transition<T> t = tbm.getBestValidTransition(fv,c); 
 			c = t.perform(c);
 			c.getHistory().add(t.id()); // do we keep it?
 		}
 		return c.getAnalyses();
 	}
    
     
     private boolean beamHasOnlyTerminalConfigurations(LinkedList<ParseHypothesis<T>> beam){
    	 for(ParseHypothesis<T> h:beam){
    		 if(!h.getConfiguration().isTerminal()){
    			 return false;
    		 }
    	 }
    	 return true;
     }
     
     
     /*
     private FeatureVector addFeatureVectors(FeatureVector fv1, FeatureVector fv2){
    	 FeatureVector fv = new FeatureVector(tbm.getFeatures());
    	 for(Feature f:fv1){
    		 fv.add(f);
    	 }
    	 for(Feature f:fv2){
    		 fv.add(f);
    	 }
    	 
    	 return fv;
    	 
     }
     */
     
     
     private static <T extends Analysis> LinkedList<ParseHypothesis<T>> prune(LinkedList<ParseHypothesis<T>> newBeam, int k){
    	 LinkedList<ParseHypothesis<T>> beam = new LinkedList<ParseHypothesis<T>>();
		 Iterator<ParseHypothesis<T>> it = newBeam.iterator();
		 for(int i = 0 ; i < k ; i++){
			 if(it.hasNext()){
				 ParseHypothesis<T> ph = it.next(); 
			    beam.add(ph);
			    //System.err.print(ph.getScore()+ "#");
			 }
		 }
		 return beam;
     }
     
     
     private static <T extends Analysis> boolean goldIsOffTheBeam(List<ParseHypothesis<T>> beam){
    	 
    	 for(ParseHypothesis<T> h:beam){
    		if(h.isGold()){
    			return false;
    		} 
    	 }    	 
    	 return true;
     }
     
     
     public ParseHypothesis<T> beamSearchParse(List<Unit> units, int k, boolean returnWhenFail){
    	 Configuration<T> c0 = tbm.getInitialConfiguration(units);
    	 ParseHypothesis<T> h0 = new ParseHypothesis<T>(c0, 0.0, new FeatureVector(tbm.getFeatures()),null,true,null);
    	 LinkedList<ParseHypothesis<T>> beam = new LinkedList<ParseHypothesis<T>>();
    	 beam.add(h0);
    	 ParseHypothesis<T> gold = h0;
    	     	 
    	 //System.err.println(units);
    	 
    	 while(!beamHasOnlyTerminalConfigurations(beam)){    	     
    		 
    		 LinkedList<ParseHypothesis<T>> newBeam = new LinkedList<ParseHypothesis<T>>();
    		 for(ParseHypothesis<T> h:beam){
    			 //System.err.println("START="+h);
    			 Configuration<T> c = h.getConfiguration();
    			 FeatureVector fv = tbm.extractFeatures(c);
    			 Set<Transition<T>> transitions = tbm.getValidTransitions(c);
    			 //System.err.println(transitions);
    			 if(transitions.isEmpty()){
    				 newBeam.add(h);
    			 }
    			 else{  
    				 
    				 for(Transition<T> t:transitions){
    				     double newScore = tbm.getScore(fv,t);
    				     Configuration<T> newConfig = new Configuration<T>(c); 
    				     //System.err.println("NEW="+newConfig);
    				     //System.err.println("OLD="+c);
    				     newConfig = t.perform(newConfig);
    				     newConfig.getHistory().add(t.id());
    				     boolean isGold = h.isGold();
    				     if(returnWhenFail && isGold){
    				    	 isGold = newConfig.getAnalyses().isGold(units);    
    				    	 //System.err.println(isGold);
    				     }    				     
    				     ParseHypothesis<T> newHyp = new ParseHypothesis<T>(newConfig, newScore, fv, h,isGold,t);
    				     
    				     newBeam.add(newHyp);
    				     if(isGold){
    				    	 gold = newHyp;
    				     }
    				 } 
    				 Collections.sort(newBeam);
    			 }    			 
    		 } 
    		 
    		 beam = prune(newBeam,k);
    		 //for(ParseHypothesis<T> h:beam){
    			 //System.err.println("DEST="+h);
    		 //}
    		 //System.err.println(beam);
    		 //ParseHypothesis<T> h1 = beam.getFirst();
    		 //System.err.println(h1.getTransition()+"="+h1.getConfiguration());
    		 
    		 if(returnWhenFail && goldIsOffTheBeam(beam)){
    			 //System.err.println("OFFBEAM");
    			 ParseHypothesis<T> h = beam.getFirst();
    			 h.setGoldHypothesis(gold);
    			 
    			 return h;    			 
    		 }
			 //System.err.println();
			 
			 //ParseHypothesis<T> ht = beam.getFirst();
			 //System.err.println(ht.getConfiguration());
    	 }
    	 ParseHypothesis<T> h = beam.getFirst();
    	 h.setGoldHypothesis(gold);
    	 return h;
    	 
     }
     
     
     
     public T oracleParse(List<Unit> units){
  		Configuration<T> c = tbm.getInitialConfiguration(units);
  		while(!c.isTerminal()){
  			Transition<T> t = tbm.staticOracle(c); 
  			c = t.perform(c);
  		}
  		return c.getAnalyses();
  	}
     
   
     private static interface ParsingMethod<A>{
    	 public A parse(List<Unit> units);
     }
     
     
 	
 	public DepTreebank parseTreebank(DepTreebank tb, ParsingMethod<T> pm) throws FileNotFoundException{
 		
 		int cnt = 0;
 		for(Sentence s:tb){
 			T analysis = pm.parse(s.getTokens());
 			//System.err.println(analysis);
 			tbm.updateSentenceAfterAnalysis(s,analysis); 			
 			cnt++;
 			if(cnt%300 == 0){
 				System.err.println(cnt+" sentences parsed.");
 			} 			
 		} 
 		
 		return tb;
 		
 	}
    
 	
public ParsingResult parseTreebankAndEvaluate(DepTreebank tb, ParsingMethod<T> pm) throws FileNotFoundException{
 	   tb = parseTreebank(tb,pm); 
 	   ParsingAccuracy eval = ParsingAccuracy.computeParsingAccuracy(tb);
	   System.err.println(eval);
 		return new ParsingResult(tb,eval);
 		
 	}
 	
 	
public ParsingResult oracleParseTreebankAndEvaluate(DepTreebank tb) throws FileNotFoundException{
	  return parseTreebankAndEvaluate(tb, new ParsingMethod<T>() {

		@Override
		public T parse(List<Unit> units) {
			return oracleParse(units);
		}
		  
	});
	}

    
 	
	
public ParsingResult greedyParseTreebankAndEvaluate(DepTreebank tb) throws FileNotFoundException{
  return parseTreebankAndEvaluate(tb, new ParsingMethod<T>() {

	@Override
	public T parse(List<Unit> units) {
		return greedyParse(units);
	}
	  
});
}



public ParsingResult beamSearchParseTreebankAndEvaluate(DepTreebank tb,final int k) throws FileNotFoundException{
	  return parseTreebankAndEvaluate(tb, new ParsingMethod<T>() {

		@Override
		public T parse(List<Unit> units) {
			return beamSearchParse(units, k,false).getConfiguration().getAnalyses();
		}
		  
	});
	}


   abstract void inexactSearchTrain(DepTreebank tb, DepTreebank dev, String modelFilename, int iterations, int beamSize) throws IOException;

   public void inexactSearchTrain(DepTreebank tb, String modelFilename, int iterations,int beamSize) throws IOException{
	   inexactSearchTrain(tb, null, modelFilename, iterations,beamSize);
   }
   

     abstract public void staticOracleTrain(DepTreebank tb, DepTreebank dev, String modelFilename, int iterations) throws IOException;
     
     public void staticOracleTrain(DepTreebank tb, String modelFilename, int iterations) throws IOException{
    	 staticOracleTrain(tb, null, modelFilename, iterations);
     }
     
     
	
}
