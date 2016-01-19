/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

/**
 * @author Matthieu Constant
 *
 */
public abstract class AbstractTransition<T> implements Transition<T> {
	final String type;
	
	public AbstractTransition(String type) {
		this.type = type;
	}
		
	@Override
	public String id() {		
		return type;
	}

	
	@Override
	public String toString(){
		return id();
	}
	
}
