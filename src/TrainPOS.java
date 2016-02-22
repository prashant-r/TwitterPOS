import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TrainPOS {

	private static String trainPath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.train";
	private static String oraclePath = System.getProperty("user.dir")+"/data/oct27.splits/oct27.train";
	
	

	HashMap<String, HashMap<String, Integer>> emissionMatrix;
	HashMap<String, HashMap<String, Integer>> transitionMatrix;
	HashMap<String, Integer> observedCountMatrix;
	
	public void readCorpus(String filename) throws IOException
	{
		List<Sentence> sentences = Utility.readInCoNLLFormat(filename);
		for(Sentence sentence: sentences)
		{
			for(WordTag wordTag: sentence.wordTags)
			{
				
			}
		}
		
	}
	
	public void updateEmissionMatrix()
	{
		
	}
	
	public void updateTransitionMatrix()
	{
		
	}
	
	public void updateObservedCountMatrix()
	{
		
	}
	public void recordState(String filename)
	{
		
		
	}
	
	TrainPOS()
	{
		transitionMatrix = new HashMap<String, HashMap<String,Integer>>();
		observedCountMatrix = new HashMap<String, Integer>();
		emissionMatrix = new HashMap<String, HashMap<String,Integer>>();	
	}
	
	public static void main(String args[]) throws IOException
	{
		TrainPOS trainPOS = new TrainPOS();
		trainPOS.readCorpus(trainPath);
		trainPOS.recordState(oraclePath);
	
	}
}
