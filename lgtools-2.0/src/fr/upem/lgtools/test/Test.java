package fr.upem.lgtools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashFeatureMappingWithMemory;
import fr.upem.lgtools.parser.model.ArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.DepTreebankFactory;
import fr.upem.lgtools.text.StreamDepTreebank;

public class Test {

	
	
	
	
	private static DepTreebank readTreebank(String filename) throws FileNotFoundException{
		return readTreebank(filename,-1);
	}
	
	private static DepTreebank readTreebank(String filename,int size) throws FileNotFoundException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		DepTreebank gold = new BufferedDepTreebank(new StreamDepTreebank(reader));
		if(size >= 0){
			gold = DepTreebankFactory.limitSize(gold, size);
		}
		return gold;
	}
	
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		 
		//TransitionBasedSystem<DepTree> parser = new TransitionBasedSystem<DepTree>(tbm);
		
		DepTreebank tb = readTreebank("train.expandedcpd.conll",2);
		FeatureMapping fm = new HashFeatureMappingWithMemory(10000);
		ArcStandardTransitionBasedParserModel model = new ArcStandardTransitionBasedParserModel(fm,tb);
		System.err.println(model);
		/**
		 * BUG: ajout d'une transition a chaque fois qu'il y a une occurrence possible dans tb: faire une seule fois
		 */
		
		//TransitionBasedModel<DepTree> model = trainingTest("train.expandedcpd.conll", "test10M", 5, 10000000,-1);
		//TransitionBasedModel.countCollisions(model);
		/*ArcStandardSyntacticParserModel model = new ArcStandardSyntacticParserModel("test1.final");*/
		//Model m = model.getModel();
		//System.err.println("Model trained => nfeatures"+m.getFeatureCount()+",nLabels="+m.getLabelCount());
		//System.err.println("Feature set size: "+model.getFeatureSet().size());
		//int emptyCnt = Model.getNonEmptyFeatureIdCount(m);
		//System.err.println("Number of empty feature ids: "+emptyCnt+" =>"+((double)emptyCnt)/m.getFeatureCount());
		//Model.getNonEmptyIdDistribution(m);
		
		/*
		DepTreebank sys = parsingTest("dev.expandedcpd.conll", "test.13");
		Utils.saveTreebank(sys, "sys.conll");
		
		DepTreebank gold = readTreebank("dev.expandedcpd.conll");
		ParsingAccuracy acc = ParsingAccuracy.computeParsingAccuracy(gold, sys);
		System.out.println(acc);
		*/
		
		
		/*for(Sentence s:tb){
			System.out.println(s.getTokens());
		   parser.parse(s.getTokens());
		}*/
		
		
		
		
	}

}
