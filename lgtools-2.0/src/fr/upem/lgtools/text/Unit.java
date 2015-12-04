/**
 * 
 */
package fr.upem.lgtools.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
     
     
     private Unit shead;  //syntactic head
     private String slabel;
   //Some day: unit may have several syntactic parents
     
     
     private Unit lhead;  // lexical head     
     
     private final List<Unit> subunits = new ArrayList<Unit>();  // lexical head     

     
     
     
     
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

	public Unit getShead() {
		return shead;
	}

	public void setShead(Unit shead) {
		this.shead = shead;
	}

	public String getSlabel() {
		return slabel;
	}

	public void setSlabel(String slabel) {
		this.slabel = slabel;
	}

	public Unit getLhead() {
		return lhead;
	}

	public void setLhead(Unit lhead) {
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

	public HashMap<String, String> getFeats() {
		return feats;
	}

	public List<Unit> getSubunits() {
		return subunits;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(id)
		   .append("\t")
		   .append(form)
		   .append("\t")
		   .append(lemma).append(Arrays.toString(positions))
		   .append("\n");	
		
		return sb.toString(); 
	}
     
     
}
