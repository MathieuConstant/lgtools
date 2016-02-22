package fr.upem.lgtools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.upem.lgtools.evaluation.ParsingAccuracy;
import fr.upem.lgtools.evaluation.ParsingResult;
import fr.upem.lgtools.evaluation.Score;
import fr.upem.lgtools.evaluation.SegmentationAccuracy;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.PerceptronTransitionBasedSystem;
import fr.upem.lgtools.parser.TransitionBasedSystem;
import fr.upem.lgtools.parser.arcstandard.ArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.arcstandard.FullyMWEAwareArcStandardTransitionBasedModel;
import fr.upem.lgtools.parser.arcstandard.SimpleLabeledMergeArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.arcstandard.SimpleMergeArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashFeatureMapping;
import fr.upem.lgtools.parser.features.HashMapFeatureMapping;
import fr.upem.lgtools.parser.model.TransitionBasedModel;
import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.DepTreebankFactory;
import fr.upem.lgtools.text.StreamDepTreebank;
import fr.upem.lgtools.text.Utils;

public class Test {
    private final static String MWE_LABEL = "dep_cpd";
    private final static String REG_MWE = "rcpd";
	
	
	
	
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
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		Utils.saveTreebankInXConll(tb, "merged.conll");
		tb = DepTreebankFactory.binarizeMWE(tb, false);
		Utils.saveTreebankInXConll(tb, "binarized.conll");
		FeatureMapping fm = new  HashFeatureMapping(2000000);
		SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleLabeledMergeArcStandardTransitionBasedParserModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
	}
	
	
	private static TransitionBasedSystem<DepTree> trainFullSystem(DepTreebank tb,String model, int iter) throws IOException{
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		tb = DepTreebankFactory.mergeRegularMWEs(tb, REG_MWE);
		Utils.saveTreebankInXConll(tb, "merged.conll");
		tb = DepTreebankFactory.binarizeMWE(tb, false);
		Utils.saveTreebankInXConll(tb, "binarized.conll");
		FeatureMapping fm = new  HashMapFeatureMapping(4000000);
		FullyMWEAwareArcStandardTransitionBasedModel tbm = new FullyMWEAwareArcStandardTransitionBasedModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
		return parser;
	}
	
	
	private static void train(DepTreebank tb,String model, int iter) throws IOException{
		tb = DepTreebankFactory.removeRegularMWEs(tb, REG_MWE);
		FeatureMapping fm = new  HashFeatureMapping(2000000);
		ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
	}
	
	
	private static void parse(DepTreebank tb,String model,String output) throws IOException{
		
		ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);		
		DepTreebank tmp = DepTreebankFactory.unlabelMWEArcs(res.getTreebank(), MWE_LABEL);
		tmp = DepTreebankFactory.removeRegularMWEs(tmp, REG_MWE);
		System.err.println(ParsingAccuracy.computeParsingAccuracy(tmp));
		Utils.saveTreebankInXConll(tmp, output);
		tb = DepTreebankFactory.mergeFixedMWEs(res.getTreebank(), MWE_LABEL);		
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb));
		for(Score s:SegmentationAccuracy.computeMergeParsingScore(tb)){
			System.err.println(s);
		}
	}
	
	private static void parseWithMerge(DepTreebank tb,String model,String output) throws IOException{
		SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleLabeledMergeArcStandardTransitionBasedParserModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
	
		tb = DepTreebankFactory.unMergeMWE(res.getTreebank(), MWE_LABEL);
		System.err.println(ParsingAccuracy.computeParsingAccuracy(tb));
		Utils.saveTreebankInXConll(tb, output);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb));
		for(Score s:SegmentationAccuracy.computeMergeParsingScore(tb)){
			System.err.println(s);
		}
	}
	
	private static void parseWithFullSystem(DepTreebank tb,String model,String output) throws IOException{
		FullyMWEAwareArcStandardTransitionBasedModel tbm = new FullyMWEAwareArcStandardTransitionBasedModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		//System.err.println(tbm.getTransitions());
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
		//ParsingResult res = parser.oracleParseTreebankAndEvaluate(tb);
		tb = DepTreebankFactory.unMergeMWE(res.getTreebank(), MWE_LABEL);
		
		Utils.saveTreebankInXConll(tb, output);
		//System.err.println(ParsingAccuracy.computeParsingAccuracy(tb));
		
		//tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		//tb = DepTreebankFactory.mergeRegularMWEs(tb, REG_MWE);
		//tb = res.getTreebank();
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb));
		for(Score s:SegmentationAccuracy.computeMergeParsingScore(tb)){
			System.err.println(s);
		}
	}
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		 /*DepTreebank tb = readTreebank("train.labeled.acl14.conll");
		 trainWithMerge(tb, "lmodel",6);
		 tb = readTreebank("dev.acl14.conll");
		 parseWithMerge(tb, "lmodel.final", "res-merge.conll");
		*/
		
		DepTreebank tb = readTreebank("train.acl14.joint.predmorph.lexcpd.conll");
		trainFullSystem(tb, "fullmodel", 1);
		tb = readTreebank("dev.acl14.joint.predmorph.lexcpd.conll");
		parseWithFullSystem(tb, "fullmodel.final", "res-full.conll");
		
		
		/*DepTreebank tb = readTreebank("train.acl14.joint.predmorph.lexcpd.conll");		
		train(tb, "stdmodel", 6);
		 tb = readTreebank("dev.acl14.joint.predmorph.lexcpd.conll");
		 
		 
		 parse(tb, "stdmodel.final", "res-std.conll");
		 */
		
	}

}
