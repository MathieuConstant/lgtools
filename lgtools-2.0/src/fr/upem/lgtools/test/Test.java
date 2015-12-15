package fr.upem.lgtools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.upem.lgtools.parser.ArcStandardSyntacticParserModel;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.StandardGreedyParser;
import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.StreamDepTreebank;

public class Test {

	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("corpus.conll")));
		DepTreebank tb = new BufferedDepTreebank(new StreamDepTreebank(reader));
		ArcStandardSyntacticParserModel model = new ArcStandardSyntacticParserModel(1000, tb);
		
		StandardGreedyParser<DepTree> parser = new StandardGreedyParser<DepTree>(model);
		parser.train(tb, 1);
		/*for(Sentence s:tb){
			System.out.println(s.getTokens());
		   parser.parse(s.getTokens());
		}*/
		
	}

}
