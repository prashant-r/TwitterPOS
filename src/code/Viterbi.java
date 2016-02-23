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

	private HashMap<String, HashMap<String, Integer>> emissionMatrix;
	private HashMap<String, HashMap<String, Integer>> transitionMatrix;
	private HashMap<String, Integer> observedTagCountMatrix;
	
	
	public Viterbi(RecordedState recordedState)
	{
		this.emissionMatrix = recordedState.emissionMatrix;
		this.transitionMatrix = recordedState.transitionMatrix;
		this.observedTagCountMatrix = recordedState.observedTagCountMatrix;
	}
	
	public void printDpArray(Node [][] node, String [] words)
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
	
	
	public double getTransitionProbability(String first,String second)
	{
		if(transitionMatrix.containsKey(first))
		{
			if(transitionMatrix.get(first).containsKey(second))
				return (double)transitionMatrix.get(first).get(second) / (observedTagCountMatrix.get(first)+transitionMatrix.get(first).size());
			else 
				return 1/(observedTagCountMatrix.get(first)+transitionMatrix.get(first).size());
		}
		else
			return 0.0;
	}

	public  double getOutputProbability(String word, String posTag)
	{
		
		if(emissionMatrix.containsKey(word))
		{
			if(emissionMatrix.get(word).containsKey(posTag))
				return (double)emissionMatrix.get(word).get(posTag) / (observedTagCountMatrix.get(posTag)+0.01);
			else 
				return (double)0.01/(observedTagCountMatrix.get(posTag)+0.01);
		}
		else
			return (double)0.01/(observedTagCountMatrix.get(posTag)+0.01);
	}

	public double getPriorProbability(String tag)
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
		
		
		//printDpArray(trellis,xarr);
		Node traverse = trellis[x][y-1];
		
		SuperWord wordtags [] = new SuperWord[words.length];
		
		while((traverse.backp) != null)
		{
			--x;
			wordtags[x] = new SuperWord(words[x], traverse.postag);
			traverse = traverse.backp;
		}
		return Arrays.asList(wordtags);

	}


		
}
