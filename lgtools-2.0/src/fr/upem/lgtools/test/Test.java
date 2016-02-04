package fr.upem.lgtools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.PerceptronTransitionBasedSystem;
import fr.upem.lgtools.parser.TransitionBasedSystem;
import fr.upem.lgtools.parser.arcstandard.ArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashMapFeatureMapping;
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
		 
		
		DepTreebank tb = readTreebank("train.expandedcpd.conll");
		DepTreebank dev1 = DepTreebankFactory.mergeFixedMWEs(tb, new HashSet<String>(Arrays.asList("dep_cpd")));
		DepTreebank dev2 = DepTreebankFactory.binarizeMWE(dev1, false);
		DepTreebank dev3 = DepTreebankFactory.unbinarizeMWE(dev2, false);
		
		//Utils.saveTreebankInXConll(dev3, "test.conll");
		
		//DepTreebank tb = readTreebank("dev.expandedcpd.conll",1);
		FeatureMapping fm = new  HashMapFeatureMapping(10000000);
		ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(fm,tb);
		//System.err.println(tbm);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		//parser.inexactSearchTrain(tb,"beam",1,4);
		parser.staticOracleTrain(tb, "model",2);
		parser.greedyParseTreebankAndEvaluate(tb);
		//System.err.println(tbm);
		
		//tb = readTreebank("dev.expandedcpd.conll");
		//parser.greedyParseTreebankAndEvaluate(tb);
		//Utils.saveTreebank(tb, "output20-lemma.conll");
		
		
		
		
		
		//ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel("model.final");
		//TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		//parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		//System.err.println(tbm);
		
		
		//parser.greedyParseTreebankAndEvaluate(dev);
		//parser.beamSearchParseTreebankAndEvaluate(dev, 4);
		//Utils.saveTreebank(dev, "output20-lemma2.conll");
		
		
		
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
