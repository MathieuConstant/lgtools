/**
 * 
 */
package fr.upem.lgtools.parser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.upem.lgtools.parser.features.Feature;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashFeatureMapping;
import fr.upem.lgtools.parser.transitions.Transition;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public abstract class TransitionBasedModel<T> {
	private static final double EPS = 0.001; 
	private final Model model;
	private final List<Transition<T>> transitions;
	private final Map<String,Transition<T>> transitionsFromId = new HashMap<String, Transition<T>>();
	
	private final FeatureMapping featureMapping;
	
	
	
	
	
	//constructor for training stage
	public TransitionBasedModel(int nFeats,DepTreebank tb) {
			transitions = constructTransitions(tb);
			for(Transition<T> t:transitions){
				transitionsFromId.put(t.id(), t);
			}
			model = new Model(nFeats,transitions.size());
			this.featureMapping = new HashFeatureMapping(model.getFeatureCount());
	}
	
	
	
	//Constructor for parsing stage
	/* OLD MODEL
	  public TransitionBasedModel(String filename) throws IOException {	
	 
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
		int nFeats = in.readInt();
		int nLabels = in.readInt();
		this.transitions = new ArrayList<Transition<T>>();
		for(int l = 0 ; l < nLabels ; l++){
		   this.transitions.add(constructTransition(in.readUTF()));
		}
		
		this.model = new Model(nFeats,transitions.size());
		for(int f = 0 ; f < model.getFeatureCount() ; f++){
			for(int l = 0; l < model.getLabelCount() ; l++){
				model.set(f,l,in.readDouble());				
			}							
		}
		in.close();
		
		this.featureMapping = new HashFeatureMapping(model.getFeatureCount());
		
		
		
	}
	*/
	
	public TransitionBasedModel(String filename) throws IOException {	
		 
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
		int nFeats = in.readInt();
		int nLabels = in.readInt();
		this.transitions = new ArrayList<Transition<T>>();
		for(int l = 0 ; l < nLabels ; l++){
		   this.transitions.add(constructTransition(in.readUTF()));
		}
		
		this.model = new Model(nFeats,transitions.size());
		for(int f = 0 ; f < model.getFeatureCount() ; f++){
			int n = in.readInt();
			for(int i = 0; i < n ; i++){
				int l = in.readInt();
				model.set(f,l,in.readDouble());				
			}							
		}
		in.close();
		
		this.featureMapping = new HashFeatureMapping(model.getFeatureCount());
		
		
		
	}
		
	
	public Model getModel() {
		return model;
	}
	public abstract FeatureExtractor<T> getFeatureExtractor();
	
	public List<Transition<T>> getTransitions() {
		return transitions;
	}
	public FeatureMapping getFeatureMapping() {
		return featureMapping;
	}
	
	
				

	public AbstractList<Feature> getFeatures(final List<String> feats){
		return new AbstractList<Feature>() {

			@Override
			public Feature get(int index) {
				
				return new Feature(featureMapping.getFeatureId(feats.get(index)));
			}

			@Override
			public int size() {				
				return feats.size();
			}
			
		};
	}
	
	
	public Transition<T> getTransitionFromId(String transitionId){
		//System.out.println(transitionsFromId);
		Transition<T> t = transitionsFromId.get(transitionId);
		if(t == null){
			throw new IllegalArgumentException("Trannsition "+transitionId+ " does not exist!");
		}
		return t;
	}
	
	
	public Transition<T> getBestTransition(Configuration<T> config,Set<Transition<T>> authorizedTransitions,List<Feature> feats){
		return getTransitions().get(getBestTransitionIdx(config, authorizedTransitions, feats));
	}
	
	
	
	public int getBestTransitionIdx(Configuration<T> config,Set<Transition<T>> authorizedTransitions,List<Feature> feats){		
		
		//System.out.println(-Double.MAX_VALUE);
		double bestSc = -Double.MAX_VALUE;
		int bestLabel =-1;
		for(int l = 0 ; l < transitions.size(); l++){
			if(authorizedTransitions.contains(transitions.get(l))){ 
				//System.out.println(authorizedTransitions);
			  double sc = model.score(feats,l);
			  //System.out.println(sc);
			  if(sc > bestSc){
				bestSc = sc;
				bestLabel = l;
			  }
			  //System.out.println(bestLabel);
			}
		}
		return bestLabel;
		//return transitions.get(bestLabel);
	}
	
	/* memory costly
	public void saveModel(String filename) throws IOException{
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
		out.writeInt(model.getFeatureCount());
		out.writeInt(model.getLabelCount());
		for(int l = 0 ; l < model.getLabelCount() ; l++){
			out.writeUTF(transitions.get(l).id());
		}
		for(int f = 0 ; f < model.getFeatureCount() ; f++){
			for(int l = 0; l < model.getLabelCount() ; l++){
				out.writeDouble(model.get(f,l));				
			}							
		}
		out.close();
	}
	*/
	
	public void saveModel(String filename) throws IOException{
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
		out.writeInt(model.getFeatureCount());
		out.writeInt(model.getLabelCount());
		for(int l = 0 ; l < model.getLabelCount() ; l++){
			out.writeUTF(transitions.get(l).id());
		}
		for(int f = 0 ; f < model.getFeatureCount() ; f++){
			int cnt = 0;
			//loop used to count the number of non-zero values
			for(int l = 0; l < model.getLabelCount() ; l++){
				double v = model.get(f,l);
				if(v > EPS){
					cnt++;
				}
				//out.writeDouble(model.get(f,l));				
			}
			out.writeInt(cnt);
			for(int l = 0; l < model.getLabelCount() ; l++){
				double v = model.get(f,l);
				if(v > EPS){
					out.writeInt(l);
					out.writeDouble(v);
				}
				//				
			}
			
		}
		out.close();
	}
	
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("***** MODEL *****\n");
		sb.append("Labels: ");
		for(Transition<T> t:transitions){
			sb.append(",");
			sb.append(t.id());
		}
		sb.append("\n");
		for(int f = 0 ; f < model.getFeatureCount() ; f++){
			for(int l = 0; l < model.getLabelCount() ; l++){
				sb.append("\t").append(model.get(f,l));			
			}
			sb.append("\n");
		}
		return sb.toString();
		
	}

	
	public abstract Transition<T> constructTransition(String transitionId);
	public abstract List<Transition<T>> constructTransitions(DepTreebank tb);
	public abstract Transition<T> staticOracle(Configuration<T> t);	
	public abstract Configuration<T> getInitialConfiguration(List<Unit> units);
	
	
	
}
