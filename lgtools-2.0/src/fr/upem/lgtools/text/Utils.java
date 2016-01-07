package fr.upem.lgtools.text;

public class Utils {
	
	
	private static boolean isCrossing(int i1, int j1, int i2, int j2){
		int tmp = i1;
		i1 = Math.min(i1, j1);
		j1 = Math.max(tmp,j1);
		tmp = i2;
		i2 = Math.min(i2, j2);
		j2 = Math.max(tmp,j2);
		//System.err.println(i1+","+j1+":"+i2+","+j2);
		if(i1 < i2 && i2 < j1 && j1 < j2){
			System.err.println("Non projective sentence (class Utils)");
			return true;
		}
		return false;
	}
	
	public static boolean isProjectiveSentence(Sentence sentence){
		for(Unit u1:sentence.getTokens()){
			for(Unit u2:sentence.getTokens()){
				if(u1 != u2){
					 int i1 = u1.getId();
					 int j1 = u1.getSheadId();
					 int i2 = u2.getId();
					 int j2 = u2.getSheadId();
					 if(isCrossing(i1,j1,i2,j2)){
						 return false;
					 }
					 
				}
				
			}
			
		}
		return true;
	}

}
