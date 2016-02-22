import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
		List<WordTag> wordTags = new ArrayList<WordTag>();
		Sentence sentence = new Sentence(wordTags);
		for(String lineInSentence:linesInSentence)
		{
			String[] parts = lineInSentence.split("\t");

			if(parts.length == 2){

				if(WordTag.validTag(parts[1]) && WordTag.validWord(parts[0]))
				{
					WordTag wordTag = new WordTag(parts[0].trim(), parts[1].trim());
					
					wordTags.add(wordTag);
				}
			}
		}
		sentences.add(sentence);
	}
	
}
