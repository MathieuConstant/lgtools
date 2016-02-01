/**
 * 
 */
package fr.upem.lgtools.parser;

import java.io.IOException;

import fr.upem.lgtools.parser.features.FeatureVector;
import fr.upem.lgtools.parser.model.TransitionBasedModel2;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;

/**
 * @author Mathieu
 *
 */
public class PerceptronTransitionBasedSystem<T extends Analysis> extends TransitionBasedSystem<T> {

	public PerceptronTransitionBasedSystem(TransitionBasedModel2<T> tbm) {
		super(tbm);
	}

	
	@Override
	public void inexactSearchTrain(DepTreebank tb, DepTreebank dev, String modelFilename, int iterations, int k) throws IOException{
		tb = tbm.filter(tb);
	   	 Model averaged = new Model(tbm.getFeatureCount(),tbm.getLabelCount());
	   	int step = 1;
	   	for(int i = 0 ; i < iterations ; i++){
	   		 System.err.println("Iteration "+ (i+1));
	   		 int cnt = 0;
	   		 int sent = 0;
	   		 int total = 0;
	   		for(Sentence gold:tb.shuffle()){
	   			sent++;
	   			if(sent % 1000 == 0){System.err.println("Processed "+ sent+ " sentences");}
	   			ParseHypothesis<T> hyp = beamSearchParse(gold.getTokens(), k, true);
	   			if(!hyp.isGold()){
	   				tbm.update(hyp.getFeatures(),hyp.getGoldHypothesis().getTransition(),hyp.getTransition(),averaged,step);
	   				System.err.println(hyp.getGoldHypothesis().getTransition()+"=="+hyp.getTransition());
	   			}
	   			step++;
	   			cnt += hyp.getConfiguration().getHistory().size();
	   			total += gold.getTokens().size()*2;	   			
	   		}
	   		System.err.println("Accuracy on training transition sequence: "+((double)cnt)/total+ "  ("+cnt+"/"+total+")");
	       	 System.err.println("Number of sentences: "+sent);	   	    
	   	}   	
	   	tbm.setModel(tbm.getAveragedModel(averaged,step));
	   	 tbm.save(modelFilename+".final");
	   	 System.err.println("Done.");
	}
	
	
	
	@Override
	public void staticOracleTrain(DepTreebank tb, DepTreebank dev, String modelFilename, int iterations) throws IOException{
   	 tb = tbm.filter(tb);
   	 Model averaged = new Model(tbm.getFeatureCount(),tbm.getLabelCount());
   	 int step = 1;
   	 for(int i = 0 ; i < iterations ; i++){
   		 System.err.println("Iteration "+ (i+1));
   		 int cnt = 0;
   		 int sent = 0;
   		 int total = 0;
   		 boolean stop = false;
   		 for(Sentence gold:tb.shuffle()){
   			 sent++;
   			 if(sent % 1000 == 0){System.err.println("Processed "+ sent+ " sentences");}
   			 Configuration<T> c = tbm.getInitialConfiguration(gold.getTokens());
   			 stop = false;
   			 while(!c.isTerminal() && !stop){
   				 FeatureVector fv = tbm.extractFeatures(c);
   				 Transition<T> pt = tbm.getBestValidTransition(fv,c);
   				 Transition<T> ot = tbm.getBestCorrectTransition(fv,c);
   				 //System.err.println("OT "+ot);
   				 //System.err.println("PT "+pt);
   				 if(pt.equals(ot)){ // true prediction
   					 c = pt.perform(c); 
   					 c.getHistory().add(pt.id());
   					 cnt++;
   				 }
   				 else{  //false prediction
   					 tbm.update(fv,ot,pt,averaged,step);
   					 stop = false;//early update if stop == true
   					 if(!stop){
   					    c = ot.perform(c);
   					    c.getHistory().add(ot.id());
   					 }
   				 }
   				 total++;
   				 //System.err.println(c);
   				 //System.err.println(tbm);
   				step++;
   			 }
   			 
   			 
   		 }
   		 if(dev != null){
   			 Model old = tbm.getModel();
   			Model a = tbm.getAveragedModel(averaged,step);
   			tbm.setModel(a);
   			greedyParseTreebankAndEvaluate(dev);
   			a = null;
   			tbm.setModel(old);
   			
   		 }
   		 
   		 //TransitionBasedModel2.save(tbm, modelFilename+"."+(i+1));
   		 System.err.println("Accuracy on training transition sequence: "+((double)cnt)/total+ "  ("+cnt+"/"+total+")");
	       	 System.err.println("Number of sentences: "+sent);
   	 }
   	tbm.setModel(tbm.getAveragedModel(averaged,step));
   	 tbm.save(modelFilename+".final");
   	 System.err.println("Done.");
   	 
    }
	
	
}
