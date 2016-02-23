package code;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.ArrayList;
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
				//return 0.0;
				return 1/(observedTagCountMatrix.get(first)+transitionMatrix.get(first).size());
		}
		else
		{
			//we never really come here during our cross validation atleast
			return 0.0;
			//return 1/(priorStateCounts.size()+1);
		}
	}

	public  double getOutputProbability(String word, String posTag)
	{
		
		if(emissionMatrix.containsKey(word))
		{
			if(emissionMatrix.get(word).containsKey(posTag))
				return (double)emissionMatrix.get(word).get(posTag) / (observedTagCountMatrix.get(posTag)+0.01);
			else 
				return (double)0.01/(observedTagCountMatrix.get(posTag)+0.01);
				//return smooth(word, posTag);
				//return 0.0; /*TODO should this need smoothing? seen word not seen with a particular pos tag*/
		}
		else
		{
			/*
			 * TODO
			 * Smoothing goes here
			 */
			//return 0.0;
			//for now return a standard very small number
			//return (double)0.01/(priorStateCounts.get(posTag)+0.01);
		
			return smooth(word, posTag);
		}
		
		
		
	}

	public double getPriorProbability(String tag)
	{
		int sum=0;
		for(String key : observedTagCountMatrix.keySet())
			sum += observedTagCountMatrix.get(key);

		return ((double) observedTagCountMatrix.get(tag) / sum);
	}

// Implement suffix-based smoothing for the given word and return P(tag | word)/
	public double smooth(String word, String tag)
	{
		int LONGEST_SUFFIX_LENGTH =4;// 4;
		double result = -1;
		// 1: Basic "small value" smoothing
		// result = (double) 1 / outputCounts.size();

		// 2: Most common tag approach
		/* if(tag.equals("NN1"))
			result = 1.0;
		else
			result = 0.0; 
		 */
		// 3: Recursive suffix-based smoothing

		// Assume biggest suffix length of 4
		int suffixLength = word.length() > LONGEST_SUFFIX_LENGTH ? LONGEST_SUFFIX_LENGTH : word.length();
		String initialSuffix = word.substring(word.length() - suffixLength);

		result = smoothRecursion(initialSuffix, tag);

		// This is P(tag | suffix). TODO: Apply bayesian inversion here.

		//System.out.println("P(" + word + " | " + tag + ") = " + result);

		return result;
		//return ( (double) 1 / outputCounts.size() );
	}

	public  double smoothRecursion(String suffix, String tag)
	{
		double theta = 0.2;	// Assume a value for now

		// Recursive case: MLE of suffix + theta * recursion over smaller suffix
		if(!suffix.equals(""))
		{
			// A: Initial MLE of P(tag | suffix)

			// double MLE = getPriorProbability(tag);	// Placeholder value
			double MLE = 0.0;
			int numerator = 0;
			int denominator = 0;

			// 1: For each key in outputCounts, see if it ends with required suffix
			for(String corpusWord : emissionMatrix.keySet())
			{
				// 2: If yes, add all its counts to denominator
				if(corpusWord.endsWith(suffix))
				{
					// Add counts to denominator here
					for(String tagKey : emissionMatrix.get(corpusWord).keySet())
						denominator += emissionMatrix.get(corpusWord).get(tagKey);

					// 3: Now add count of MATCHING TAG ONLY to numerator
					if(emissionMatrix.get(corpusWord).containsKey(tag))
						numerator += emissionMatrix.get(corpusWord).get(tag);
				}
			}
			// 5: Compute MLE = numerator/denominator
			if(denominator != 0)
				MLE = numerator / (double) denominator;
			else
				MLE = 0;

	
			// B: Make the recursive call//			
			double recursiveResult = smoothRecursion(suffix.substring(1), tag);

			return (MLE + theta * recursiveResult) / (1 + theta);
}

		// Base case: Only MLE
		else
		{
			// C: MLE for P(tag) without suffix

			double MLE = getPriorProbability(tag);
			return MLE;
		}
	}

	public String decode(String sentence)
	{
		System.out.println(sentence);
		
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
		
		
		printDpArray(trellis,xarr);
		Node traverse = trellis[x][y-1];
		
		String posSeq = "";
		while((traverse) != null)
		{
			posSeq = " " + traverse.postag + posSeq;
			traverse = traverse.backp;
		}
		return posSeq;

	}


		
}
