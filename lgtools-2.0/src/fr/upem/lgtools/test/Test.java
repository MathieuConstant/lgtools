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
import fr.upem.lgtools.evaluation.SegmentationAccuracy;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.PerceptronTransitionBasedSystem;
import fr.upem.lgtools.parser.TransitionBasedSystem;
import fr.upem.lgtools.parser.arcstandard.ArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.arcstandard.SimpleMergeArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashMapFeatureMapping;
import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.DepTreebankFactory;
import fr.upem.lgtools.text.StreamDepTreebank;
import fr.upem.lgtools.text.Utils;

public class Test {
    private final static String MWE_LABEL = "dep_cpd";
	
	
	
	
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
	
	
	private static void trainWithMerge(DepTreebank tb,String model, int iter) throws IOException{
		tb = DepTreebankFactory.mergeFixedMWEs(tb, new HashSet<String>(Arrays.asList(MWE_LABEL)));
		tb = DepTreebankFactory.binarizeMWE(tb, false);
		FeatureMapping fm = new  HashMapFeatureMapping(10000000);
		SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleMergeArcStandardTransitionBasedParserModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
	}
	
	
	private static void train(DepTreebank tb,String model, int iter) throws IOException{		
		FeatureMapping fm = new  HashMapFeatureMapping(10000000);
		ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
	}
	
	
	private static void parse(DepTreebank tb,String model,String output) throws IOException{
		ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
		System.err.println(res.getAcccuracy());
		Utils.saveTreebankInXConll(res.getTreebank(), output);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, new HashSet<String>(Arrays.asList(MWE_LABEL)));
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb));
	}
	
	private static void parseWithMerge(DepTreebank tb,String model,String output) throws IOException{
		SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleMergeArcStandardTransitionBasedParserModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
		tb = DepTreebankFactory.unMergeMWE(res.getTreebank(), MWE_LABEL);
		System.err.println(ParsingAccuracy.computeParsingAccuracy(tb));
		Utils.saveTreebankInXConll(tb, output);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, new HashSet<String>(Arrays.asList(MWE_LABEL)));
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb));
	}
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		 //DepTreebank tb = readTreebank("train.expandedcpd.conll");
		 //trainWithMerge(tb, "model", 6);
		 DepTreebank tb = readTreebank("dev.expandedcpd.conll");
		 parseWithMerge(tb, "model.final", "res-merge.conll");
		
		 //DepTreebank tb = readTreebank("train.expandedcpd.conll");
		 //train(tb, "stdmodel", 7);
		 //DepTreebank tb = readTreebank("dev.expandedcpd.conll");
		 //parse(tb, "stdmodel.final", "res-std.conll");
		 
		
	}

}
