/**
 * 
 */
package fr.upem.lgtools.parser;

import java.io.FileNotFoundException;
import java.util.List;

import fr.upem.lgtools.evaluation.ParsingAccuracy;
import fr.upem.lgtools.parser.features.FeatureVector;
import fr.upem.lgtools.parser.model.TransitionBasedModel2;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;


/**
 * @author Mathieu
 *
 */
public abstract class TransitionBasedSystem<T> {
     final TransitionBasedModel2<T> tbm;
     
     
     public TransitionBasedSystem(TransitionBasedModel2<T> tbm){
    	 this.tbm = tbm;
     }
	
     
     public T greedyParse(List<Unit> units){
 		Configuration<T> c = tbm.getInitialConfiguration(units);
 		//System.out.println(model); 
 		while(!c.isTerminal()){
 			FeatureVector fv = tbm.extractFeatures(c);
			Transition<T> t = tbm.getBestValidTransition(fv,c); 
 			c = t.perform(c);
 			c.getHistory().add(t.id());
 		}
 		return c.getAnalyses();
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
 			tbm.updateSentenceAfterAnalysis(s,analysis);
 			cnt++;
 			if(cnt%1000 == 0){
 				System.err.println(cnt+" sentences parsed.");
 			} 			
 		} 
 		
 		return tb;
 		
 	}
    
 	
public DepTreebank parseTreebankAndEvaluate(DepTreebank tb, ParsingMethod<T> pm) throws FileNotFoundException{
 	   tb = parseTreebank(tb,pm); 
 	   ParsingAccuracy eval = ParsingAccuracy.computeParsingAccuracy(tb);
	   System.err.println(eval);
 		return tb;
 		
 	}
 	
 	
public DepTreebank oracleParseTreebankAndEvaluate(DepTreebank tb) throws FileNotFoundException{
	  return parseTreebankAndEvaluate(tb, new ParsingMethod<T>() {

		@Override
		public T parse(List<Unit> units) {
			return oracleParse(units);
		}
		  
	});
	}

    
 	
	
public DepTreebank greedyParseTreebankAndEvaluate(DepTreebank tb) throws FileNotFoundException{
  return parseTreebankAndEvaluate(tb, new ParsingMethod<T>() {

	@Override
	public T parse(List<Unit> units) {
		return greedyParse(units);
	}
	  
});
}

 	
     abstract public void staticOracleTrain(DepTreebank tb, String modelFilename, int iterations);
     
     
     
	
}
