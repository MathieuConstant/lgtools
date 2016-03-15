/**
 * 
 */
package fr.upem.lgtools.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.upem.lgtools.evaluation.SimpleEvaluation;
import fr.upem.lgtools.parser.Analysis;
import fr.upem.lgtools.parser.Constants;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.Parameters;
import fr.upem.lgtools.parser.PerceptronTransitionBasedSystem;
import fr.upem.lgtools.parser.TransitionBasedSystem;
import fr.upem.lgtools.parser.arcstandard.ArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.arcstandard.BaselineFullyMWEAwareArcStandardTransitionBasedModel;
import fr.upem.lgtools.parser.arcstandard.ImplicitCmpFullyMWEAwareArcStandardTransitionBasedModel;
import fr.upem.lgtools.parser.arcstandard.SimpleLabeledMergeArcStandardTransitionBasedParserModel;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.HashFeatureMapping;
import fr.upem.lgtools.parser.mwereco.MweRecognizerModel;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu
 *
 */
public class TreebankProcesses {


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


	private static void addMWEUnit(Unit head, List<Unit> components,Sentence s,boolean goldMwes,String pos,boolean fixedMwe, String mweLabel){
		int[] positions = new int[components.size() + 1];

		positions[0] = head.getId();
		int i = 1;
		for(Unit c: components){
			positions[i] = c.getId();
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
		
		StringBuilder mweForm = new StringBuilder();
		for(int c:positions){
			mweForm.append("_").append(s.get(c).getForm());
		}

		if(pos == null || pos.equals("")){
			pos = mweLabel;
		}

		Unit mwe = mweUnitAlreadyExists(positions, s); 
		if(mwe == null){
			mwe = new Unit(s.getUnits().size() + 1,mweForm.substring(1),positions);

			s.add(mwe);
			//System.err.println("MWE="+mwe);
		}

		if(goldMwes){
			mwe.setGoldCpos(mweLabel);
			mwe.setGoldPos(pos); //the pos = pos of the label
			//mwe.setGoldPos("MWE");  // no pos: specific tag for MWE
			//mwe.setGoldPos(mwe.getGoldPOSPattern(s));  //pos = component pos sequence
			if(fixedMwe){
				mwe.setGoldShead(head.getGoldSheadId());
				mwe.setGoldSlabel(head.getGoldSlabel());
				head.setGoldShead(-1);
				head.setGoldSlabel(null);
			}
		}
		else{
			mwe.setCpos(mweLabel);
			mwe.setPos(pos);  //same as above
			//mwe.setPos("MWE");  //same as above
			//mwe.setPos(mwe.getPOSPattern(s)); //same as aboe
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
		mwe.setLemma(concatenateLemmas(mwe, s)); //lemma 
		//mwe.setLemma(s.get(mwe.getPositions()[0]).getLemma());  //MODIFY IF TEST FAILS
	}


	private static void addMWEUnits(Map<Integer,List<Unit>> mwes,Sentence s,boolean goldMwes,Map<Integer,String> mwePoses, String mweLabel){
		for(int i = 0 ; i < s.getTokens().size() ; i++){
			Unit h = s.getTokens().get(i);
			if(mwes.containsKey(h.getId())){
				addMWEUnit(h,mwes.get(h.getId()),s,goldMwes,mwePoses.get(h.getId()),true,mweLabel);
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






	private static Sentence mergeFixedMWEs(Sentence s){
		Sentence res = new Sentence(s);
		//Sentence res = s;

		
		Map<Integer,String> gmwePoses = new HashMap<Integer, String>();
		Map<Integer,String> mwePoses = new HashMap<Integer, String>();
		Map<Integer,List<Unit>> gmwes = getMWEs(res, Constants.MWE_LABEL,true,gmwePoses);

		Map<Integer,List<Unit>> mwes = getMWEs(res, Constants.MWE_LABEL,false,mwePoses);


		addMWEUnits(gmwes, res, true,gmwePoses, Constants.MWE_LABEL);
		addMWEUnits(mwes, res, false,mwePoses, Constants.MWE_LABEL);

		//System.err.println(mwes);

		return res;
	}

	//mergeFixedMWEs(s,mweLabels)

	public static SentenceProcess mergeFixedMWEs(){
		return new AbstractSentenceProcess() {

			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {

				return mergeFixedMWEs(s);
			}
		};
	}


	public static SentenceProcess copy(){
		return new AbstractSentenceProcess() {

			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {
				return new Sentence(s);
			}
		};
	}


	private static String[] splitLabel(String label){
		if(!label.contains(Constants.REG_MWE)){		
			return null;
		}
		int i = label.indexOf(Constants.REG_MWE);
		String function = label.substring(0,i-1);
		String mwePos = label.substring(i+Constants.REG_MWE.length() + 1);



		return new String[]{function,mwePos};

	}

	private static Unit findMWESyntacticHead(Unit u,Sentence res,boolean goldAnnotation){
		String label = u.getSLabel(goldAnnotation);
		Unit h = u;

		while(label.contains(Constants.REG_MWE)){
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
			String l = s.get(i).getLemma();
			if(l == null){
				return null;
			}
			sb.append("_");
			sb.append(l);
		}
		return sb.substring(1);
	}


	private static void addRegularMWEUnits(Sentence res,Map<Unit,List<Unit>> mwes,Map<Unit,String> mwePoses,boolean goldAnnotation){
		Set<Unit> set = mwes.keySet();
		for(Unit u:set){
			List<Unit> comp = mwes.get(u);
			String pos = mwePoses.get(u);
			addMWEUnit(u, comp, res, goldAnnotation, pos, false,Constants.REG_MWE);
		}	
	}

	private static Map<Unit,List<Unit>> getRegularMWEs(Sentence res, boolean goldAnnotation,Map<Unit,String> mwePoses){
		HashMap<Unit,List<Unit>> mwes = new HashMap<Unit, List<Unit>>();
		for(Unit u:res.getTokens()){
			//System.err.println(u);
			Unit r = findMWESyntacticHead(u,res,goldAnnotation);
			if(r != null){
				List<Unit> c = mwes.get(r);
				if(c == null){
					c = new LinkedList<Unit>();
					mwes.put(r, c);
					//System.err.println(u+"--");
					String label = u.getSLabel(goldAnnotation); 
					//System.err.println(u+"--"+label);
					String pos = splitLabel(label)[1];
					mwePoses.put(r,pos);
				}
				c.add(u);
			}
		}

		return mwes;
	}


	private static Sentence mergeRegularMWEs(Sentence s){
		Sentence res = new Sentence(s);
		//Sentence res =s;

		HashMap<Unit,String> mwePoses = new HashMap<Unit, String>();
		HashMap<Unit,String> gmwePoses = new HashMap<Unit, String>();


		//getMWEs
		Map<Unit,List<Unit>> gmwes = getRegularMWEs(res,true,gmwePoses);
		Map<Unit,List<Unit>> mwes = getRegularMWEs(res,false,mwePoses);

		//remove mwe label from syntactic labels
		for(Unit u:res.getTokens()){
			modifyLabel(u, true);
			modifyLabel(u, false);

		}

		//	add merged unit
		addRegularMWEUnits(res,mwes,mwePoses,false);
		addRegularMWEUnits(res,gmwes,gmwePoses,true);
		//addMWEUnit
		return res;
	}


	public static SentenceProcess mergeRegularMWEs(){
		return new AbstractSentenceProcess(){

			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {

				return mergeRegularMWEs(s);
			}
		};
	}


	private static void modifyLabel(Unit u, boolean goldAnnotation){
		String label = u.getSLabel(goldAnnotation);
		//System.err.println(u.getSlabel()+"--"+u.getGoldSlabel());
		String[] labels = splitLabel(label);
		if(labels != null){
			u.setSlabel(labels[0], goldAnnotation);
		}
	}


	private static Sentence removeRegularMWEs(Sentence s){
		Sentence res = new Sentence(s);
        //Sentence res = s;
        
		for(Unit u:res.getTokens()){
			modifyLabel(u,true);
			modifyLabel(u,false);
		}

		return res;
	}

	public static SentenceProcess removeRegularMWEs(){
		return new AbstractSentenceProcess(){

			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {

				return removeRegularMWEs(s);
			}
		};
	}







	//on gold annotation only
	//TODO: enable right binarization, binarization of gold annotation only?

	public static SentenceProcess binarizeMWE(final boolean rightBinarization){
		return new AbstractSentenceProcess() {


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
			public Sentence apply(Sentence s, SimpleEvaluation eval) {
				Sentence res = new Sentence(s);
				//Sentence res = s;
				
				List<Unit> mwes = s.getMWUnits();
				int size = mwes.size();
				for(int i = 0 ; i < size ; i++){
					Unit mwe = mwes.get(i);
					//System.err.println(mwe);
					binarizeMWE(mwe,res,rightBinarization);
				}

				return res;
			}
		};
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

	public static SentenceProcess unmergeFixedMWE(){
		return new AbstractSentenceProcess() {
			Map<Unit,List<Unit>> children;


			private Unit decomposeFixedMWE(Unit u, Sentence s,boolean goldAnnotation){
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
							d.setGoldSlabel(Constants.MWE_LABEL);
						}
						else{
							d.setShead(u0id);
							d.setPredictedSlabel(Constants.MWE_LABEL);
						}
					}

				}
				return u0;
			}

			private void unmerge(Unit u, Unit parent,Sentence s,boolean goldAnnotation){
				Unit u0 = decomposeFixedMWE(u, s, goldAnnotation);
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
					unmerge(c,u0,s,goldAnnotation);
				}
			}


			private void unmergeFixedMWE(Sentence s,boolean goldAnnotation){
				children = constructChildren(s,goldAnnotation); 
				unmerge(s.get(0),null,s,goldAnnotation);
			}


			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {
				Sentence res = new Sentence(s);
				//Sentence res = s;
				unmergeFixedMWE(res,true);
				unmergeFixedMWE(res,false);
				return res;
			}
		};

	}
	
	public static String generateLabel(String oldLabel,String mwePos){
		return oldLabel+"_"+Constants.REG_MWE+"_"+mwePos;
	}
	
 
	
	
	public static SentenceProcess unmergeRegularMWE(){
		return new AbstractSentenceProcess() {

			
			private boolean contains(int[] x,int val){
				for(int v:x){
					if(v == val){
						return true;
					}
				}
				return false;
			}
			
			private void unmergeRegularMWE(Unit mwe,Sentence s,boolean goldAnnotation){
				for(int c:mwe.getPositions()){
					Unit mc = s.get(c);
					if(mc.hasSyntacticHead(goldAnnotation)){
						//System.err.println(mc+"--"+Arrays.toString(mwe.getPositions())+"=="+mc.getSHead(goldAnnotation));
						if(contains(mwe.getPositions(),mc.getSHead(goldAnnotation))){
							//System.err.println("H");
							mc.setLhead(0, goldAnnotation);
							String label = generateLabel(mc.getSLabel(goldAnnotation),mwe.getPos());
							mc.setSlabel(label, goldAnnotation);
						}
					}
				}
				
			}
			
			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {
				Sentence res = new Sentence(s); 
				for(Unit mwe :res.getMWUnits()){
					if(!mwe.isFixedMWE(res, false)){
						unmergeRegularMWE(mwe, res, false);
					}
					if(!mwe.isFixedMWE(res, true)){
						unmergeRegularMWE(mwe, res, true);
					}
					
				}
				return res;
			}
		};
		
	}
	
	
	
	public static SentenceProcess unMergeMWE(DepTreebank tb,final String mweLabel){
		return new AbstractSentenceProcess() {

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
				//Sentence res = s;
				
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
			public Sentence apply(Sentence s, SimpleEvaluation eval) {				
				return unmerge(s);
			}
		};

	}


	//on predicted annotation only
	public static SentenceProcess unbinarizeMWE(boolean rightBinarization){
		return new AbstractSentenceProcess() {
			private Sentence unbinarize(Sentence s){
				Sentence res = new Sentence(s);
				//Sentence res = s;
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
			public Sentence apply(Sentence s, SimpleEvaluation eval) {
				return unbinarize(s);
			}
		};


	}


	public static SentenceProcess unlabelMWEArcs(){
		return new AbstractSentenceProcess(){

			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {
				Sentence res = new Sentence(s);
				//Sentence res = s;
				for(Unit u:res.getUnits()){

					String label = u.getGoldSlabel();

					if(label != null && label.startsWith(Constants.MWE_LABEL)){
						u.setGoldSlabel(Constants.MWE_LABEL);					
					}
					label = u.getPredictedSlabel();
					//System.err.println(label);
					if(label != null && label.startsWith(Constants.MWE_LABEL)){
						u.setPredictedSlabel(Constants.MWE_LABEL);
					}
				}
				return res;
			}

		};
	}






	public static SentenceProcess removeMwePosInLabels(){
		return new AbstractSentenceProcess() {

			private void removeMwePosInLabels(Unit u, boolean goldAnnotation, Sentence s){
				int h = u.getSHead(goldAnnotation);
				if(h < 0){
					return;
				}
				String l = u.getSLabel(goldAnnotation);
				if(l.startsWith(Constants.MWE_LABEL)){
					u.setSlabel(Constants.MWE_LABEL,goldAnnotation);
				}
				String[] tab = splitLabel(l);
				if(tab != null){
					u.setSlabel(tab[0],goldAnnotation);
				}

			}

			@Override
			public Sentence apply(Sentence s, SimpleEvaluation eval) {
				Sentence res = new Sentence(s);
				//Sentence res = s;
				for(Unit u:res.getTokens()){
					removeMwePosInLabels(u,true,res);
					removeMwePosInLabels(u,false,res);

				}
				return res;
			}

			
		};
	}


   public static <T extends Analysis> SentenceProcess greedyParse(final TransitionBasedSystem<T> parser){
	   return new AbstractSentenceProcess() {
		
		@Override
		public Sentence apply(Sentence s, SimpleEvaluation eval) {
			T analysis = parser.greedyParse(s);
			parser.getModel().updateSentenceAfterAnalysis(s,analysis); 
 			//System.err.println(s.getTokenSequence(false));
 			
			return s;
		}

		
	};
	   
   }

  
	

   public static DepTreebank prepareTreebank(final DepTreebank tb,final SentenceProcess mod, final SimpleEvaluation eval){
	   return new DepTreebank() {
		   private int cnt = 0;
		   
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
						cnt++;
						if(cnt % 500 == 0){
							System.err.println(cnt+" sentences processed!");
						}
						
						Sentence s = it.next();
						return mod.apply(s,eval);
					}

					@Override
					public void remove() {
						throw new IllegalStateException();

					}
				};
			}
		};
   }
   
   
   

	public static void processTreebank(final DepTreebank tb,final SentenceProcess mod, final SimpleEvaluation eval){
		mod.initProcess();
		DepTreebank res = prepareTreebank(tb, mod, eval); 
		// process (use of empty loop)
		for(@SuppressWarnings("unused") Sentence s:res){
			// empty loop
		}
		mod.endProcess();
	}

	
	public static <T extends Analysis> void staticOracleMweTrain(DepTreebank tb, SentenceProcessComposition mod, String model,int iter) throws IOException{
		mod.initProcess();
		DepTreebank res = TreebankProcesses.prepareTreebank(tb, mod,null);
		FeatureMapping fm = new  HashFeatureMapping(1000000);
		MweRecognizerModel tbm = new MweRecognizerModel(fm, res);
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(res,null, model,iter);	
		mod.endProcess();
		
	}
	
	public static <T extends Analysis> void staticOracleTrain(DepTreebank tb, SentenceProcessComposition mod, Parameters param) throws IOException{
		
		mod.initProcess();
		DepTreebank res = TreebankProcesses.prepareTreebank(tb, mod,null);
		
		ArcStandardTransitionBasedParserModel tbm = null;
		FeatureMapping fm = new  HashFeatureMapping(param.modelSize);
	
		
		if(param.noSyntax){
			
			
		}
		else{
			if(param.baseline){
				tbm = new ArcStandardTransitionBasedParserModel(fm,res,param.projective);
			}
			else{
				if(param.fixedMweOnly){
					tbm = new SimpleLabeledMergeArcStandardTransitionBasedParserModel(fm,res,param.projective);  //need for parameter to distinguish both cases or automatic?
				}
				else{
					if(param.implicitComplete){
						tbm = new ImplicitCmpFullyMWEAwareArcStandardTransitionBasedModel(fm,res,false,param.projective); 

					}
					else{
						tbm = new BaselineFullyMWEAwareArcStandardTransitionBasedModel(fm,res,param.projective); 
					}


				}		
			}
		}
		TransitionBasedSystem<DepTree> parser = new PerceptronTransitionBasedSystem<DepTree>(tbm);
		parser.staticOracleTrain(res,null, param.model,param.iters);	
		mod.endProcess();
	}
	
	
}
