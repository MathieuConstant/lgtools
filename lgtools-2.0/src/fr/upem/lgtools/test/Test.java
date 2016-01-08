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
import fr.upem.lgtools.parser.StandardGreedyParser;
import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.StreamDepTreebank;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.Utils;

public class Test {

	
	public static void trainingTest(String dataset, String model, int iterations, int features) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(dataset)));
		DepTreebank tb = new BufferedDepTreebank(new StreamDepTreebank(reader));
		ArcStandardSyntacticParserModel m = new ArcStandardSyntacticParserModel(features, tb);
		
		StandardGreedyParser<DepTree> parser = new StandardGreedyParser<DepTree>(m);
		parser.train(tb, iterations,model);
		
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
	
	
	public static DepTreebank parsingTest(String dataset,String model) throws IOException{
				
		DepTreebank tb = readTreebank(dataset);
		ArcStandardSyntacticParserModel m = new ArcStandardSyntacticParserModel(model);
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
	
	
	private static DepTreebank readTreebank(String filename) throws FileNotFoundException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		DepTreebank gold = new BufferedDepTreebank(new StreamDepTreebank(reader));
		return gold;
	}
	
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		//trainingTest("train.expandedcpd.conll", "test", 4, 10000000);
		
		
		
		
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
