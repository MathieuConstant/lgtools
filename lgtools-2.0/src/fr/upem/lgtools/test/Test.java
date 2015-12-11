package fr.upem.lgtools.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.upem.lgtools.text.BufferedDepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.StreamDepTreebank;

public class Test {

	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//System.out.println("Hello world!");
        //Unit u = new Unit(0, "WWW", 0,8,9);
        //u.setLemma("root");
        //System.out.println(u);
        FileInputStream in = new FileInputStream("tests/test.conll");        
        StreamDepTreebank tb = new StreamDepTreebank(new BufferedReader(new InputStreamReader(in)));        
        BufferedDepTreebank btb = new BufferedDepTreebank(tb);
        in.close();
        
        for(Sentence s:btb){
            
        	
        }
        
        System.err.println("############");
        for(Sentence s:btb){
        	System.err.println("*****");
        	System.err.println(s);
        }
       

        
	}

}
