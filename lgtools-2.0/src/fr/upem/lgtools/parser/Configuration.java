package fr.upem.lgtools.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import fr.upem.lgtools.text.Unit;

public class Configuration {
	private final ArrayDeque<Unit> buffer = new ArrayDeque<Unit>();
    private final Stack<Unit> stack = new Stack<Unit>();
    private final DepTree tree;
    private final List<String> history = new ArrayList<String>();
    
    public Configuration(List<Unit> units){
    	tree = new DepTree(units.size());
    	buffer.addAll(units);
    }
    
    
    
}
