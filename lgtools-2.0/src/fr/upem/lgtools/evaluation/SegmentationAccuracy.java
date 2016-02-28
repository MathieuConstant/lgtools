/**
 * 
 */
package fr.upem.lgtools.evaluation;

import java.util.Arrays;
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
	private final String eval;
	private int predictedMWECount = 0;
	private int goldMWECount = 0;
	private int sentenceCount = 0;
	private int goldUnitCount = 0;
	private int predictedUnitCount = 0;
	private int goodUnitCount = 0;
	private int goodMWECount = 0;
	private int exactSegmentationCount = 0;
	
	
	
	
	
	/**
	 * @param eval
	 */
	public SegmentationAccuracy(String eval) {
		
		this.eval = eval;
	}

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
			if(u.getGoldSheadId() == u.getPredictedSheadId()){
				uas.addGood();
				if(u.getGoldSlabel().equals(u.getPredictedSlabel())){
					las.addGood();
				}
			}
		}
		for(Unit u:sentence.getTokenSequence(false)){
			uas.addPredicted();
			las.addPredicted();
		}
	}
	
	
	private static void computeSegmentationAccuracy(Sentence s,SegmentationAccuracy acc, boolean onlyFixedMwe){
		List<Unit> gold= onlyFixedMwe?s.getTokenSequence(true):s.getUnitSequence(true);
		List<Unit> predicted = onlyFixedMwe?s.getTokenSequence(false):s.getUnitSequence(false);
		//System.err.println(gold);
		Set<Unit> goldseg = new HashSet<Unit>(gold);
		Set<Unit> goldmwes = new HashSet<Unit>();
		for(Unit u:gold){
			if(u.isMWE()){
				goldmwes.add(u);
				acc.addGoldMWE();
			}
			acc.addGoldUnit();
		}
		Set<Unit> predseg = new HashSet<Unit>(predicted);
		Set<Unit> predmwes = new HashSet<Unit>();
		for(Unit u:predicted){
			if(u.isMWE()){
				predmwes.add(u);
				acc.addPredictedMWE();
			}
			acc.addPredictedUnit();
		}
		Set<Unit> goodseg = new HashSet<Unit>(predicted);
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
			
	}
	
	
	public static SegmentationAccuracy computeSegmentationAccuracy(DepTreebank tb, boolean onlyFixedMwe){
		SegmentationAccuracy acc = new SegmentationAccuracy(onlyFixedMwe?"FixedMWEs":"All MWEs");
		for(Sentence s:tb){
			acc.addSentence();
			computeSegmentationAccuracy(s,acc,onlyFixedMwe);
		}
		return acc;
		
	}

	@Override
	public String toString() {
		String res = eval+":\nSegmentation: r="+getRecall()+", p="+getPrecision()+", f="+getFscore()+", exact="+getExact()+"\n";
		res += "MWEs: r="+getMWERecall()+", p="+getMWEPrecision()+", f="+getMWEFscore()+"\n";
		
		return res;
	}
	
}
