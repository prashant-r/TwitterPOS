package code;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
public class Viterbi {

	private static HashMap<String, HashMap<String, Integer>> emissionMatrix;
	private static HashMap<String, HashMap<String, Integer>> transitionMatrix;
	private static HashMap<String, Integer> observedTagCountMatrix;
	
	
	public Viterbi(RecordedState recordedState)
	{
		Viterbi.emissionMatrix = recordedState.emissionMatrix;
		Viterbi.transitionMatrix = recordedState.transitionMatrix;
		Viterbi.observedTagCountMatrix = recordedState.observedTagCountMatrix;
	}
	
	public static void printDpArray(Node [][] node, String [] words)
	{
		int counter = 0;
		
		for(int j =0; j<= words.length; j++)
			System.out.print("\t"+j);
		System.out.println();
		for(String wordOrTag : observedTagCountMatrix.keySet())
		{
			System.out.print(wordOrTag+"\t");
				for(int j =0; j<= words.length; j++)
						System.out.print(node[j][counter]+"\t");
		System.out.println();
		counter ++;
		}
		
	}
	
	
	public static double getTransitionProbability(String first,String second)
	{
		if(transitionMatrix.containsKey(first))
			if(transitionMatrix.get(first).containsKey(second))
				return (double)transitionMatrix.get(first).get(second) / (observedTagCountMatrix.get(first));
			else
				return 0.01/(observedTagCountMatrix.get(first)+0.01);
		return 0.0;
	}

	public  double getOutputProbability(String word, String posTag)
	{
		
		if(emissionMatrix.containsKey(word))
			if(emissionMatrix.get(word).containsKey(posTag))
				return (double)emissionMatrix.get(word).get(posTag) / (observedTagCountMatrix.get(posTag));
			else
			return 0.01/(observedTagCountMatrix.get(posTag)+0.01);
		// smooth the data for unrecognized words
		return 0.01;
	}

	public static double getPriorProbability(String tag)
	{
		int sum=0;
		for(String key : observedTagCountMatrix.keySet())
			sum += observedTagCountMatrix.get(key);

		return ((double) observedTagCountMatrix.get(tag) / sum);
	}


	public List<SuperWord> decode(String sentence)
	{
		//System.out.println(sentence);
		
		String words[] = sentence.split("\\s+");
		
		int x = words.length;
		
		String xarr[] = words;
		String yarr[] = observedTagCountMatrix.keySet().toArray(new String[ observedTagCountMatrix.keySet().size()]);
		
		int y = observedTagCountMatrix.keySet().size();
		
		Node [][] trellis = new Node[x+1][y];
		

		// Initialize the dp table 
		for(int j = 0; j< yarr.length; j++ )
			trellis[0][j] = new Node(getTransitionProbability("*", yarr[j]),"*", null);
		
	
		
		// Initialize emission probabilities
		double [][] emissionProbabilities = new double[x+1][y];
		for(int i= 1; i<= xarr.length; i++)
			for(int k = 0; k< yarr.length; k++)
				emissionProbabilities[i][k] = getOutputProbability(xarr[i-1], yarr[k]);
		
		for(int i = 1; i <= xarr.length; i ++ )
		{
			for(int j = 0; j< yarr.length; j++ )
			{
				String maxPosTag = "*";
				double maxProb = 0.0;
				Node maxNode =null;
				for(int k = 0; k<yarr.length; k++)
				{
					double value = trellis[i-1][k].probability; 
					value *= getTransitionProbability(yarr[k], yarr[j]);
					value *= emissionProbabilities[i][k];
					if(value > maxProb)
					{
						maxProb = value;
						maxPosTag = yarr[k];
						maxNode = trellis[i-1][k];
					}
				}
				trellis[i][j] = new Node(maxProb, maxPosTag, maxNode);
			}
			
		}
		// Find the best path
		
		double maxProb = 0.0;
		Node maxNode = null;
		for(int j=0; j < y; j++)
		{
			if(trellis[x][j]!=null){
				if(trellis[x][j].probability > maxProb)
				{
					double value = trellis[x][j].probability; 
					if(value > maxProb)
					{
						maxProb = value;
						maxNode = trellis[x][j];
					}
				}
			}
		}	
		Node traverse = maxNode;
		SuperWord wordtags [] = new SuperWord[words.length];
		
		while((traverse.backp) != null)
		{
			--x;
			wordtags[x] = new SuperWord(words[x], traverse.postag);
			traverse = traverse.backp;
		}
		return Arrays.asList(wordtags);

	}
	
	public static List<SuperWord> decode(Sentence sentence, double weight[])
	{
		//System.out.println(sentence);
		
		int x = sentence.wordTags.size();
		String yarr[] = observedTagCountMatrix.keySet().toArray(new String[ observedTagCountMatrix.keySet().size()]);
		
		int y = observedTagCountMatrix.keySet().size();
		
		Node [][] trellis = new Node[x+1][y];
		
		// Initialize the dp table 
		for(int j = 0; j< yarr.length; j++ )
			trellis[0][j] = new Node(getTransitionProbability("*", yarr[j]),"*", null);
		

		for(int i = 1; i <= x; i ++ )
		{
			for(int j = 0; j< yarr.length; j++ )
			{
				String maxPosTag = "*";
				double maxProb = 0.0;
				Node maxNode =null;
				for(int k = 0; k<yarr.length; k++)
				{
					double value = trellis[i-1][k].probability; 
					double score = weightedSum(sentence.wordTags.get(i-1).getWordFeatureVector(), weight );
					if(score == 0)
						score = 0.01;
					value *= score;
					System.out.println(value);
					if(value >= maxProb)
					{
						maxProb = value;
						maxPosTag = yarr[k];
						maxNode = trellis[i-1][k];
					}
				}
				trellis[i][j] = new Node(maxProb, maxPosTag, maxNode);
			}
			
		}
		// Find the best path
		
		double maxProb = 0.0;
		Node maxNode = null;
		for(int j=0; j < y; j++)
		{
			if(trellis[x][j]!=null){
				if(trellis[x][j].probability >= maxProb)
				{
					double value = trellis[x][j].probability; 
					if(value >= maxProb)
					{
						maxProb = value;
						maxNode = trellis[x][j];
					}
				}
			}
		}	
		Node traverse = maxNode;
		SuperWord wordtags [] = new SuperWord[x];
		
		
		while(--x>=0)
		{
			wordtags[x] = new SuperWord(sentence.wordTags.get(x).word, traverse.postag);
			if(traverse.backp!= null)
				traverse = traverse.backp;
		}
		
		for(SuperWord wordTag: wordtags)
		{
			if(wordTag.postag == null)
				wordTag.postag = "*";
		}
		return Arrays.asList(wordtags);

	}
	
	public static double weightedSum(double [] a ,double [] b)
	{
	    double value = 0.0d;
	    double sum = 0.0d;

	    for (int i = 0 ; i < a.length ; i++)
	    {
	        value = a[i] * b [i];
	        sum = sum +value ;

	    }

	    return sum;
	}
	

		
}
