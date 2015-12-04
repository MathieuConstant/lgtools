package fr.upem.lgtools.test;

import fr.upem.lgtools.text.Unit;

public class Test {

	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello world!");
        Unit u = new Unit(0, "WWW", 0,8,9);
        u.setLemma("root");
        System.out.println(u);
	}

}
