package fr.upem.lgtools.evaluation;

import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

public class ParsingAccuracy {
	private int sentenceCount = 0;
	private int unitCount = 0;
	private int lasCount = 0;
	private int uasCount = 0;
	private int exactMatchCount = 0;
	
	
	public static void computeParsingAccuracy(Sentence sentence, ParsingAccuracy acc){
		
		boolean exact = true;
		for(Unit u:sentence.getTokens()){
			//System.err.println(u);
			acc.addUnit();
			if(u.getGoldSheadId() == u.getSheadId()){
				acc.addUnlabeledMatch();
				
				if(u.getGoldSlabel().equals(u.getSlabel())){
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
	
	
    public static ParsingAccuracy computeParsingAccuracy(DepTreebank tb){
		ParsingAccuracy acc = new ParsingAccuracy();
		for(Sentence s:tb){
			acc.addSentence();
			computeParsingAccuracy(s,acc);
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
