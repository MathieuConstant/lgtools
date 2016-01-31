/**
 * 
 */
package fr.upem.lgtools.parser.model;

import java.io.IOException;
import java.util.List;

import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.features.DefaultFeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureExtractor;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.text.DepTreebank;
import fr.upem.lgtools.text.Sentence;
import fr.upem.lgtools.text.Unit;

/**
 * @author Matthieu Constant
 *
 */
public abstract class TransitionBasedDependencyParserModel extends
		TransitionBasedModel2<DepTree> {

	public TransitionBasedDependencyParserModel(FeatureMapping fm,
			DepTreebank tb) {
		super(fm, tb);
		
	}

	public TransitionBasedDependencyParserModel(String filename) throws IOException {
		super(filename);		
	}

	
	@Override
	public Configuration<DepTree> getInitialConfiguration(List<Unit> units) {		
		return new Configuration<DepTree>(units, new DepTree(units.size() + 1), 1, 1);
	}

	
	@Override
	public void updateSentenceAfterAnalysis(Sentence s, DepTree analysis) {
		for(Unit u:s.getTokens()){
			u.setShead(analysis.getHead(u.getId()));			
			u.setSlabel(analysis.getlabel(u.getId()));
		}
		
	}

	@Override
	protected FeatureExtractor<DepTree> getFeatureExtractor(FeatureMapping fm) {		
		return new DefaultFeatureExtractor(fm);
	}
	
	

}
