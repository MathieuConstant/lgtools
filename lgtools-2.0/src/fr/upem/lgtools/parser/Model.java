package fr.upem.lgtools.parser;

import java.util.List;

import fr.upem.lgtools.parser.features.Feature;

public class Model {
	private final double[] weights;
	private final int nFeatures;
	private final int nLabels;
	
		 
	
	
	public Model(int nFeatures,int nLabels){
		this.nLabels = nLabels;
		weights = new double[nFeatures*nLabels];
		this.nFeatures = nFeatures;				
		
	}

	private int getId(int label, int feat){
		return nFeatures*label+ feat;		
	}
	
	
	
	/**
	 * 
	 * Sets a weight to a feature with respect to a label
	 * 
	 * @param feat
	 * @param label
	 * @param weight
	 */
	
	
	public void set(int feat, int label, double weight){
		if(feat >= nFeatures || feat < 0){
			throw new IllegalArgumentException("feature "+feat+" does not exist!");
		}
		if(label < 0 || label >= nLabels){
	    	throw new IllegalArgumentException("Label "+ label + " does not exist!");
	    }
		weights[getId(label, feat)] = weight;
	}
	
	/**
	 * gets the weight of a feature with respect to a label
	 * 
	 * @param feat
	 * @param label
	 * @return weight of a feature with respect to a label
	 */
	
	
	public double get(int feat, int label){
		if(feat >= nFeatures || feat < 0){
			throw new IllegalArgumentException("feature "+feat+" does not exist!");
		}
		if(label < 0 || label >= nLabels){
	    	throw new IllegalArgumentException("Label "+ label + "does not exist!");
	    }
		return weights[getId(label, feat)];
	}
	
	/**
	 * 
	 * @return the number of labels
	 */
	
	public int getLabelCount(){
		return nLabels;
	}
	
	/**
	 * 
	 * @return the number of features
	 */
	
	public int getFeatureCount(){
		return nFeatures;
	}
	
	
	
	
	
	/**
	 * 
	 * @param feature
	 * @param updatePlus indicates weither the update should be positive (true value) or negative (false value)
	 */
	
	private void update(Feature feature, int label, boolean updatePlus){
		int feat = feature.getFeat();
	    if(feat >= nFeatures || feat < 0){
			//double[] tmp = new double[2*weights.length];
			//System.arraycopy(weights, 0, tmp, 0, weights.length);
			//weights = tmp;
	    	throw new IllegalArgumentException("feature "+feat+" does not exist!");
		}
	    if(label < 0 || label >= nLabels){
	    	throw new IllegalArgumentException("Label "+ label + "does not exist!");
	    }
	    int id = getId(label,feat);
	    
	    if(updatePlus){
	    	weights[id] += feature.getValue();	
	    }
	    else{
	    	weights[id] -= feature.getValue();
	    }
		 
	}
	
	/*
	private void update(Feature feature){
		update(feature,true);
	}*/
	
	
	
	public void update(Iterable<Feature> feats, int label, boolean updatePlus){
	    for(Feature feat:feats){
	    	update(feat,label, updatePlus);
	    }	
	}
	
	public void update(Iterable<Feature> feats, int label){
	    update(feats,label,true);	
	}
	
	
	public double score(List<Feature> feats,int label){
		double sc = 0.0;
		for(Feature f:feats){
			double w = f.getValue();
			int fid = f.getFeat();
			sc += w*weights[getId(label,fid)];
		}
		return sc;
	}
	
	
	
}
