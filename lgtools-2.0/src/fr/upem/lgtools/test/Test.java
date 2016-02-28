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
import fr.upem.lgtools.parser.arcstandard.BaselineFullyMWEAwareArcStandardTransitionBasedModel;
import fr.upem.lgtools.parser.arcstandard.FullyMWEAwareArcStandardTransitionBasedModel;
import fr.upem.lgtools.parser.arcstandard.ImplicitCmpFullyMWEAwareArcStandardTransitionBasedModel;
import fr.upem.lgtools.parser.arcstandard.SimpleLabeledMergeArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.arcstandard.SimpleMergeArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashFeatureMapping;
import fr.upem.lgtools.parser.mwereco.MweRecognizerModel;
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
	
	
	
	
	
	private static TransitionBasedSystem<DepTree> trainMweSystem(String filename,String model, int iter, int limit, boolean withFixedMweOnly) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		if(!withFixedMweOnly){
		    tb = DepTreebankFactory.mergeRegularMWEs(tb, REG_MWE);
		}
	
		Utils.saveTreebankInXConll(tb, "merged.conll");
		tb = DepTreebankFactory.binarizeMWE(tb, false);
		Utils.saveTreebankInXConll(tb, "binarized.conll");
		
		FeatureMapping fm = new  HashFeatureMapping(1000000);
		MweRecognizerModel tbm = new MweRecognizerModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
		return parser;
	}
	
	private static void parseWithMweSystem(String filename,String model,String output, int limit, boolean withFixedMweOnly) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		MweRecognizerModel tbm = new MweRecognizerModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
		//ParsingResult res = parser.oracleParseTreebankAndEvaluate(tb);
		tb = DepTreebankFactory.unbinarizeMWE(res.getTreebank(), false);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		if(!withFixedMweOnly){
		   tb = DepTreebankFactory.mergeRegularMWEs(tb, REG_MWE);
		}
		Utils.saveTreebankInXConll(tb, output);
		//System.err.println("\nFixed MWEs only:\n"+SegmentationAccuracy.computeSegmentationAccuracy(tb,true));
		System.err.println("\nAll MWEs:\n"+SegmentationAccuracy.computeSegmentationAccuracy(tb,false));
			
		
	}
	
	
	private static TransitionBasedSystem<DepTree> trainFullSystem(String filename,String model, int iter, int limit, boolean baseline) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		tb = DepTreebankFactory.mergeRegularMWEs(tb, REG_MWE);
	
		Utils.saveTreebankInXConll(tb, "merged.conll");
		tb = DepTreebankFactory.binarizeMWE(tb, false);
		Utils.saveTreebankInXConll(tb, "binarized.conll");
		
		FeatureMapping fm = new  HashFeatureMapping(4000000);
		FullyMWEAwareArcStandardTransitionBasedModel tbm = baseline?new BaselineFullyMWEAwareArcStandardTransitionBasedModel(fm,tb):new ImplicitCmpFullyMWEAwareArcStandardTransitionBasedModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
		return parser;
	}
	
	
	
	private static void parseWithFullSystem(String filename,String model,String output, int limit, boolean baseline) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		FullyMWEAwareArcStandardTransitionBasedModel tbm = baseline?new BaselineFullyMWEAwareArcStandardTransitionBasedModel(model):new ImplicitCmpFullyMWEAwareArcStandardTransitionBasedModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		//System.err.println(tbm.getTransitions());
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
		//ParsingResult res = parser.oracleParseTreebankAndEvaluate(tb);
		tb = DepTreebankFactory.unbinarizeMWE(res.getTreebank(), false);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		tb = DepTreebankFactory.mergeRegularMWEs(tb, REG_MWE);
		Utils.saveTreebankInXConll(tb, output);
		System.err.println("\nFixed MWEs only:\n"+SegmentationAccuracy.computeSegmentationAccuracy(tb,true));
		System.err.println("\nAll MWEs:\n"+SegmentationAccuracy.computeSegmentationAccuracy(tb,false));
		for(Score s:SegmentationAccuracy.computeMergeParsingScore(tb)){
			System.err.println(s);
		}
		
		tb = DepTreebankFactory.unmergeFixedMWE(tb, MWE_LABEL);
		tb = DepTreebankFactory.removeMwePOSInLabels(tb, MWE_LABEL, REG_MWE);
		System.err.println(ParsingAccuracy.computeParsingAccuracy(tb));
			
		
	}
	
	
	
	
	private static void train(String filename,String model, int iter, int limit, boolean withFixedMweOnly) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		if(withFixedMweOnly){
		   tb = DepTreebankFactory.removeRegularMWEs(tb, REG_MWE);
		}
		FeatureMapping fm = new  HashFeatureMapping(2000000);
		ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
	}
	
	
	private static void parse(String filename,String model,String output, int limit, boolean withFixedMweOnly) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		ArcStandardTransitionBasedParserModel tbm = new ArcStandardTransitionBasedParserModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);		
		tb = DepTreebankFactory.mergeFixedMWEs(res.getTreebank(), MWE_LABEL);
		if(!withFixedMweOnly){
		   tb = DepTreebankFactory.mergeRegularMWEs(tb, REG_MWE);
		}
		Utils.saveTreebankInXConll(tb, output);
		tb = DepTreebankFactory.removeMwePOSInLabels(tb, MWE_LABEL, REG_MWE);
		
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb,true));
		if(!withFixedMweOnly){
		    System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb,false));
		}
		for(Score s:SegmentationAccuracy.computeMergeParsingScore(tb)){
			System.err.println(s);
		}
		
		
		DepTreebank tmp = DepTreebankFactory.removeMwePOSInLabels(res.getTreebank(), MWE_LABEL, REG_MWE);
		if(withFixedMweOnly){
		    tmp = DepTreebankFactory.removeRegularMWEs(tmp, REG_MWE);
		}
		System.err.println(ParsingAccuracy.computeParsingAccuracy(tmp));
		//Utils.saveTreebankInXConll(tmp, output);
		
			
		
	}
	
	
	private static void trainWithMerge(String filename,String model, int iter, int limit) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		tb = DepTreebankFactory.removeRegularMWEs(tb, REG_MWE);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		Utils.saveTreebankInXConll(tb, "merged.conll");
		tb = DepTreebankFactory.binarizeMWE(tb, false);
		Utils.saveTreebankInXConll(tb, "binarized.conll");
		FeatureMapping fm = new  HashFeatureMapping(2000000);
		SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleLabeledMergeArcStandardTransitionBasedParserModel(fm,tb);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(tb, model,iter);
	}
	private static void parseWithMerge(String filename,String model,String output,int limit) throws IOException{
		DepTreebank tb = readTreebank(filename,limit);
		SimpleMergeArcStandardTransitionBasedParserModel tbm = new SimpleLabeledMergeArcStandardTransitionBasedParserModel(model);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		ParsingResult res = parser.greedyParseTreebankAndEvaluate(tb);
		
		tb = DepTreebankFactory.removeRegularMWEs(res.getTreebank(), REG_MWE);
		tb = DepTreebankFactory.unMergeMWE(tb, MWE_LABEL);
		tb = DepTreebankFactory.removeMwePOSInLabels(tb, MWE_LABEL, REG_MWE);
		
		System.err.println(ParsingAccuracy.computeParsingAccuracy(tb));
		Utils.saveTreebankInXConll(tb, output);
		tb = DepTreebankFactory.mergeFixedMWEs(tb, MWE_LABEL);
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb,true));
		System.err.println(SegmentationAccuracy.computeSegmentationAccuracy(tb,false));
		for(Score s:SegmentationAccuracy.computeMergeParsingScore(tb)){
			System.err.println(s);
		}
	}
	
	
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//trainWithMerge("train.acl14.joint.predmorph.lexcpd.conll", "mergemodel", 6,-1);
		//parseWithMerge("dev.acl14.joint.predmorph.lexcpd.conll", "mergemodel.final","res-merge.conll",-1);
		
		//trainFullSystem("train.acl14.joint.predmorph.lexcpd.conll", "fullmodel-imp", 6,-1,true);
		//parseWithFullSystem("dev.acl14.joint.predmorph.lexcpd.conll", "fullmodel.final", "res-full.conll",-1,false);
		
		trainMweSystem("train.acl14.joint.predmorph.lexcpd.conll", "mwemodel", 6,-1,false);
		parseWithMweSystem("dev.acl14.joint.predmorph.lexcpd.conll", "mwemodel.final", "res-mwe.conll",-1,false);
		
		
		//train("train.acl14.joint.predmorph.lexcpd.conll", "stdmodel-irreg", 6,-1,true);
		 //parse("dev.acl14.joint.predmorph.lexcpd.conll", "stdmodel-irreg.final", "res-std.conll",-1,true);
		 
		
	}

}
