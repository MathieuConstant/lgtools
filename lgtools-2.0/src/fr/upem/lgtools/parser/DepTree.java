package fr.upem.lgtools.parser;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DepTree {
	private final Set<DepArc>[] nodeChildren;
	private final DepArc[] reverse;
	private final DepArc[] leftMostDependencies;
	private final DepArc[] rightMostDependencies;
	
	@SuppressWarnings("unchecked")
	public DepTree(int size){
		nodeChildren = (Set<DepArc>[])new Set<?>[size];
		reverse = new DepArc[size];
		leftMostDependencies = new DepArc[size];	
		rightMostDependencies = new DepArc[size];		
	}
	
	public void addArc(DepArc a){
		int dep = a.getDep();
		if(reverse[dep] != null){
			throw new IllegalStateException("Node "+dep+ " has already a parent");
		}
		int h = a.getHead();
		int d = a.getDep();
		if(nodeChildren[h] == null){
			nodeChildren[h] = new HashSet<DepArc>();
		}
		nodeChildren[h].add(a);
		reverse[dep] = a;
		if(h < a.getDep()){  //case of a right dependency
			if(rightMostDependencies[h] == null || rightMostDependencies[h].getDep() < d){
				rightMostDependencies[h] = a;			
			}
		}
		else{ //case of a left dependency
			if(leftMostDependencies[h] == null || leftMostDependencies[h].getDep() > d){
				leftMostDependencies[h] = a;			
			}
			
		}
	}
	
	public DepArc[] getArcs(){
		return reverse;
	}
	
	
	
	
	public DepArc[] getLeftMostDependencies() {
		return leftMostDependencies;
	}

	public DepArc[] getRightMostDependencies() {
		return rightMostDependencies;
	}

	public int getHead(int node){
		return reverse[node].getHead();
	}

	public String getlabel(int node){
		return reverse[node].getLabel();
	}
	
	public Set<Integer> getChildren(final int node){
		if(node < 0 || node >= reverse.length){
			
			return Collections.emptySet();
		}
		return new AbstractSet<Integer>() {

			@Override
			public Iterator<Integer> iterator() {
				
				return new Iterator<Integer>() {
					private final Iterator<DepArc> it = nodeChildren[node] == null?Collections.<DepArc>emptySet().iterator():nodeChildren[node].iterator();

					
					
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
