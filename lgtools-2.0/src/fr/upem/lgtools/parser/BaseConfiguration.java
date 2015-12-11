package fr.upem.lgtools.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.upem.lgtools.text.Unit;

public class BaseConfiguration<T> {
	private final ArrayDeque<Unit> buffer = new ArrayDeque<Unit>();
    private final Stack<Unit> stack = new Stack<Unit>();
    private final T analyses;
    private final List<String> history = new ArrayList<String>();
    
    public BaseConfiguration(List<Unit> units,T analyses){
    	this.analyses = analyses;
    	buffer.addAll(units);
    }
   
    
    public Stack<Unit> getStack(){
    	return stack;
    }


	public ArrayDeque<Unit> getBuffer() {
		return buffer;
	}


	public T getAnalyses() {
		return analyses;
	}


	public List<String> getHistory() {
		return history;
	}
    
    
    
}
