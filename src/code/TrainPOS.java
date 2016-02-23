package code;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TrainPOS {

	private static final String trainPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.train";
	private static final String oraclePath = System.getProperty("user.dir")+"/data/oracle/oracle.txt";
	HashMap<String, HashMap<String, Integer>> emissionMatrix;
	HashMap<String, HashMap<String, Integer>> transitionMatrix;
	HashMap<String, Integer> observedTagCountMatrix;
	public void readCorpus(String filename) throws IOException
	{
		List<Sentence> sentences = Utility.readInCoNLLFormat(filename);
		for(Sentence sentence: sentences)
		{	
			String previousTag = "*";
			for(WordTag wordTag: sentence.wordTags)
			{
				updateTagCountMatrix(previousTag);
				updateTransitionMatrix(previousTag, wordTag.postag);
				updateEmissionMatrix(wordTag);
				previousTag = wordTag.postag;
			}
		}
		
	}
	public void updateEmissionMatrix(WordTag currWordTag)
	{
		if(emissionMatrix.containsKey(currWordTag.word))
		{
			HashMap<String, Integer> emissionEntryTagCount = emissionMatrix.get(currWordTag.word);
			if(emissionEntryTagCount.containsKey(currWordTag.postag))
				emissionEntryTagCount.put(currWordTag.postag, (emissionEntryTagCount.get(currWordTag.postag)+1));
			else
				emissionEntryTagCount.put(currWordTag.postag, 1);
		}
		else
		{
			HashMap<String, Integer> hash = new HashMap<String, Integer>();
			hash.put(currWordTag.postag, 1);
			emissionMatrix.put(currWordTag.word, hash);
		}
	}
	
	public void updateTransitionMatrix(String previousTag, String currentTag)
	{
		if(transitionMatrix.containsKey(previousTag)) 
		{
			HashMap<String, Integer> transmissionEntryTagCount = transitionMatrix.get(previousTag); 
			if(transmissionEntryTagCount.containsKey(currentTag)) 
				transmissionEntryTagCount.put(currentTag, (transmissionEntryTagCount.get(currentTag)+1));
			else
				transmissionEntryTagCount.put(currentTag, 1);
			transitionMatrix.put(previousTag, transmissionEntryTagCount);
		}
		else 
		{
			HashMap<String, Integer> hash = new HashMap<String, Integer>();
			hash.put(currentTag, 1);
			transitionMatrix.put(previousTag, hash);
		}
	}
	
	public void updateTagCountMatrix(String word)
	{
		if(observedTagCountMatrix.containsKey(word))
		{
			observedTagCountMatrix.put(word, observedTagCountMatrix.get(word) + 1);
		}
		else
			observedTagCountMatrix.put(word, 1);
	}
	public void recordState(String filename) throws IOException
	{
		RecordedState recordedState = new RecordedState(emissionMatrix, transitionMatrix,observedTagCountMatrix);
		recordedState.printRecordedState();
		Utility.writeRecordedStateToFile(recordedState, oraclePath);
	}
	
	TrainPOS()
	{
		transitionMatrix = new HashMap<String, HashMap<String,Integer>>();
		observedTagCountMatrix = new HashMap<String, Integer>();
		emissionMatrix = new HashMap<String, HashMap<String,Integer>>();	
	}
	
	
	public static void main(String args[]) throws IOException
	{
		TrainPOS trainPOS = new TrainPOS();
		trainPOS.readCorpus(trainPath);
		trainPOS.recordState(oraclePath);
	}
}
