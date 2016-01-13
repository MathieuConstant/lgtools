package fr.upem.lgtools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import fr.upem.lgtools.evaluation.ParsingAccuracy;
import fr.upem.lgtools.parser.ArcStandardSyntacticParserModel;
import fr.upem.lgtools.parser.DepArc;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.Model;
import fr.upem.lgtools.parser.StandardGreedyParser;
import fr.upem.lgtools.parser.TransitionBasedModel;
import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.DepTreebankFactory;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.StreamDepTreebank;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.Utils;

public class Test {

	
	public static TransitionBasedModel<DepTree> trainingTest(String dataset, String model, int iterations, int features, int sizeLimit) throws IOException{
		DepTreebank tb = readTreebank(dataset,sizeLimit);
		ArcStandardSyntacticParserModel m = new ArcStandardSyntacticParserModel(features, tb);
		
		StandardGreedyParser<DepTree> parser = new StandardGreedyParser<DepTree>(m);
		parser.train(tb, iterations,model);
		return parser.getModel();
	}
	
	private static void updateSentence(Sentence s,DepTree tree){
		List<Unit> tokens = s.getTokens();
		DepArc[] arcs =  tree.getArcs();
		for(Unit u:tokens){			
		    DepArc a = arcs[u.getId()];
		    u.setShead(a.getHead());
		    u.setSlabel(a.getLabel());
			
		}
	}
	
	public static DepTreebank parsingTest(String dataset,TransitionBasedModel<DepTree> m) throws FileNotFoundException{
		DepTreebank tb = readTreebank(dataset);
		StandardGreedyParser<DepTree> parser = new StandardGreedyParser<DepTree>(m);
		
		int cnt = 0;
		for(Sentence s:tb){
			DepTree tree = parser.parse(s.getTokens());
			updateSentence(s,tree);
			cnt++;
			if(cnt%1000 == 0){
				System.err.println(cnt+" sentences parsed.");
			}
			//System.out.println("");
			
			//System.out.println(s);
		}
		
		return tb;
		
	}
	
	public static DepTreebank parsingTest(String dataset,String model) throws IOException{

		ArcStandardSyntacticParserModel mod = new ArcStandardSyntacticParserModel(model);
		//Model m = mod.getModel();
		//System.err.println("Model trained => nfeatures"+m.getFeatureCount()+",nLabels="+m.getLabelCount());
		//System.err.println(Model.compareModels(m, other));;
		return parsingTest(dataset, mod);
		
	}
	
	
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
		
		TransitionBasedModel<DepTree> model = trainingTest("train.expandedcpd.conll", "test", 5, 1000000,5000);
		//Model m = model.getModel();
		//System.err.println("Model trained => nfeatures"+m.getFeatureCount()+",nLabels="+m.getLabelCount());
		
		
		
		DepTreebank sys = parsingTest("dev.expandedcpd.conll", "test.final");
		Utils.saveTreebank(sys, "sys.conll");
		
		DepTreebank gold = readTreebank("dev.expandedcpd.conll");
		ParsingAccuracy acc = ParsingAccuracy.computeParsingAccuracy(gold, sys);
		System.out.println(acc);
		
		
		
		/*for(Sentence s:tb){
			System.out.println(s.getTokens());
		   parser.parse(s.getTokens());
		}*/
		
		
		
		
	}

}
