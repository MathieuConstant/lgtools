package fr.upem.lgtools.test;

import java.io.IOException;

import fr.upem.lgtools.parser.Parser;
import fr.upem.lgtools.parser.arcstandard.ParserUtils;

public class Test {
   
	
	
	
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Parser.readXConllTreebank("tmp.xconll", -1);
		//MultipleEvaluation me = new MultipleEvaluation();
		//Parser.trainWithMerge("data/clean/fa-ud-train.conllu", "tmp", 15,-1);
		//Parser.parseWithMerge("data/clean/fa-ud-dev.conllu", "tmp.final","tmp.conll",-1);
		
		//trainFullSystem("tune-train.acl14.joint.predmorph.lexcpd.conll", "fullmodel", 6,-1,true,N_EXP);
		
		//parseWithFullSystem("dev.acl14.joint.predmorph.lexcpd.conll", "fullmodel.final", "res-full.conll",-1,false);
		
		  //trainMweSystem("tune-train.acl14.joint.predmorph.lexcpd.conll", "mwemodel", 1,-1,false);
		  //Evaluation e = parseWithMweSystem("tune-test.acl14.joint.predmorph.lexcpd.conll", "mwemodel.final", "res-mwe.conll",-1,false);
		  //me.add(e);
		
		//System.err.println("------------------");
		//System.err.println(me);
		
		//Parser.train("data/clean/fa-ud-train.conllu", "tmp-std", 5,-1,true);
		
		//train("tune-train.acl14.joint.predmorph.lexcpd.conll", "basemodel-3", 6,-1,false);
		//Parser.parse("data/clean/fa-ud-dev.conllu", "tmp-std.final", "tmp.conll",-1,true);
		 
		
	}

}
