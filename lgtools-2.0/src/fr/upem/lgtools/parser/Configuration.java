package fr.upem.lgtools.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Stack;

import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.UnitFactory;

public class Configuration<T> {
	private final Buffer[] buffers;
    private final Deque<Unit>[] stacks;
    private final T analyses;
    private final List<String> history = new ArrayList<String>();
    
    
    //units is a list of non-root units
    @SuppressWarnings("unchecked")
	public Configuration(List<Unit> units,T analyses, int nBuffers, int nStacks){
    	if(units == null){
    		throw new IllegalArgumentException("List of units cannot be null");
    	}
    	if(units.isEmpty()){
    		throw new IllegalArgumentException("List of units cannot be empty");
    	}
    	if(nBuffers < 1){
    		throw new IllegalArgumentException("There should be at least one buffer (instead of "+nBuffers + ")");
    	}
    	if(nStacks < 1){
    		throw new IllegalArgumentException("There should be at least one stack (instead of "+nStacks + ")");
    	}
    	
    	Unit root = UnitFactory.createRootUnit();
    	buffers = (SimpleBuffer[])new SimpleBuffer[nBuffers];
    	for(int i = 0 ; i < nBuffers ; i++){
    		buffers[i] = new SimpleBuffer(units);    		
    	}
    	stacks =  (Deque<Unit>[])new Stack<?>[nStacks];
    	for(int i = 0 ; i < nStacks ; i++){
    		  stacks[i] = new ArrayDeque<Unit>();
    		  stacks[i].push(root);
    		  
    	}
    	this.analyses = analyses;
    	
    }
   
    public Deque<Unit> getStack(int index){
    	return stacks[index];
    }
    
    
    public Deque<Unit> getFirstStack(){
    	return stacks[0];
    }

    public Deque<Unit> getSecondStack(){
    	if(stacks.length < 2){
    		throw new IllegalArgumentException("To get the second stack, there should be at leat two stacks");
    	}
    	return stacks[1];
    }
    

	public Buffer getFirstBuffer() {
		return buffers[0];
	}


	public T getAnalyses() {
		return analyses;
	}


	public List<String> getHistory() {
		return history;
	}
    
	public int stackCount(){
		return stacks.length;
	}
    
	public int bufferCount(){
		return buffers.length;
	}

	public boolean isTerminal(){
		//buffers must be empty
		for(Buffer buffer:buffers){
			if(buffer.size() != 0){
				return false;
			}
		}
		
		for(Deque<Unit> stack:stacks){
			if(stack.size() > 1){
				return false;
			}
		}
		
		return true;
	}
	
}
