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
	
	 private final int id;
     private final String form;
     private final int[] positions;
     private String lemma;
     private String cpos;
     private String pos;
     private final HashMap<String,String> feats = new HashMap<String, String>();
     
     
     private int shead;  //syntactic head
     private String slabel;
   //Some day: unit may have several syntactic parents
     
     
     private int lhead;  // lexical head     
     
     private final List<Integer> subunits = new ArrayList<Integer>();  // lexical head     

     
     
     
     
	public Unit(int id, String form, int... positions) {
		this.id = id;
		this.form = form;
		this.positions = positions;
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

	public void setShead(int shead) {
		this.shead = shead;
	}

	public String getSlabel() {
		return slabel;
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
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
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
		
		/*sb.append(id).append(":").append(form).append(":").append(shead)
		   .append(":").append(slabel);*/
		return sb.toString(); 
	}
     
     
}
