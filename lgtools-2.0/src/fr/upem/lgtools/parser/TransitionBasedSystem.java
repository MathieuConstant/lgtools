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
public class TransitionBasedSystem<T> {
     private final TransitionBasedModel2<T> tbm;
     
     
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
 		}
 		return c.getAnalyses();
 	}
    
     
   
 	
 	public DepTreebank greedyParseTreebank(DepTreebank tb) throws FileNotFoundException{
 		
 		int cnt = 0;
 		for(Sentence s:tb){
 			T analysis = greedyParse(s.getTokens());
 			tbm.updateSentenceAfterAnalysis(s,analysis);
 			cnt++;
 			if(cnt%1000 == 0){
 				System.err.println(cnt+" sentences parsed.");
 			} 			
 		} 
 		
 		return tb;
 		
 	}
    
 	
public DepTreebank greedyParseTreebankAndEvaluate(DepTreebank tb) throws FileNotFoundException{
 	   tb = greedyParseTreebank(tb); 
 	   ParsingAccuracy eval = ParsingAccuracy.computeParsingAccuracy(tb);
	   System.err.println(eval);
 		return tb;
 		
 	}
 	
 	
    
 	
 	
     
     
     public void staticOracleTrain(DepTreebank tb, String modelFilename, int iterations){
    	 tb = tbm.filter(tb);
    	 for(int i = 0 ; i < iterations ; i++){
    		 System.err.println("Iteration "+ (i+1));
    		 int cnt = 0;
    		 int sent = 0;
    		 int total = 0;
    		 for(Sentence gold:tb){
    			 if(sent % 1000 == 0){System.err.println("Processed "+ sent+ " sentences");}
    			 Configuration<T> c = tbm.getInitialConfiguration(gold.getTokens());
    			 while(!c.isTerminal()){
    				 FeatureVector fv = tbm.extractFeatures(c);
    				 Transition<T> pt = tbm.getBestValidTransition(fv,c);
    				 Transition<T> ot = tbm.getBestCorrectTransition(fv,c);
    				 //System.err.println("OT "+ot);
    				 //System.err.println("PT "+pt);
    				 if(pt.equals(ot)){ // true prediction
    					 c = pt.perform(c); 
    					 cnt++;
    				 }
    				 else{  //false prediction
    					 tbm.update(fv,ot,pt);
    					 c = ot.perform(c);  
    				 }
    				 total++;
    				 //System.err.println(c);
    				 //System.err.println(tbm);
    			 }
    			 sent++;
    		 }
    		 TransitionBasedModel2.save(tbm, modelFilename+"."+(i+1));
    		 System.err.println("Accuracy on training transition sequence: "+((double)cnt)/total+ "  ("+cnt+"/"+total+")");
	       	 System.err.println("Number of sentences: "+sent);
    	 }
    	 TransitionBasedModel2.save(tbm, modelFilename+".final");
    	 System.err.println("Done.");
    	 
     }
     
	
}
