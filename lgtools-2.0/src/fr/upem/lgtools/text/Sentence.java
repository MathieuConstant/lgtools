/**
 * 
 */
package fr.upem.lgtools.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author mconstant
 *
 */
public class Sentence {
	private final List<Unit> units = new ArrayList<Unit>(); 
	private final int size; //token count 
	private final HashMap<Integer,Unit> map = new HashMap<Integer, Unit>();
	
	public Sentence(List<Unit> units){
		this.units.addAll(units);
		for(Unit u:units){
			map.put(u.getId(), u);
		}
		this.size = this.units.size();		
	}
	
	
	
	/**
	 * Constructor which deeply copy input sentence 
	 * 
	 * @param s input sentence
	 */
	
	public Sentence(Sentence s){
		for(Unit u:s.getUnits()){
			add(new Unit(u));
		}
		this.size = s.size;
	}
	
	
	public Unit get(int id){
		return map.get(id);
	}
	
	public boolean add(Unit u){
		map.put(u.getId(), u);
		return units.add(u);
		
	}
	
	public List<Unit> getTokens(){
		return units.subList(0, size);		
	}
	
	public List<Unit> getUnits(){
		return units;		
	}
	
	public List<Unit> getMWUnits(){
		return units.subList(size,units.size());
	}
	
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Unit u:units){
			sb.append(u);	
		}		
		return sb.toString();
	}

}
