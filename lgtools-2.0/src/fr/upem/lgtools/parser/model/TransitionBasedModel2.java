package fr.upem.lgtools.parser.model;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.Model;
import fr.upem.lgtools.parser.TransitionSet;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.FeatureVector;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * 
 */

/**
 * @author Matthieu Constant
 *
 */
public abstract class TransitionBasedModel2<T> {
	protected final FeatureMapping features;
	private final Model model;
	protected final TransitionSet<T> transitions = new TransitionSet<T>();
	private final FeatureExtractor<T> extractor;
	
	
	
	
	
	
	// constructor used before training
	public TransitionBasedModel2(FeatureMapping fm,DepTreebank tb){
		createTransitions(tb);
		extractor = getFeatureExtractor(fm);
		features = fm;
		model = new Model(features.featureCapacity(),transitions.size());		
	}
	
	// constructor used before parsing
	public TransitionBasedModel2(String filename){		    
		    features = null;
			model = null;
	       /* TODO */	
			
			extractor = getFeatureExtractor(features);
			throw new UnsupportedOperationException("Not implemented yet");
	}
	

	public void setModel(Model newModel){
		model.setModel(newModel);
	}
	
	
	public int getFeatureCount(){
		return model.getFeatureCount();
	}
	
	public int getLabelCount(){
		return model.getLabelCount();
	}
	
	
    public static <T> void save(TransitionBasedModel2<T> tbm, String filename){
		
	}
	
	
	//external abstract methods
	abstract public Configuration<T> getInitialConfiguration(List<Unit> units);
	abstract public Transition<T> staticOracle(Configuration<T> configuration);
	abstract public DepTreebank filter(DepTreebank tb); //used to filter gold treebank for training, with respect to system constraints
	abstract public void updateSentenceAfterAnalysis(Sentence s,T analysis);
	
	//internal abstract methods
	abstract protected FeatureExtractor<T> getFeatureExtractor(FeatureMapping fm);
	abstract protected Transition<T> createLabelDependentTransition(Unit unit);	
	abstract protected Transition<T> createTransition(String type, String label);	
	abstract protected Collection<Transition<T>> createLabelIndependentTransitions();	
	
	
		
	private void createTransitions(DepTreebank tb){	
		for(Transition<T> t:createLabelIndependentTransitions()){
			addTransition(t);
		}
		for(Sentence s:tb){
			for(Unit u:s.getUnits()){
				addTransition(createLabelDependentTransition(u));
			}
		}		
	}
	
	
	private void addTransition(Transition<T> transition){
		if(transition == null){
			return;
		}
		if(!transitions.contains(transition)){
			transitions.add(transition);	
		}
		
	}
	
	
	public FeatureVector extractFeatures(Configuration<T> configuration){
		return extractor.perform(configuration);
	}

	
	private Set<Transition<T>> getValidTransitions(Configuration<T> config){
		Set<Transition<T>> set = new HashSet<Transition<T>>();
		for(Transition<T> t:transitions){
			if(t.isValid(config)){
				set.add(t);
			}
		}
		return set;
	}
	
	
	private Set<Transition<T>> getCorrectTransitions(Configuration<T> config){
		Set<Transition<T>> set = new HashSet<Transition<T>>();
		set.add(staticOracle(config));
		return set;
	}
	
	
	
	
	public Transition<T> getBestValidTransition(FeatureVector fv,Configuration<T> c){		
		return getBestTransition(fv, getValidTransitions(c));
	}
	
	public Transition<T> getBestCorrectTransition(FeatureVector fv, Configuration<T> c){
		return getBestTransition(fv,getCorrectTransitions(c));
	}
	
	
	private Transition<T> getBestTransition(FeatureVector feats, Set<Transition<T>> possibleTransitions){
		double bestSc = -Double.MAX_VALUE;
		Transition<T> bestTransition = null;
		for(int l = 0 ; l < transitions.size(); l++){
			Transition<T> t = transitions.getTransition(l);
			if(possibleTransitions.contains(t)){ 
				//System.out.println(authorizedTransitions);
			  double sc = model.score(feats,l);
			  //System.out.println(sc);
			  if(sc > bestSc){
				bestSc = sc;
				bestTransition = t;
			  }
			  //System.out.println(bestLabel);
			}
		}
		return bestTransition;
		
		
	}
		
	
	
	public void update(FeatureVector feats, Transition<T> ot, Transition<T> pt){
		model.updatePlus(feats, transitions.getTransitionIndex(ot));
		model.updateMinus(feats, transitions.getTransitionIndex(pt));		
	}
	
	public void update(FeatureVector feats, Transition<T> ot, Transition<T> pt,Model averaged,double coef){
		update(feats,ot,pt);
		averaged.updatePlus(feats, transitions.getTransitionIndex(ot),coef);
		averaged.updateMinus(feats, transitions.getTransitionIndex(pt),coef);	
		
	}
	
	
	
	
	@Override
	public String toString() {		
		return transitions.toString()+"\n"+features.toString()+"\n"+model.toString();
	}
	
}
