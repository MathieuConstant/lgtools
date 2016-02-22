/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;



import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.FeatureVector;

/**
 * @author Mathieu
 *
 */
public class FullDefaultFeatureExtractor extends DefaultFeatureExtractor {

	public FullDefaultFeatureExtractor(FeatureMapping featureMapping) {
		super(featureMapping);
	}
	
	@Override
	public FeatureVector perform(Configuration<DepTree> configuration) {
		FeatureVector feats = new FeatureVector(fm);
		/*Deque<Unit> stack = configuration.getFirstStack();
		Buffer b = configuration.getFirstBuffer();
		Unit s0u = stack.peek();
		Unit s1u = DefaultFeatureExtractor.getSecondElementInStack(stack);
		Unit b0u = DefaultFeatureExtractor.getFirstElementInBuffer(b);
		feats.add("s0u_f="+s0u.getForm());
		feats.add("s1u_f="+s1u.getForm());
		feats.add("b0u_f="+b0u.getForm());*/
		//System.err.println("ICI");
		extract(configuration, configuration.getFirstStack(),feats);
		//extract(configuration, configuration.getSecondStack(),feats);
		return feats;
		
	}
	

}
