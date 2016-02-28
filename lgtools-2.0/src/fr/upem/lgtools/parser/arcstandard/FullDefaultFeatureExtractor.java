/**
 * 
 */
package fr.upem.lgtools.parser.arcstandard;



import java.util.Deque;

import fr.upem.lgtools.parser.Buffer;
import fr.upem.lgtools.parser.Configuration;
import fr.upem.lgtools.parser.DepTree;
import fr.upem.lgtools.parser.features.FeatureMapping;
import fr.upem.lgtools.parser.features.FeatureUtils;
import fr.upem.lgtools.parser.features.FeatureVector;
import fr.upem.lgtools.text.Unit;

/**
 * @author Mathieu
 *
 */
public class FullDefaultFeatureExtractor extends DefaultFeatureExtractor {

	public FullDefaultFeatureExtractor(FeatureMapping featureMapping) {
		super(featureMapping);
	}
	
	private void extractLexicalFeatures(Configuration<DepTree> configuration, FeatureVector feats){
		Buffer buffer = configuration.getFirstBuffer();
		Deque<Unit> stack= configuration.getSecondStack();
		//feats.add("BIAS");
		//feats.add("EMPTY2");
		Unit s0u = stack.peek();
		Unit s1u = FeatureUtils.getSecondElementInStack(stack);
		Unit b0u = FeatureUtils.getFirstElementInBuffer(buffer);
		//Unit b1u = getSecondElementInBuffer(buffer);
		
		FeatureUtils.addUnitFeatures("lex:s0u", s0u,feats,configuration);
		FeatureUtils.addUnitFeatures("lex:s1u", s1u,feats,configuration);
		
		FeatureUtils.addUnitPairFeatures("lex:s0u_s1u",s0u,s1u,feats,configuration);	
		FeatureUtils.addUnitPairFeatures("lex:s0u_b0u",s0u,b0u,feats,configuration);
		
		FeatureUtils.addUnitTripletFeatures("lex:s1u_s0u_b0u",s1u,s0u,b0u,feats,configuration);
		
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
		extract("stack1",configuration, configuration.getFirstStack(),feats);
		extractLexicalFeatures(configuration, feats);
		//extract("stack2",configuration, configuration.getSecondStack(),feats);
		return feats;
		
	}
	

}
