package fr.upem.lgtools.text;


public class UnitFactory {
	private final static int ROOT_ID = 0;
	private final static String ROOT_FORM = "_ROOT_";
	private final static String CONLL_DELIM = "\t";
	private final static int CONLL_ID = 0;
	private final static int CONLL_FORM = 1;
	private final static int CONLL_LEMMA = 2;
	private final static int CONLL_CPOS = 3;
	private final static int CONLL_POS = 4;
	private final static int CONLL_FEATS = 5;
	private final static String CONLL_FEATS_DELIM = "\\|";
	private final static String CONLL_FEAT_DELIM = "=";
	private final static int CONLL_HEAD = 6;
	private final static int CONLL_LABEL = 7;
	private final static String DUMMY = "_";
	
	public static Unit createRootUnit(){
		return new Unit(ROOT_ID,ROOT_FORM,ROOT_ID);		
	}
	
	private static void parseFeats(Unit u, String feats){
		if(feats.equals(DUMMY)){
			return;
		}
		String[] tab = feats.split(CONLL_FEATS_DELIM);
	    for(String f:tab){
	    	
	    	String[] ftab = f.split(CONLL_FEAT_DELIM);	    	
	    	if(ftab.length != 2){
	    		throw new IllegalStateException("Feature "+f+ " is illegal: format is attribute=value");
	    	}
	    	u.addFeature(ftab[0], ftab[1]);
	    }
		
	}
	
	public static Unit createUnitFromConllString(String line){
		String[] tab = line.split(CONLL_DELIM);
		Unit u = new Unit(Integer.parseInt(tab[CONLL_ID]), tab[CONLL_FORM], Integer.parseInt(tab[CONLL_ID]));
		u.setLemma(tab[CONLL_LEMMA]);
		u.setCpos(tab[CONLL_CPOS]);
		u.setPos(tab[CONLL_POS]);
		parseFeats(u,tab[CONLL_FEATS]);
		int sh = tab[CONLL_HEAD].equals(DUMMY)?-1:Integer.parseInt(tab[CONLL_HEAD]); 
		u.setShead(sh);
		u.setSlabel(tab[CONLL_LABEL]);
		return u;
	}
}