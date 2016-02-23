package code;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Utility {
	private static Logger log = Logger.getLogger(Utility.class.getCanonicalName());

	public static BufferedReader readFile(String fileLoc) {
		try {
			BufferedReader buffReader = new BufferedReader(new FileReader(fileLoc));

			return buffReader;
		} catch (IOException e) {
			e.printStackTrace();
			log.severe("Attempt to open file at :" + fileLoc + " failed with message " + e.getMessage());
			System.exit(-1);
		}
		return null;
	}

	public static BufferedWriter writeFile(String fileLoc) {
		try {
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(fileLoc));

			return buffWriter;
		} catch (IOException e) {
			e.printStackTrace();
			log.severe("Attempt to write file at :" + fileLoc + " failed with message " + e.getMessage());
			System.exit(-1);
		}
		return null;
	}
	
	public static void writeRecordedStateToFile(RecordedState recordedState, String file) throws IOException
	{
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try{
		    fout = new FileOutputStream(file, false);
		    oos = new ObjectOutputStream(fout);
		    oos.writeObject(recordedState);
		} catch (Exception ex) {
		    ex.printStackTrace();
		} finally {
		    if(oos  != null){
		        oos.close();
		    } 
		}
	}
	
	public static RecordedState readRecordedStateFromFile(String file) throws IOException
	{
		ObjectInputStream objectinputstream = null;
		FileInputStream streamIn = null;
		RecordedState readState = null;
		try {
		    streamIn = new FileInputStream(file);
		    objectinputstream = new ObjectInputStream(streamIn);
		    readState = (RecordedState) objectinputstream.readObject();
		    
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    if(objectinputstream != null){
		        objectinputstream .close();
		        
		    } 
		}
		return readState;
	}
	
	
	
	
	public static void closeReadFile(BufferedReader br)
	{
		try{
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
			log.severe("Attempt to close file failed with message " + e.getMessage());
			System.exit(-1);
		}
	}

	public static void closeWrittenFile(BufferedWriter bw)
	{
		try{
			bw.close();
		}
		catch(Exception e){
			e.printStackTrace();
			log.severe("Attempt to close file failed with message " + e.getMessage());
			System.exit(-1);
		}
	}


	public static List<Sentence> readInCoNLLFormat(String file) throws IOException
	{
		List<Sentence> sentences = new ArrayList<Sentence>();
		BufferedReader br = readFile(file);
		String temp;
		List<String> linesInSentence = new ArrayList<String>();
		while ( (temp = br.readLine()) != null ) {
			if (temp.matches("^\\s*$")) {
				if (linesInSentence.size() > 0) {
					addSentence(sentences, linesInSentence);
					linesInSentence.clear();
				}
			}
			else {
				linesInSentence.add(temp);
			}
		}
		if (linesInSentence.size() > 0) {
			addSentence(sentences, linesInSentence);
		}
		closeReadFile(br);
		return sentences;
	}
	
	public static void addSentence(List<Sentence> sentences, List<String> linesInSentence)
	{
		List<SuperWord> wordTags = new ArrayList<SuperWord>();
		Sentence sentence = new Sentence(wordTags);
		for(String lineInSentence:linesInSentence)
		{
			String[] parts = lineInSentence.split("\t");

			if(parts.length == 2){

				if(SuperWord.validTag(parts[1]) && SuperWord.validWord(parts[0]))
				{
					SuperWord wordTag = new SuperWord(parts[0].trim(), parts[1].trim());
					
					wordTags.add(wordTag);
				}
			}
		}
		sentences.add(sentence);
	}
	
	public static List<String> sentencesAsString(List<Sentence> sentences)
	{
		List<String> sentenceStrings = new ArrayList<String>();
		
		for(Sentence sentence: sentences)
		{
			String sent = "";
			for(SuperWord wordTag: sentence.wordTags)
				sent = sent + " " + wordTag.word;
			sent.trim();
			sentenceStrings.add(sent);
		}
		return sentenceStrings;		
	}
	
	public static void printHashOHashMap(HashMap<String, HashMap<String, Integer>> twoDMatrix)
	{
		
		for(String wordOrTag:twoDMatrix.keySet())
			System.out.print("\t"+wordOrTag);
		System.out.println();
		for(String wordOrTag : twoDMatrix.keySet())
		{
			System.out.print(wordOrTag+"\t");
				for(String tag : twoDMatrix.keySet())
				{
					if(twoDMatrix.get(wordOrTag).containsKey(tag))
						System.out.print(twoDMatrix.get(wordOrTag).get(tag)+"\t");
					else
						System.out.print("0"+"\t");
				}
		System.out.println();
		}
		
	}
	
	public static void printHashMap(HashMap<String, Integer> OneDMatrix)
	{
		for(String tag : OneDMatrix.keySet())
				System.out.print( tag + " : "  + OneDMatrix.get(tag)+"\t");
	}
	

}
