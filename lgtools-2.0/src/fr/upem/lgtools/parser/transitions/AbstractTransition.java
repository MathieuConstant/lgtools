/**
 * 
 */
package fr.upem.lgtools.parser.transitions;

import fr.upem.lgtools.parser.Configuration;

/**
 * @author Mathieu Constant
 *
 */
public abstract class AbstractTransition<T> implements Transition<T>{
	private final String id;
	
	public AbstractTransition(String id){
		this.id = id;
	}
	

	@Override
	public String id() {
		return id;
	}


	@Override
	public Configuration<T> perform(Configuration<T> configuration) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isValid(Configuration<T> configuration) {
		// TODO Auto-generated method stub
		return false;
	}


	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractTransition other = (AbstractTransition) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	

}
