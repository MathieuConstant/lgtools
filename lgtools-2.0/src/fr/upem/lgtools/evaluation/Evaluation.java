package fr.upem.lgtools.evaluation;



public abstract class Evaluation {
  public abstract ParsingAccuracy getParsingAccuracy();
  public abstract Score getMweAccuracy();
  public abstract Score getFixedMweAccuracy();
  public abstract Score getSUAS();
  public abstract Score getSLAS();
  
  @Override
	public String toString() {
	   String res = "EVALUATION:\n";
	   Score mwe = getMweAccuracy();
       res += mwe ==null?"":mwe.toString()+"\n";
       mwe = getFixedMweAccuracy();
       res += mwe ==null?"":mwe.toString()+"\n";
       Score sas = getSLAS();
       res += sas ==null?"":sas.toString()+"\n";
       sas = getSUAS();
       res += sas ==null?"":sas.toString()+"\n";
       ParsingAccuracy acc = getParsingAccuracy();
       res += acc==null?"":acc.toString()+"\n";
		return res;
	}
}
