/**
 * 
 */
package fr.upem.lgtools.parser.impl;

import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu Constant
 *
 */
public class Container {
	
	Element head;
	
	public Container(){
		head = null;
	}
	
	public Container(Unit... units){
		for(int i= units.length - 1 ; i >= 0 ; i++){
		   addFirst(units[i]);	
		}
	}
	
	
	private void addFirst(Unit u){
		Element h = new Element(u,head);
		head = h;
	}
	
	public boolean isEmpty(){
		return head == null;
	}
	
	
	//pop version for buffer
	
	public Unit read(){
		return pop();
	}
	
	
	public void push(Unit u){
		addFirst(u);
	}
	
	
	public Unit pop(){
		if(!isEmpty()){
			throw new IllegalStateException("Cannot pop as container is empty!");
		}
		Element h = head;
		head = head.getNext();
		return h.getUnit();
	}
	
	
	

}
