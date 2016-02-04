package fr.upem.lgtools.text;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {
	
	
	private static String feats(Map<String,String> feats){
		StringBuilder sb = new StringBuilder();
		if(feats.size() == 0){
			return "_";
		}
		for(String att:feats.keySet()){
			sb.append("|").append(att).append("=").append(feats.get(att));			
		}
		sb.deleteCharAt(0);
		return sb.toString();
		
	}
	
	
	private static void writeUnit(BufferedWriter out, Unit u) throws IOException{
		out.write(u.getId()+"\t"+u.getForm()+"\t"+u.getLemma()+"\t"+u.getCpos());
		out.write("\t"+u.getPos()+"\t"+feats(u.getFeatures()));
		out.write("\t"+u.getSheadId()+"\t"+u.getSlabel());
		out.write("\t"+u.getGoldSheadId()+"\t"+u.getGoldSlabel());
		out.write("\n");
		
	}
	
	private static void writeUnitInXConll(BufferedWriter out, Unit u) throws IOException{
		out.write(u.getId()+"\t"+Arrays.toString(u.getPositions())+"\t"+u.getForm()+"\t"+u.getLemma()+"\t"+u.getCpos());
		out.write("\t"+u.getPos()+"\t"+feats(u.getFeatures()));
		out.write("\t"+u.getSheadId()+"\t"+u.getSlabel());
		out.write("\t"+u.getGoldSheadId()+"\t"+u.getGoldSlabel());
		out.write("\t"+u.getLheadId()+"\t"+u.getGoldLHead());
		out.write("\n");
		
	}
	
	
	public static void saveTreebankInConll(DepTreebank tb,String filename) throws IOException{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		for(Sentence s:tb){
			for(Unit u:s.getTokens()){
				writeUnit(out,u);
			}
			out.write("\n");
		}
		out.close();			
	}
	
	public static void saveTreebankInXConll(DepTreebank tb,String filename) throws IOException{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		for(Sentence s:tb){
			for(Unit u:s.getUnits()){
				writeUnitInXConll(out,u);
			}
			out.write("\n");
		}
		out.close();			
	}
	
	
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

	
	
	//for now, it only deals with MWE component positions and not POS of the MWE 
	
		/**
		 * 
		 * 
		 * 
		 * @param mwePositions
		 * @param s
		 * @return the existing mwe unit, retun null if not found
		 */
		
		public static Unit findExistingMweUnitByPosition(int[] mwePositions, List<Unit> units){
			for(Unit u:units){
				if(Arrays.equals(u.getPositions(), mwePositions)){
					return u;
				}
			}
			
			return null;
		}
	
	public static Unit mergeUnits(Unit u1, Unit u2,List<Unit> units){
		String form = u1.getForm()+"_"+u2.getForm();
		int [] pos1 = u1.getPositions();
		int [] pos2 = u2.getPositions();
		int[] positions = new int[pos1.length+pos2.length];
		
		// fill positions
		for(int i = 0 ; i < pos1.length ; i++){
			positions[i] = pos1[i];
		}
		for(int i = 0 ; i < pos2.length ; i++){
			positions[i+pos1.length] = pos2[i];
		}
		
		int id = units.size() + 1;
		//System.err.println(form);
		//System.err.println(Arrays.toString(positions));
		
		Unit mwe = findExistingMweUnitByPosition(positions, units);
		//System.err.println(mwe);
		
		if(mwe == null){
		      mwe = new Unit(id,form, positions);
		}
		return mwe;
	}
	
}
