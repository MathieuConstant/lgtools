/**
 * 
 */
package fr.upem.lgtools.parser;

import fr.upem.lgtools.parser.features.FeatureVector;
import fr.upem.lgtools.parser.model.TransitionBasedModel2;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;

/**
 * @author Mathieu
 *
 */
public class PerceptronTransitionBasedSystem<T> extends TransitionBasedSystem<T> {

	public PerceptronTransitionBasedSystem(TransitionBasedModel2<T> tbm) {
		super(tbm);
	}

	
	@Override
	public void staticOracleTrain(DepTreebank tb, String modelFilename, int iterations){
   	 tb = tbm.filter(tb);
   	 Model averaged = new Model(tbm.getFeatureCount(),tbm.getLabelCount());
   	 int max = iterations * tb.size();
   	 int step = max;
   	 for(int i = 0 ; i < iterations ; i++){
   		 System.err.println("Iteration "+ (i+1));
   		 int cnt = 0;
   		 int sent = 0;
   		 int total = 0;
   		 for(Sentence gold:tb.shuffle()){
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
   					 c.getHistory().add(pt.id());
   					 cnt++;
   				 }
   				 else{  //false prediction
   					 tbm.update(fv,ot,pt,averaged,step/(double)max);
   					 
   					 c = ot.perform(c);
   					 c.getHistory().add(ot.id());
   				 }
   				 total++;
   				 //System.err.println(c);
   				 //System.err.println(tbm);
   			 }
   			 step--;
   			 sent++;
   		 }
   		 tbm.setModel(averaged);
   		 TransitionBasedModel2.save(tbm, modelFilename+"."+(i+1));
   		 System.err.println("Accuracy on training transition sequence: "+((double)cnt)/total+ "  ("+cnt+"/"+total+")");
	       	 System.err.println("Number of sentences: "+sent);
   	 }
   	 TransitionBasedModel2.save(tbm, modelFilename+".final");
   	 System.err.println("Done.");
   	 
    }
	
	
}
