package fr.upem.lgtools.evaluation;

import java.util.Iterator;

import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

public class ParsingAccuracy {
	private int sentenceCount = 0;
	private int unitCount = 0;
	private int lasCount = 0;
	private int uasCount = 0;
	private int exactMatchCount = 0;
	
	
	private static void computeParsingAccuracy(Sentence gold, Sentence sys, ParsingAccuracy acc){
		Iterator<Unit> it = sys.getTokens().iterator();
		boolean exact = true;
		for(Unit gu:gold.getTokens()){
			Unit su = it.next();
			if(su.getId() != gu.getId()){
				throw new IllegalArgumentException("Compared units are not the same ones: "+su+ " vs. "+gu);
			}
			acc.addUnit();
			if(su.getSheadId() == gu.getSheadId()){
				acc.addUnlabeledMatch();
				if(su.getSlabel().equals(gu.getSlabel())){
					acc.addLabeledMatch();
				}
				else{
					exact = false;
				}
				
			}
			else{
				exact = false;
			}
			
		}
		if(exact){
			acc.addExactMatch();
		}
	}
	
	
    public static ParsingAccuracy computeParsingAccuracy(DepTreebank gold, DepTreebank sys){
		ParsingAccuracy acc = new ParsingAccuracy();
		Iterator<Sentence> it = sys.iterator();
		for(Sentence gs:gold){
			acc.addSentence();
			Sentence ss = it.next();
			computeParsingAccuracy(gs,ss,acc);
		}
		return acc;
	}
	
	
	
	public void addUnit(){
		unitCount++;
	}
	
	public void addLabeledMatch(){
		lasCount++;
	}
	
	public void addUnlabeledMatch(){
		uasCount++;
	}
	
	public void addExactMatch(){
		exactMatchCount++;
	}
	
	public void addSentence(){
		sentenceCount++;
	}
	
	
	public double getLAS(){
		return lasCount/(double)unitCount;		
	}
	
	public double getUAS(){
		return uasCount/(double)unitCount;		
	}
	
	public double getExactMacth(){
		return exactMatchCount/(double)sentenceCount;
	}

		
	@Override
	public String toString(){
		String res = "UAS="+getUAS()+", LAS="+getLAS() + ", ExactMatch="+getExactMacth();
		return res;
	}
	
	
}
