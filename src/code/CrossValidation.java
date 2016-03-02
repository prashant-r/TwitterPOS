package code;

import java.io.IOException;
import java.util.List;

public class CrossValidation {

	private static final String oraclePath = System.getProperty("user.dir")+"//data//oracle//oracle.txt";
	private static final String trainPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.train";
	private static final String testPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.test";
	private static final String devPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.dev";
	
	static List<Sentence> sentences; 
	public void basicTestViterbi() throws IOException
	{
		RecordedState recordedState = Utility.readRecordedStateFromFile(oraclePath);
		Viterbi viterbi = new Viterbi(recordedState);
		int counter =0;
		int correct = 0;
		int incorrect = 0;
		int total = 0;
		for(String stringRead: readTestCorpus(testPath))
			{
			List<SuperWord> output = viterbi.decode(stringRead.trim());
			List<SuperWord> input = sentences.get(counter ++ ).wordTags;
			int wordCounter =0;
			for (SuperWord wordTag : output){ 
				total ++ ;
				if(input.get(wordCounter).word.equals(wordTag.word))
				{
					if(input.get(wordCounter).postag.equals(wordTag.postag))
					{
						correct ++;
					}
					else
						incorrect ++ ;
					wordCounter ++;
				}
				else
				{
					System.exit(-1);
				}
			}
			}
		
		System.out.println(" Print Statistics ");
		System.out.println(" Total instances " + total + " Correct " + correct + " incorrect " + incorrect + " Accuracy " + (double)correct/total);
	}
	
	
	public static void basicTestMEMM() throws IOException
	{
		RecordedState recordedState = Utility.readRecordedStateFromFile(oraclePath);
		int counter =0;
		int correct = 0;
		int incorrect = 0;
		int total = 0;
		for(String stringRead: readTestCorpus(testPath))
			{
			List<SuperWord> output = MEMM.decode(stringRead.trim());
			List<SuperWord> input = sentences.get(counter ++ ).wordTags;
			int wordCounter =0;
			for (SuperWord wordTag : output){ 
				total ++ ;
				if(input.get(wordCounter).word.equals(wordTag.word))
				{
					if(input.get(wordCounter).postag.equals(wordTag.postag))
					{
						correct ++;
					}
					else
						incorrect ++ ;
					wordCounter ++;
				}
				else
				{
					System.exit(-1);
				}
			}
			}
		
		System.out.println(" Print Statistics ");
		System.out.println(" Total instances " + total + " Correct " + correct + " incorrect " + incorrect + " Accuracy " + (double)correct/total);	}
	
	
	
	
	public static void main(String arg[]) throws IOException
	{
		CrossValidation crossValidate = new CrossValidation();
		crossValidate.basicTestViterbi();
		//crossValidate.basicTestMEMM();
	}
	
	public static List<String> readTestCorpus(String filename) throws IOException
	{
		sentences = Utility.readInCoNLLFormat(filename);
		List<String> sentencesAsString = Utility.sentencesAsString(sentences);
		return sentencesAsString;
	}
	
}
