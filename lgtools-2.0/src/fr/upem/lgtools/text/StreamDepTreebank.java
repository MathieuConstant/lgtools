package fr.upem.lgtools.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class StreamDepTreebank extends DepTreebank {

   private BufferedReader reader;
	
	public StreamDepTreebank(BufferedReader reader) {
		this.reader = reader;
	}



	@Override
	public Iterator<Sentence> iterator() {
		return new Iterator<Sentence>() {

			private boolean canDoReading = true;
			private Sentence currentSentence;
			
			
			private Sentence readSentence() throws IOException{
				List<Unit> units = new ArrayList<Unit>();
				String line;
				if(reader == null){
					return null;
				}				
				while(((line = reader.readLine())!=null) && (!line.isEmpty())){
					//System.err.println("##"+line);
					units.add(UnitFactory.createUnitFromConllString(line));
					//System.err.println(line);
				}

				if(line == null){
					//reader.close(); //to be closed externally
					reader = null;
				}
				return units.isEmpty()?null:new Sentence(units);
			}

			
			
			@Override
			public boolean hasNext() {
				if(canDoReading){
					try {
						currentSentence = readSentence();  
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				canDoReading = false;
				return currentSentence != null; 
			}

			@Override
			public Sentence next() {
				if(!hasNext()){
					throw new NoSuchElementException();
				}
				 canDoReading = true;
                 return currentSentence;		
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Removal of a sentence is forbidden !!");
			}
			
		};
	}



}
