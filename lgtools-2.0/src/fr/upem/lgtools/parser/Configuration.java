package fr.upem.lgtools.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;
import fr.upem.lgtools.text.UnitFactory;

public class Configuration<T extends Analysis> {
	private final static String NONE = "";
	private final Sentence sentence;
	private final Buffer[] buffers;
    private final Deque<Unit>[] stacks;
    private final T analyses;
    private final List<String> history = new ArrayList<String>();
    
    
    //units is a list of non-root units
    @SuppressWarnings("unchecked")
	public Configuration(Sentence s,T analyses, int nBuffers, int nStacks){
    	/*if(units == null){
    		throw new IllegalArgumentException("List of units cannot be null");
    	}
    	if(units.isEmpty()){
    		throw new IllegalArgumentException("List of units cannot be empty");
    	}*/
    	if(nBuffers < 1){
    		throw new IllegalArgumentException("There should be at least one buffer (instead of "+nBuffers + ")");
    	}
    	if(nStacks < 1){
    		throw new IllegalArgumentException("There should be at least one stack (instead of "+nStacks + ")");
    	}
    	
    	Unit root = UnitFactory.createRootUnit();
    	buffers = (SimpleBuffer[])new SimpleBuffer[nBuffers];
    	for(int i = 0 ; i < nBuffers ; i++){
    		buffers[i] = new SimpleBuffer(s);    		
    	}
    	stacks =  (Deque<Unit>[])new Deque<?>[nStacks];
    	for(int i = 0 ; i < nStacks ; i++){
    		  stacks[i] = new ArrayDeque<Unit>();
    		  stacks[i].push(root);
    		  
    	}
    	this.sentence = s;
    	
    	this.analyses = analyses;
    }
    
   @SuppressWarnings("unchecked")
   public Configuration(Configuration<T> configuration){

	   buffers = (SimpleBuffer[])new SimpleBuffer[configuration.buffers.length];
	   for(int i = 0 ; i < configuration.buffers.length ; i++){
		   buffers[i] = new SimpleBuffer((SimpleBuffer)configuration.buffers[i]);    		
	   }
	   stacks =  (Deque<Unit>[])new Deque<?>[configuration.stacks.length];
	   for(int i = 0 ; i < configuration.stacks.length ; i++){
		   stacks[i] = new ArrayDeque<Unit>(configuration.stacks[i]);   		  

	   }
	   this.sentence = configuration.sentence;
	   
	   this.analyses = (T)configuration.analyses.copy();

   }

    
   public Sentence getSentence(){
	   return sentence;
   }
   
   public void addUnit(Unit u){
	   sentence.add(u);
   }
   
   
    public Unit getUnit(int id){
    	return sentence.get(id);
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

	
	public void addAction(String action){
		this.history.add(action);
	}
	
	public String getPreviousTransition(){
		if(history.size() < 1){
			return NONE;
		}
		return history.get(history.size() - 1);
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
	
	@Override
	public String toString() {
		String res = getFirstStack().toString()+""+getFirstBuffer().toString()+"\n";
		if(stackCount() > 1){
		    res += getSecondStack()+"\n";
		}
		return res;
	}
	
	public List<Unit> getUnits(){
		return sentence.getUnits();
	}
	
}
