/**
 * 
 */
package fr.upem.lgtools.evaluation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * @author Matthieu Constant
 *
 */
public class SegmentationAccuracy {
	private int predictedMWECount = 0;
	private int goldMWECount = 0;
	private int sentenceCount = 0;
	private int goldUnitCount = 0;
	private int predictedUnitCount = 0;
	private int goodUnitCount = 0;
	private int goodMWECount = 0;
	private int exactSegmentationCount = 0;
	
	
	public double getPrecision(){
		return goodUnitCount/(double)predictedUnitCount;
	}
	
	public double getMWEPrecision(){
		return goodMWECount/(double)predictedMWECount;
	}
	
	
	public double getRecall(){
		return goodUnitCount/(double)goldUnitCount;
	}
	
	public double getMWERecall(){
		return goodMWECount/(double)goldMWECount;
	}
	
	public double getExact(){
		return exactSegmentationCount/(double)sentenceCount;
		
	}
	
	public double getFscore(){
		double r = getRecall();
		double p = getPrecision();
		return 2*r*p/(r+p);
	}
	
	public double getMWEFscore(){
		double r = getMWERecall();
		double p = getMWEPrecision();
		return 2*r*p/(r+p);
	}
	
	
	public void addSentence(){
		sentenceCount++;
	}
	
	public void addGoldUnit(){
		goldUnitCount++;
	}
	 
	public void addPredictedUnit(){
		predictedUnitCount++;
	}
	
	public void addGoodUnit(){
		goodUnitCount++;
	}
	
	public void addGoodMWE(){
		goodMWECount++;
	}
	
	public void addExactSegmentation(){
		exactSegmentationCount++;
	}
	
	public void addPredictedMWE(){
		predictedMWECount++;
	}
	
	public void addGoldMWE(){
		goldMWECount++;
	}
	
	
	
	public static List<Score> computeMergeParsingScore(DepTreebank tb){
		Score uas = new Score("muas");
		Score las = new Score("mlas");
		for(Sentence s:tb){
			computeMergeParsingScore(s,uas,las);
		}
		
		return Arrays.asList(uas,las);
	}
	
	
	public static void computeMergeParsingScore(Sentence sentence, Score uas, Score las){
		for(Unit u:sentence.getTokenSequence(true)){
			uas.addGold();
			las.addGold();
			if(u.getGoldSheadId() == u.getSheadId()){
				uas.addGood();
				if(u.getGoldSlabel().equals(u.getSlabel())){
					las.addGood();
				}
			}
		}
		for(Unit u:sentence.getTokenSequence(false)){
			uas.addPredicted();
			las.addPredicted();
		}
	}
	
	
	private static void computeSegmentationAccuracy(Sentence s,SegmentationAccuracy acc){
		Set<Unit> goldseg = new HashSet<Unit>(s.getTokenSequence(true));
		Set<Unit> goldmwes = new HashSet<Unit>();
		for(Unit u:s.getTokenSequence(true)){
			if(u.isMWE()){
				goldmwes.add(u);
				acc.addGoldMWE();
			}
			acc.addGoldUnit();
		}
		Set<Unit> predseg = new HashSet<Unit>(s.getTokenSequence(false));
		Set<Unit> predmwes = new HashSet<Unit>();
		for(Unit u:s.getTokenSequence(false)){
			if(u.isMWE()){
				predmwes.add(u);
				acc.addPredictedMWE();
			}
			acc.addPredictedUnit();
		}
		Set<Unit> goodseg = new HashSet<Unit>(s.getTokenSequence(false));
		Set<Unit> goodmwes = new HashSet<Unit>();
		for(Unit u:predseg){
			if(goldseg.contains(u)){
				goodseg.add(u);
				acc.addGoodUnit();				
			}
			if(u.isMWE() && goldmwes.contains(u)){
				goodmwes.add(u);
				acc.addGoodMWE();
			}
		}
		
		if(goldseg.size() == predseg.size() && predseg.size() == goodseg.size()){
			acc.addExactSegmentation();
		}
		acc.addSentence();			
	}
	
	
	public static SegmentationAccuracy computeSegmentationAccuracy(DepTreebank tb){
		SegmentationAccuracy acc = new SegmentationAccuracy();
		for(Sentence s:tb){
			acc.addSentence();
			computeSegmentationAccuracy(s,acc);
		}
		return acc;
		
	}

	@Override
	public String toString() {
		String res = "Segmentation: r="+getRecall()+", p="+getPrecision()+", f="+getFscore()+", exact="+getExact()+"\n";
		res += "MWEs: r="+getMWERecall()+", p="+getMWEPrecision()+", f="+getMWEFscore()+"\n";
		
		return res;
	}
	
}
