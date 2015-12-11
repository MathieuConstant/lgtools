package fr.upem.lgtools.parser;

public interface Transition<T> {
	public void perform(BaseConfiguration<T> configuration);
	public boolean preconditions(BaseConfiguration<T> configuration);

}
