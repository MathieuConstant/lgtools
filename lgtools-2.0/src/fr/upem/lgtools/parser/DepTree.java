package fr.upem.lgtools.parser;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DepTree {
	private final Set<DepArc>[] nodeChildren;
	private final DepArc[] reverse;
	
	@SuppressWarnings("unchecked")
	public DepTree(int size){
		nodeChildren = (Set<DepArc>[])new Set<?>[size];
		reverse = new DepArc[size];
	}
	
	public void addArc(DepArc a){
		int dep = a.getDep();
		if(reverse[dep] != null){
			throw new IllegalStateException("Node "+dep+ " has already a parent");
		}
		if(nodeChildren[a.getHead()] == null){
			nodeChildren[a.getHead()] = new HashSet<DepArc>();
		}
		nodeChildren[a.getHead()].add(a);
		reverse[dep] = a;
	}
	
	public DepArc[] getArcs(){
		return reverse;
	}
	
	
	public int getHead(int node){
		return reverse[node].getHead();
	}

	public String getlabel(int node){
		return reverse[node].getLabel();
	}
	
	public Set<Integer> getChildren(final int node){
		return new AbstractSet<Integer>() {

			@Override
			public Iterator<Integer> iterator() {
				
				return new Iterator<Integer>() {
					private final Iterator<DepArc> it = nodeChildren[node].iterator();

					@Override
					public boolean hasNext() {						
						return it.hasNext();
					}

					@Override
					public Integer next() {						
						return it.next().getDep();
					}

					@Override
					public void remove() {
						throw new IllegalStateException("Cannot remove dep arcs");
						
					}
					
					
				};
			}

			@Override
			public int size() {				
				return nodeChildren[node].size();
			}
		};
		
		
	}

	@Override
	public String toString() {		
		return Arrays.toString(reverse);
	}
	
	
}
