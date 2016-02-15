/**
 * 
 */
package fr.upem.lgtools.evaluation;

/**
 * @author Mathieu Constant
 *
 */
public class Score {
	private int  gold = 0;
	private int predicted = 0;
	private int good = 0;
	private final String evalName;
	
	public Score(String name){
		this.evalName = name;
	}
	
	
	public void addGold(){
		gold++;
	}
	
	public void addPredicted(){
		predicted++;
	}
	
	public void addGood(){
		good++;
	}
	
	
	public double getPrecision(){
		return good/(double)predicted;
	}
	
	
	public double getRecall(){
		return good/(double)gold;
	}
	
	public double getFscore(){
		double r = getRecall();
		double p = getPrecision();
		return 2*r*p/(r+p);
		
	}
	
	@Override
	public String toString() {
		return evalName + ": p="+getPrecision()+ ", r="+getRecall()+", f="+getFscore();
	}
	

}
