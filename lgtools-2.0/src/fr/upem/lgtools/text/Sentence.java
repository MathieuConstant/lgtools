/**
 * 
 */
package fr.upem.lgtools.text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mconstant
 *
 */
public class Sentence {
	private final List<Unit> units = new ArrayList<Unit>(); 
	private final int size; //token count 
	
	public Sentence(List<Unit> units){
		this.units.addAll(units);
		this.size = this.units.size();		
	}
	
	public boolean add(Unit u){
		return units.add(u);
		
	}
	
	public List<Unit> getTokens(){
		return units.subList(0, size);
		
	}

}
