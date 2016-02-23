package code;

import java.io.IOException;
import java.util.List;

public class CrossValidation {

	private static final String oraclePath = System.getProperty("user.dir")+"//data//oracle//oracle.txt";
	private static final String trainPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.train";
	private static final String testPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.test";
	private static final String devPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.dev";
	
	List<Sentence> sentences; 
	public void basicTest() throws IOException
	{
		RecordedState recordedState = Utility.readRecordedStateFromFile(oraclePath);
		Viterbi viterbi = new Viterbi(recordedState);
		int counter =0;
		int correct = 0;
		int incorrect = 0;
		int total = 0;
		for(String stringRead: readTestCorpus(testPath))
			{
			//System.out.println(stringRead);
			List<SuperWord> output = viterbi.decode(stringRead.trim());
			List<SuperWord> input = sentences.get(counter ++ ).wordTags;
			//System.out.println();
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
	
	
	public static void main(String arg[]) throws IOException
	{
		CrossValidation crossValidate = new CrossValidation();
		crossValidate.basicTest();
	}
	
	public List<String> readTestCorpus(String filename) throws IOException
	{
		sentences = Utility.readInCoNLLFormat(filename);
		List<String> sentencesAsString = Utility.sentencesAsString(sentences);
		return sentencesAsString;
	}
	
}
