package fr.upem.lgtools.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class DepTreebankFactory {
	
	public static DepTreebank filter(final DepTreebank tb, final FilterTreebank filter){
		return new DepTreebank() {
			
			@Override
			public int size() {
				return tb.size();
			}
			
			
			
			@Override
			public Iterator<Sentence> iterator() {
				
				return new Iterator<Sentence>() {
                    Iterator<Sentence> it = tb.iterator();
                    Sentence nextSentence = null;
                    
					@Override
					public boolean hasNext() {
						if(nextSentence == null){
							boolean stop = false;
							while(it.hasNext() && !stop){
								nextSentence = it.next();
								if(filter.accept(nextSentence)){
									stop = true;
								}
								else{
									nextSentence = null;
								}
							}
							
						}
						return nextSentence != null;
					}

					@Override
					public Sentence next() {
						if(!hasNext()){
							throw new NoSuchElementException();
						}
						Sentence s = nextSentence;
						nextSentence = null;
						return s;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
						
					}
				};
			}
		};
		
	}
	
	
	public static DepTreebank limitSize(final DepTreebank tb,final int n){
		return new DepTreebank() {
			
			@Override
			public Iterator<Sentence> iterator() {
				
				return new Iterator<Sentence>() {
					int cnt = 0;
					Iterator<Sentence> it = tb.iterator();

					@Override
					public boolean hasNext() {
						
						return it.hasNext() && cnt < n;
					}

					@Override
					public Sentence next() {
						if(!hasNext()){
							throw new NoSuchElementException();
						}
						cnt++;
						return it.next();
					}

					@Override
					public void remove() {
						throw new IllegalStateException();
					}
				};
			}
			
			@Override
			public int size() {
				return tb.size();
			}
			
		};
		
	}
	
	
	
	
	
	public static DepTreebank filterNonProjective(DepTreebank tb){
		return filter(tb, new FilterTreebank() {
			
			@Override
			public boolean accept(Sentence sentence) {
				return Utils.isProjectiveSentence(sentence);
			}
		});
		
	}

	
	private static Map<Integer,List<Unit>> getMWEs(Sentence s,String mweLabel,boolean goldMWEs, Map<Integer,String> poses){
		Map<Integer,List<Unit>> mwes = new HashMap<Integer, List<Unit>>();
	    for(Unit u:s.getTokens()){
	    	String label = u.getSLabel(goldMWEs);
	    	
	    	if(label.startsWith(mweLabel)){
	    		String pos = "";
	    		if(!mweLabel.equals(label)){
	    		    pos = label.substring(mweLabel.length() +1);
	    		}
	    		int head = u.getGoldSheadId();
	    		if(!goldMWEs){
	    			head = u.getPredictedSheadId();
	    		}
	    		List<Unit> mwe = mwes.get(head);
	    		if(mwe == null){
	    			mwe = new LinkedList<Unit>();
	    			mwes.put(head, mwe);
	    			poses.put(head, pos);
	    		}
	    		mwe.add(u);
	    	}
	    }	
	    return mwes;
	}
	
	
	
	//for now, it only deals with MWE component positions and not POS of the MWE 
	
	/**
	 * 
	 * 
	 * 
	 * @param mwePositions
	 * @param s
	 * @return the existing mwe unit, retun null if not found
	 */
	
	private static Unit mweUnitAlreadyExists(int[] mwePositions, Sentence s){
		List<Unit> units = s.getMWUnits();
		//System.err.println(units);
		for(Unit u:units){
			//System.err.println(u);
			//System.err.println("already "+Arrays.toString(u.getPositions()));
			//System.err.println(Arrays.toString(mwePositions));
			if(Arrays.equals(u.getPositions(), mwePositions)){
				return u;
			}
			
		}
		
		return null;
	}
	
	
	private static void addMWEUnit(Unit head, List<Unit> components,Sentence s,boolean goldMwes,String pos,boolean fixedMwe){
        int[] positions = new int[components.size() + 1];
        StringBuilder mweForm = new StringBuilder(head.getForm());
        positions[0] = head.getId();
        int i = 1;
		for(Unit c: components){
			positions[i] = c.getId();
			mweForm.append("_").append(c.getForm());
			if(goldMwes){
				if(fixedMwe){
			        c.setGoldShead(-1);
			        c.setGoldSlabel(null);
				}
			  
			}
			else{
				if(fixedMwe){
				  c.setShead(-1);
				   c.setPredictedSlabel(null);
				}
			}
			i++;
		}
		Arrays.sort(positions);
		Unit mwe = mweUnitAlreadyExists(positions, s); 
		if(mwe == null){
			mwe = new Unit(s.getUnits().size() + 1,mweForm.toString(),positions);
			
			s.add(mwe);
			//System.err.println("MWE="+mwe);
		}
		if(goldMwes){
			mwe.setGoldPos(pos);
			if(fixedMwe){
			    mwe.setGoldShead(head.getGoldSheadId());
			    mwe.setGoldSlabel(head.getGoldSlabel());
			    head.setGoldShead(-1);
			    head.setGoldSlabel(null);
			}
		}
		else{
		    mwe.setPos(pos);
		    if(fixedMwe){
			   mwe.setShead(head.getPredictedSheadId());
		       mwe.setPredictedSlabel(head.getPredictedSlabel());
		       head.setShead(-1);
			   head.setPredictedSlabel(null);
		    }
		}
		
		
		for(Unit c:components){
			if(goldMwes){
			   c.setGoldLHead(mwe.getId());
			   head.setGoldLHead(mwe.getId());
			}
			else{
				c.setPredictedLhead(mwe.getId());
				head.setPredictedLhead(mwe.getId());
			}	
		}
		//mwe.setLemma(concatenateLemmas(mwe, s));
		
	}
	
	
	private static void addMWEUnits(Map<Integer,List<Unit>> mwes,Sentence s,boolean goldMwes,Map<Integer,String> mwePoses){
		for(int i = 0 ; i < s.getTokens().size() ; i++){
			Unit h = s.getTokens().get(i);
			if(mwes.containsKey(h.getId())){
				addMWEUnit(h,mwes.get(h.getId()),s,goldMwes,mwePoses.get(h.getId()),true);
				//System.err.println(h+"--"+goldMwes);
			}	
		}
	
		//modify the head of units that are governed by the MWEs
		for(Unit u:s.getTokenSequence(goldMwes)){
			Set<Integer> mwe= new HashSet<Integer>();
			for(int p:u.getPositions()){
				mwe.add(p);
			}
			for(Unit v:s.getTokenSequence(goldMwes)){
				if(goldMwes){
				   int h = v.getGoldSheadId();
				   if(mwe.contains(h)){
					   v.setGoldShead(u.getId());
				   }
				}
				else{
					int h = v.getPredictedSheadId();
					   if(mwe.contains(h)){
						   v.setShead(u.getId());
					   }
				}
			}
			
		}
						
	}
	
	
	
	
	
	
	private static Sentence mergeFixedMWEs(Sentence s,String mweLabels){
		Sentence res = new Sentence(s);
		
		Map<Integer,String> gmwePoses = new HashMap<Integer, String>();
		Map<Integer,String> mwePoses = new HashMap<Integer, String>();
		Map<Integer,List<Unit>> gmwes = getMWEs(res, mweLabels,true,gmwePoses);
		
		Map<Integer,List<Unit>> mwes = getMWEs(res, mweLabels,false,mwePoses);
		
		
		addMWEUnits(gmwes, res, true,gmwePoses);
		addMWEUnits(mwes, res, false,mwePoses);
		
	    //System.err.println(mwes);
	   
		return res;
	}
	
	//mergeFixedMWEs(s,mweLabels)
	
	public static DepTreebank mergeFixedMWEs(final DepTreebank tb,final String mweLabel){
		return modifyTreebank(tb,new SentenceModifier() {
			
			@Override
			public Sentence modify(Sentence s) {
				
				return mergeFixedMWEs(s,mweLabel);
			}
		});
	}
	
	private static String[] splitLabel(String label, String regmweLabel){
		if(!label.contains(regmweLabel)){		
		  return null;
		}
		int i = label.indexOf(regmweLabel);
		String function = label.substring(0,i-1);
		String mwePos = label.substring(i+regmweLabel.length() + 1);
		
				
		
		return new String[]{function,mwePos};
		
	}
	
	private static Unit findMWESyntacticHead(Unit u,Sentence res,boolean goldAnnotation,String regmweLabel){
		String label = u.getSLabel(goldAnnotation);
		Unit h = u;
		
		while(label.contains(regmweLabel)){
			int hid = h.getSHead(goldAnnotation);
			
			h = res.get(hid);
			//System.err.println(h+" "+label);
			label = h.getSLabel(goldAnnotation);
		}
		if(h == u){
			return null;
		}
		//System.err.println(u+"=="+h);
		return h;
	}
	
	private static String concatenateLemmas(Unit mwe,Sentence s){
		StringBuilder sb = new StringBuilder();
		for(int i:mwe.getPositions()){
			sb.append("_");
			sb.append(s.get(i).getLemma());
		}
		return sb.substring(1);
	}
	
	
	private static void addRegularMWEUnits(Sentence res,Map<Unit,List<Unit>> mwes,Map<Unit,String> mwePoses,boolean goldAnnotation){
		Set<Unit> set = mwes.keySet();
		for(Unit u:set){
			List<Unit> comp = mwes.get(u);
			String pos = mwePoses.get(u);
			addMWEUnit(u, comp, res, goldAnnotation, pos, false);
		}	
	}
	
	private static Map<Unit,List<Unit>> getRegularMWEs(Sentence res, String regmweLabel,boolean goldAnnotation,Map<Unit,String> mwePoses){
		HashMap<Unit,List<Unit>> mwes = new HashMap<Unit, List<Unit>>();
		for(Unit u:res.getTokens()){
			//System.err.println(u);
			Unit r = findMWESyntacticHead(u,res,goldAnnotation,regmweLabel);
			if(r != null){
				List<Unit> c = mwes.get(r);
				if(c == null){
					c = new LinkedList<Unit>();
					mwes.put(r, c);
					//System.err.println(u+"--");
					String label = u.getSLabel(goldAnnotation); 
					//System.err.println(u+"--"+label);
					String pos = splitLabel(label, regmweLabel)[1];
					mwePoses.put(r,pos);
				}
				c.add(u);
			}
		}
		
		return mwes;
	}
	
	
	private static Sentence mergeRegularMWEs(Sentence s,String regmweLabel){
		Sentence res = new Sentence(s);
		
		HashMap<Unit,String> mwePoses = new HashMap<Unit, String>();
		HashMap<Unit,String> gmwePoses = new HashMap<Unit, String>();
		
	
		//getMWEs
		Map<Unit,List<Unit>> gmwes = getRegularMWEs(res,regmweLabel,true,gmwePoses);
		Map<Unit,List<Unit>> mwes = getRegularMWEs(res,regmweLabel,false,mwePoses);
		
		//remove mwe label from syntactic labels
		for(Unit u:res.getTokens()){
			modifyLabel(u, regmweLabel, true);
			modifyLabel(u, regmweLabel, false);
			
		}
		
		//	add merged unit
		addRegularMWEUnits(res,mwes,mwePoses,false);
		addRegularMWEUnits(res,gmwes,gmwePoses,true);
		//addMWEUnit
		return res;
	}
	
	
	public static DepTreebank mergeRegularMWEs(final DepTreebank tb,final String regmweLabel){
		return modifyTreebank(tb,new SentenceModifier() {
			
			@Override
			public Sentence modify(Sentence s) {
				
				return mergeRegularMWEs(s,regmweLabel);
			}
		});
	}
	
	
	private static void modifyLabel(Unit u, String regmweLabel, boolean goldAnnotation){
		String label = u.getSLabel(goldAnnotation);
		//System.err.println(u.getSlabel()+"--"+u.getGoldSlabel());
		String[] labels = splitLabel(label,regmweLabel);
		if(labels != null){
			u.setSlabel(labels[0], goldAnnotation);
			

		}
	}
	
	
	private static Sentence removeRegularMWEs(Sentence s, String regmweLabel){
		
		Sentence res = new Sentence(s);
		for(Unit u:res.getTokens()){
			    modifyLabel(u,regmweLabel,true);
			    modifyLabel(u,regmweLabel,false);
		}
		
		return res;
	}
	
	public static DepTreebank removeRegularMWEs(final DepTreebank tb,final String regmweLabel){
return modifyTreebank(tb,new SentenceModifier() {
			
			@Override
			public Sentence modify(Sentence s) {
				return removeRegularMWEs(s,regmweLabel);
			}
		});
	}
	
	

	
	
	
	
	//on gold annotation only
	
	public static DepTreebank binarizeMWE(DepTreebank tb,final boolean rightBinarization){
		return modifyTreebank(tb,new SentenceModifier() {
			
			
			private Unit mergeUnits(Unit u1, Unit u2,Sentence s){
				String form = u1.getForm()+"_"+u2.getForm();
				int [] pos1 = u1.getPositions();
				int [] pos2 = u2.getPositions();
				int[] positions = new int[pos1.length+pos2.length];
				
				// fill positions
				for(int i = 0 ; i < pos1.length ; i++){
					positions[i] = pos1[i];
				}
				for(int i = 0 ; i < pos2.length ; i++){
					positions[i+pos1.length] = pos2[i];
				}
				
				int id = s.getUnits().size() + 1;
				//System.err.println(form);
				//System.err.println(Arrays.toString(positions));
				
				Unit mwe = mweUnitAlreadyExists(positions, s);
				//System.err.println(mwe);
				
				
				if(mwe == null){
					 //System.err.println("NEW");
				      mwe = new Unit(id,form, positions);
				      mwe.setGoldShead(-1);
				      mwe.setGoldSlabel(null);
				      mwe.setPos("*"); //intermediate MWE node
				      s.add(mwe);
				}
				u1.setGoldLHead(mwe.getId());
			      u2.setGoldLHead(mwe.getId());
				//System.err.println(u1+" "+mwe.getId());
				
				return mwe;
			}
			
	
			
			// for now left binarizarion only
			private void binarizeMWE(Unit mwe,Sentence s, boolean rightBinarization){
				int[] positions = mwe.getPositions();
				//System.err.println(Arrays.toString(positions));
				Unit u = s.get(positions[0]);
				//System.err.println(u);
				for(int i = 1 ; i < positions.length ; i++){
					Unit up = s.get(positions[i]);
					//System.err.println(up);
					u = mergeUnits(u,up,s);
				}
				
			}
			
			
			@Override
			public Sentence modify(Sentence s) {
				Sentence res = new Sentence(s);
				List<Unit> mwes = s.getMWUnits();
				int size = mwes.size();
				for(int i = 0 ; i < size ; i++){
					Unit mwe = mwes.get(i);
					//System.err.println(mwe);
					binarizeMWE(mwe,res,rightBinarization);
				}
				
				return res;
			}
		});
	}
	
	
	private static Map<Unit,List<Unit>> constructChildren(Sentence s,boolean goldAnnotation){
		Map<Unit,List<Unit>> children = new HashMap<Unit, List<Unit>>();
		for(Unit u:s.getUnits()){
			int h = u.getSHead(goldAnnotation);
			if(h < 0){
				continue;
			}
			Unit head = s.get(h);
			//String label = goldAnnotation?u.getGoldSlabel():u.getSlabel();
			List<Unit> ch = children.get(head);
			if(ch == null){
				ch = new LinkedList<Unit>();
				children.put(head,ch);
			}
			ch.add(u);
		}
		return children;
	}
	
	public static DepTreebank unmergeFixedMWE(DepTreebank tb, final String mweLabel){
		return modifyTreebank(tb, new SentenceModifier() {
			Map<Unit,List<Unit>> children;
			
			
			private Unit decomposeFixedMWE(Unit u, Sentence s,boolean goldAnnotation, String mweLabel){
				Unit u0 = s.get(u.getPositions()[0]);
				u0.setLhead(0, goldAnnotation);
				int u0id = u0.getId();
				if(u.isFixedMWE(s,goldAnnotation)){
					int[] positions = u.getPositions();
					for(int i = 1; i < positions.length; i++){
						Unit d = s.get(positions[i]);
						d.setLhead(0, goldAnnotation);
						if(goldAnnotation){
							d.setGoldShead(u0id);
							d.setGoldSlabel(mweLabel);
						}
						else{
							d.setShead(u0id);
							d.setPredictedSlabel(mweLabel);
						}
					}
					
				}
				return u0;
			}
			
			private void unmerge(Unit u, Unit parent,Sentence s,boolean goldAnnotation, String mweLabel){
				Unit u0 = decomposeFixedMWE(u, s, goldAnnotation,mweLabel);
				//System.err.println(parent+"-->"+u+"=="+u0);
				List<Unit> ch = children.get(u);
				if(parent != null && u0 != u){
					if(goldAnnotation){
						u0.setGoldShead(parent.getId());
						u0.setGoldSlabel(u.getGoldSlabel());
					}
					else{
						u0.setShead(parent.getId());
						u0.setPredictedSlabel(u.getPredictedSlabel());
					}
				}
				ch = ch == null?Collections.<Unit>emptyList():ch;
				for(Unit c:ch){
					unmerge(c,u0,s,goldAnnotation,mweLabel);
				}
			}
			
			
			private void unmergeFixedMWE(Sentence s, String mweLabel,boolean goldAnnotation){
				children = constructChildren(s,goldAnnotation); 
				unmerge(s.get(0),null,s,goldAnnotation,mweLabel);
			}
			
			
			@Override
			public Sentence modify(Sentence s) {
				Sentence res = new Sentence(s);
				unmergeFixedMWE(res,mweLabel,true);
				unmergeFixedMWE(res,mweLabel,false);
				return res;
			}
		});
		
		
	}
	
	public static DepTreebank unMergeMWE(DepTreebank tb,final String mweLabel){
		return modifyTreebank(tb, new SentenceModifier() {
			
			private void getMWEs(Sentence res, boolean goldAnnotation,Map<Unit,List<Unit>> map,Map<Unit,Unit> mweHeads){
								
				for(Unit u:res.getTokens()){
				    Unit r = u.findGoldLexicalRoot(res);
				    
				    if(!goldAnnotation){
				    	r = u.findPredictedLexicalRoot(res);
				    }
				    
				    if(r != u){  //u is part of an MWE
				    	if(mweHeads.get(r) == null){
					    	mweHeads.put(r,u);
					    }
				    	List<Unit> list = map.get(r);
				    	if(list == null){
				    		list = new ArrayList<Unit>();
				    		map.put(r, list);
				    	}
				    	list.add(u);				    	
				    }
					
				}
				
				
			}
			
			
			private void modifyInternalArcs(Map<Unit,List<Unit>> mwes,Map<Unit,Unit> mweHeads,Sentence res, boolean goldAnnotation){
				
				for(Unit mwe:mwes.keySet()){
					List<Unit> components = mwes.get(mwe);
					Unit root = components.get(0);
					
				
					
					for(int i = 1 ; i < components.size() ; i++){
						Unit u = components.get(i);
						if(goldAnnotation){
							//u.setGoldLHead(0);
							u.setGoldShead(root.getId());
							u.setGoldSlabel(mweLabel);
						}
						else{
							//u.setLhead(0);
							u.setShead(root.getId());
							u.setPredictedSlabel(mweLabel);
						}
						
					}
				}
				
			}
			
			private Unit getRoot(Unit u,Map<Unit,Unit> mweHeads){
				Unit r = mweHeads.get(u);
				if(r == null) {
					return u;
				}
				return r;
			}
			
			
			private void modifyExternalArcs(Sentence s,Map<Unit,Unit> mweHeads,boolean goldAnnotation){
				//System.err.println(mweHeads);
				for(Unit u:s.getTokenSequence(goldAnnotation)){
					Unit dep = getRoot(u,mweHeads);					
					
					String label = u.getSLabel(goldAnnotation);
					int h = u.getSHead(goldAnnotation);
										
					
					Unit head = getRoot(s.get(h),mweHeads);
					//System.err.println(u+" "+mweHeads.get(u)+ " "+head );
					if(goldAnnotation){
						dep.setGoldShead(head.getId());
						dep.setGoldSlabel(label);
					}
					else{
						dep.setShead(head.getId());
						dep.setPredictedSlabel(label);
					}
				
					
				}
				
			}
			
			private void clearLexicalLinks(Sentence s){
				for(Unit u:s.getTokens()){
					u.setPredictedLhead(0);
					u.setGoldLHead(0);
				}
			}
			
			
			
			private Sentence unmerge(Sentence s){
				Sentence res = new Sentence(s);
				Map<Unit,List<Unit>> mwes = new HashMap<Unit, List<Unit>>();
				Map<Unit,Unit> mweHeads = new HashMap<Unit,Unit>();
				getMWEs(res,true,mwes,mweHeads);
				modifyInternalArcs(mwes, mweHeads, res, true);
				modifyExternalArcs(res,mweHeads,true);
				mwes = new HashMap<Unit, List<Unit>>();
				mweHeads = new HashMap<Unit,Unit>();
				getMWEs(res,false,mwes,mweHeads);
				modifyInternalArcs(mwes, mweHeads, res, false);
				modifyExternalArcs(res,mweHeads,false);
				clearLexicalLinks(res);
				return res;
			}
			
			
			@Override
			public Sentence modify(Sentence s) {				
				return unmerge(s);
			}
		});
		
	}
	
	
	//on predicted annotation only
	public static DepTreebank unbinarizeMWE(DepTreebank tb,boolean rightBinarization){
		return modifyTreebank(tb, new SentenceModifier(){

			
			private Sentence unbinarize(Sentence s){
				Sentence res = new Sentence(s);
				for(Unit mwe:res.getMWUnits()){
					//System.err.println(mwe+"="+mwe.getGoldLHead());
					if(mwe.isPredictedLexicalRoot() && mwe.isPredictedMWE(res)){
						
						//System.err.println(mwe);
						int[] positions = mwe.getPositions();
						for(int i:positions){ //for each component
							Unit c = res.get(i);
							//System.err.println(c);
							c.setPredictedLhead(mwe.getId());
						}
					}
				}
				return res;
			}
			
			@Override
			public Sentence modify(Sentence s) {
				return unbinarize(s);
			}
			
		});
	}
	
	
	public static DepTreebank unlabelMWEArcs(DepTreebank tb, final String mwelabel){
		return modifyTreebank(tb, new SentenceModifier(){

			@Override
			public Sentence modify(Sentence s) {
				Sentence res = new Sentence(s);
				for(Unit u:res.getUnits()){
					
					String label = u.getGoldSlabel();
					
					if(label != null && label.startsWith(mwelabel)){
						u.setGoldSlabel(mwelabel);					
					}
					label = u.getPredictedSlabel();
					//System.err.println(label);
					if(label != null && label.startsWith(mwelabel)){
						u.setPredictedSlabel(mwelabel);
					}
				}
				return res;
			}
			
		});
	}
	
	
	
	
	public static DepTreebank removeMwePOSInLabels(DepTreebank tb, final String fixedMweLabel,final String regularMweLabel){
		return modifyTreebank(tb, new SentenceModifier() {
	
			private void removeMwePosInLabels(Unit u, boolean goldAnnotation, Sentence s){
				int h = u.getSHead(goldAnnotation);
				if(h < 0){
					return;
				}
				String l = u.getSLabel(goldAnnotation);
				if(l.startsWith(fixedMweLabel)){
					u.setSlabel(fixedMweLabel,goldAnnotation);
				}
				String[] tab = splitLabel(l, regularMweLabel);
				if(tab != null){
					u.setSlabel(tab[0],goldAnnotation);
				}
				
			}
			
			@Override
			public Sentence modify(Sentence s) {
				Sentence res = new Sentence(s);
				for(Unit u:res.getTokens()){
					removeMwePosInLabels(u,true,res);
					removeMwePosInLabels(u,false,res);
					
				}
				return res;
			}
		});
	}
	
	
	
	static interface SentenceModifier{
		Sentence modify(Sentence s);
	}
	
	private static DepTreebank modifyTreebank(final DepTreebank tb,final SentenceModifier mod){
		return new DepTreebank() {

			@Override
			public Iterator<Sentence> iterator() {
				return new Iterator<Sentence>() {
					Iterator<Sentence> it = tb.iterator();

					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public Sentence next() {
						Sentence s = it.next();
						return mod.modify(s);
					}

					@Override
					public void remove() {
						throw new IllegalStateException();

					}
				};
			}
		};
	}
	
	
}
