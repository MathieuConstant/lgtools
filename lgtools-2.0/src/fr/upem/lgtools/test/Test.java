package fr.upem.lgtools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;

import fr.upem.lgtools.evaluation.ParsingAccuracy;
import fr.upem.lgtools.evaluation.ParsingResult;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.PerceptronTransitionBasedSystem;
import fr.upem.lgtools.parser.TransitionBasedSystem;
import fr.upem.lgtools.parser.arcstandard.SimpleMergeArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashMapFeatureMapping;
import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.DepTreebankFactory;
import fr.upem.lgtools.text.StreamDepTreebank;
import fr.upem.lgtools.text.Utils;

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
		 
		
		DepTreebank tb = readTreebank("dev.expandedcpd.conll");
		DepTreebank tb1 = DepTreebankFactory.mergeFixedMWEs(tb, new HashSet<String>(Arrays.asList("dep_cpd")));
		DepTreebank tb2 = DepTreebankFactory.binarizeMWE(tb1, false);
		//DepTreebank dev3 = DepTreebankFactory.unbinarizeMWE(dev2, false);
		
		//Utils.saveTreebankInXConll(tb1, "test.conll");
		
		
		
		
		//DepTreebank tb = readTreebank("dev.expandedcpd.conll",1);
		FeatureMapping fm = new  HashMapFeatureMapping(10000000);
		//SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleMergeArcStandardTransitionBasedParserModel(fm,tb2);
		SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleMergeArcStandardTransitionBasedParserModel("model.final");
		//ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(fm,tb);
		//ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel("stdmodel.final");
		//System.err.println(tbm);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		//parser.inexactSearchTrain(tb,"beam",1,4);
		//parser.staticOracleTrain(tb, "stdmodel",2);
		//parser.greedyParseTreebankAndEvaluate(tb);
		//System.err.println(tbm);
		
		//tb = readTreebank("dev.expandedcpd.conll");
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
		//tb1 = DepTreebankFactory.mergeFixedMWEs(res.getTreebank(), new HashSet<String>(Arrays.asList("dep_cpd")));
		tb1 = DepTreebankFactory.unbinarizeMWE(res.getTreebank(), false);
		Utils.saveTreebankInXConll(tb1, "res1.conll");
		System.err.println(ParsingAccuracy.computeParsingAccuracy(tb1));
		Utils.saveTreebankInXConll(res.getTreebank(), "res.conll");
		
		
		
		
		
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
