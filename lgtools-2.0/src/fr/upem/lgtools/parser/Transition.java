package fr.upem.lgtools.parser;

public interface Transition<T> {
	public Configuration<T> perform(Configuration<T> configuration);
	public boolean isValid(Configuration<T> configuration);
	public String id();

}
