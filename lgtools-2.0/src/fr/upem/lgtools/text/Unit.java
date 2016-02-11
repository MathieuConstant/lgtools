/**
 * 
 */
package fr.upem.lgtools.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mathieu Constant
 *
 * Represents a text unit: may be a token, a multiword expression, or a "subtoken" (e.g. the token 'du' may have two subtokens in French 'de' and 'le')
 *
 */


public class Unit {	
	private final static String DUMMY="_";
	
	 private final int id;
     private final String form;
     private final int[] positions;
     private String lemma;
     private String cpos;
     private String pos;
     private final HashMap<String,String> feats = new HashMap<String, String>();
     private Boolean predictedSeg;
     private Boolean goldSeg;
     
     
     private int shead;  //syntactic head
     private String slabel;
   //Some day: unit may have several syntactic parents
     
     
     private int goldShead;  //gold syntactic head
     private String goldSlabel;  //gold label
     
     private int lhead;  // lexical head  
     private int goldLHead;  // lexical head 
     
     
     private final List<Integer> subunits = new ArrayList<Integer>();  //list of subunits??  one-token compounds?   

     
     
     /**
      * Constructor which deeply copy input unit
      * 
      * @param u input unit
      */
     
     public Unit(Unit u){
    	 this.id = u.id;
    	 this.form = u.form;
    	 this.positions = Arrays.copyOf(u.positions,u.positions.length);
    	 this.lemma = u.lemma;
    	 this.cpos = u.cpos;
    	 this.pos = u.pos;
    	 this.feats.putAll(u.feats);
    	 this.shead = u.shead;
    	 this.slabel = u.slabel;
    	 this.goldShead = u.goldShead;
    	 this.goldSlabel = u.goldSlabel;
    	 this.lhead = u.lhead;
    	 this.goldLHead = u.goldLHead;
    	 this.subunits.addAll(u.subunits);
    	 
    	 
    	 
     }
     
     
	

	public Unit(int id, String form, int... positions) {
		this.id = id;
		this.form = form;
		this.positions = positions;
	}

	
	public boolean isMWE(){
		return positions.length > 1;
	}
	
	
	
	
	
	public Unit findPredictedLexicalRoot(Sentence s){
		Unit root = this;
		//System.err.println(root);
		int lh;
		while((lh = root.getLheadId()) > 0){
			root = s.get(lh);
		}
		return root;
	}
	
	
	public Unit findGoldLexicalRoot(Sentence s){
		Unit root = this;
		//System.err.println(root);
		int lh;
		while((lh = root.getGoldLHead()) > 0){
			root = s.get(lh);
		}
		return root;
	}
	
	
	public int getUnitFirstTokenPosition(){
		return positions[0];
	}
	
	
	
	public boolean isPredictedMWE(Sentence s){
		if(!isMWE()){
			return false;
		}
		if(predictedSeg != null){
			return predictedSeg;
		}
		for(int c:positions){
			Unit tok = s.get(c);
			Unit lroot = tok.findPredictedLexicalRoot(s);
			if(lroot != this){
				predictedSeg = false;
				return predictedSeg;
			}
		}
		predictedSeg = true;
		return predictedSeg;
	}
	
	public boolean isGoldMWE(Sentence s){
		if(!isMWE()){
			return false;
		}
		if(goldSeg != null){
			return goldSeg;
		}
		for(int c:positions){
			Unit tok = s.get(c);
			Unit lroot = tok.findGoldLexicalRoot(s);
			if(lroot != this){
				goldSeg = false;
				return goldSeg;
			}
		}
		goldSeg = true;
		return goldSeg;
	}
	
	
	
	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getCpos() {
		return cpos;
	}

	public void setCpos(String cpos) {
		this.cpos = cpos;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public int getSheadId() {
		return shead;
	}

	
	
	
	public int getGoldSheadId() {
		return goldShead;
	}

	public void setGoldShead(int goldShead) {
		this.goldShead = goldShead;
	}
	
	

	public String getGoldSlabel() {
		return goldSlabel == null?DUMMY:goldSlabel;
	}

	public void setGoldSlabel(String goldSlabel) {
		this.goldSlabel = goldSlabel;
	}

	
	public void setShead(int shead) {
		this.shead = shead;
	}

	public String getSlabel() {
		return slabel==null?DUMMY:slabel;
	}
	

	public void setSlabel(String slabel) {
		this.slabel = slabel;
	}

	
	public int getLheadId() {
		return lhead;
	}

	
	public void setLhead(int lhead) {
		this.lhead = lhead;
	}

	
	
	
	public int getGoldLHead() {
		return goldLHead;
	}




	public void setGoldLHead(int goldLHead) {
		this.goldLHead = goldLHead;
	}




	public int getId() {
		return id;
	}

	public String getForm() {
		return form;
	}

	public int[] getPositions() {
		return positions;
	}

	
	
	public void addFeature(String att,String val){
		feats.put(att, val);
	}
	
	public String getFeature(String att){
		return feats.get(att);
	}

	public Map<String,String> getFeatures(){
		return feats;
	}
	
	
	public List<Integer> getSubunits() {
		return subunits;
	}
	
	public boolean isRoot(){
		return id == 0;
	}
	

	
	public boolean isGoldLexicalRoot(){
		return getGoldLHead() <= 0;
	}
	
	public boolean isPredictedLexicalRoot(){
		return getLheadId() <= 0;
	}
	
	
	public boolean isPredictedSeg() {
		return predictedSeg;
	}




	public void setPredictedSeg(boolean predictedSeg) {
		this.predictedSeg = predictedSeg;
	}




	public boolean isGoldSeg() {
		return goldSeg;
	}




	public void setGoldSeg(boolean goldSeg) {
		this.goldSeg = goldSeg;
	}




	@Override
	public String toString(){
		/*StringBuilder sb = new StringBuilder();
		sb.append(id)
		   .append("\t")
		   .append(form)
		   .append("\t")
		   .append(lemma).append(Arrays.toString(positions))
		   .append("\t")
		   .append(cpos)
		   .append("\t")
		   .append(pos)
		   .append("\t")
		   .append(feats)
		   .append("\t")
		   .append(shead).append("\t").append(slabel)
		   .append("\n");	
		*/
		/*sb.append(id).append(":").append(form).append(":").append(shead)
		   .append(":").append(slabel);*/
		//return sb.toString();
		return form+"("+id+","+pos+")";
	}
     
     
}
