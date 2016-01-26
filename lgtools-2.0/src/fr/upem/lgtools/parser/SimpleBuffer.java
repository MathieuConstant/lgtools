/**
 * 
 */
package fr.upem.lgtools.parser;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu
 *
 */
public class SimpleBuffer implements Buffer {
	private final Unit[] units;
	private int current = 0;
	
	public SimpleBuffer(List<Unit> units){
		this.units = units.toArray(new Unit[units.size()]);
	}
	
	

	@Override
	public Unit get(int index) {
		//if((size() <= 0) || (index+current >= size())){
		//	throw new IllegalStateException();
		//}
	    
		return units[current+index];
	}

	@Override
	public Unit next() {
		if(size() == 0){
			throw new IllegalStateException("Cannot get the next unit as the buffer is empty");
		}
		
		Unit u = get(0);
		current++;
		return u;
	}

	@Override
	public int size() {
		return units.length - current;
	}



	@Override
	public Iterator<Unit> iterator() {
		return Arrays.asList(units).subList(current, units.length).iterator();
	
	}

	@Override
	public String toString() {
		return Arrays.asList(units).subList(current, units.length).toString();
	}
	
}
