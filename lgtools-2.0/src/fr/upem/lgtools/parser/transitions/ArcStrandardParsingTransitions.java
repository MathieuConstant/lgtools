package fr.upem.lgtools.parser.transitions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.upem.lgtools.parser.DepTree;

public class ArcStrandardParsingTransitions implements SystemTransitions<DepTree> {
	private final Map<String,Transition<DepTree>> map = new HashMap<String, Transition<DepTree>>();

	public ArcStrandardParsingTransitions(List<String> labels) {
		Transition<DepTree> sh = new ShiftTransition<DepTree>();
		map.put(sh.id(), sh);
		Transition<DepTree> t;
		for(String l:labels){
			t = new LeftArcTransition(l);
			map.put(t.id(), t);
		}
		for(String l:labels){
			t = new RightArcTransition(l);
			map.put(t.id(), t);
		}
		
	}
	
	
	@Override
	public Transition<DepTree> getTransition(String transitionId) {
		if(map.containsKey(transitionId)){
			throw new IllegalArgumentException("Transition entitled "+transitionId+ " does not exist");
		}
		return map.get(transitionId);
	}
	

}
