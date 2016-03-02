package code;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import feature.FeatureExtractor;

public class MEMM {

	static double w[] = new double[TrainPOS.fSet.featureSet.size()];
	
	public static void learn(List<Sentence> sentences)
	{
	
		
		int ITERATION = 5;
		double lambda = 0.5;
		
		for (int iter = 0; iter < ITERATION; iter++) {
			System.out.println("Learning MEMM iteration number " + iter);
			
			for (Sentence sentence : sentences) {
				String prevTagID = "*";
				for (SuperWord superWord: sentence.wordTags) {
					// System.out.println("WORD *******" + superWord.word);
					double wordFeatures [] = superWord.addStateAwareFeatures(superWord.postag, prevTagID);
					addToWordVector(w, wordFeatures, lambda);
					for(String key: Viterbi.observedTagCountMatrix.keySet())
					{
						addToWordVector(w, wordFeatures, -1*lambda*probOfTag(superWord, superWord.postag, key));
						//System.out.println(probOfTag(superWord,superWord.postag, key));
					}
					prevTagID = superWord.postag;
				}
				
			}
			//Utility.printDoubArray(w);
		}
		
	}
	
	
	public static void addToWordVector(double elements [], double add [], double lambda) {
		
		for (int i = 0; i < add.length; i++) {
			elements[i]+= add[i] *lambda;
		}
	}
		
	
	public static double probOfTag(SuperWord superWord, String currTag, String prevTag) {
	
		//System.out.println(superWord.word + " " + currTag + " " + prevTag);
		double x[] = superWord.addStateAwareFeatures(currTag, prevTag);
	//	Utility.printDoubArray(x);
		double numerator = Math.exp(weightedSum(w,x));
		//System.out.println(" curr tag" + currTag + " prevTag " + prevTag + " weighted sum " + weightedSum(w,x));
		double denominator = 0;
		
		for (String key :Viterbi.observedTagCountMatrix.keySet()) {
			x = superWord.addStateAwareFeatures(currTag, key);
			
			denominator += Math.exp((weightedSum(w,x)));
			//System.out.println(" curr tag" + currTag + " prevTag " + key + " weighted sum " + weightedSum(w,x));
			
		}
		//System.out.println("numerator " + numerator);
		//System.out.println("denominator " + denominator);
		return numerator / denominator;
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
	
	public static List<SuperWord>  decode(String sentence)
	{
		
		//System.out.println(sentence);
		
				String words[] = sentence.split("\\s+");
				
				int x = words.length;
				
				String xarr[] = words;
				String yarr[] = Viterbi.observedTagCountMatrix.keySet().toArray(new String[ Viterbi.observedTagCountMatrix.keySet().size()]);
				
				int y = Viterbi.observedTagCountMatrix.keySet().size();
				
				Node [][] trellis = new Node[x+1][y];
				

				// Initialize the dp table 
				for(int j = 0; j< yarr.length; j++ )
					trellis[0][j] = new Node(Viterbi.getTransitionProbability("*", yarr[j]),"*", null);
				
			
				
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
							SuperWord superWord = new SuperWord(words[i-1],null);
							try {
								FeatureExtractor.setFeatures(superWord);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							value = value * probOfTag(superWord, yarr[k], yarr[j]);
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
				
				Utility.printTrellis(trellis, x+1, y);
				// Find the best path
				
				double maxProb = 0.0;
				Node maxNode = null;
				for(int j=0; j < y; j++)
				{
					if(trellis[x][j]!=null){
						if(trellis[x][j].probability > maxProb)
						{
							double value = trellis[x][j].probability; 
							maxProb = value;
							maxNode = trellis[x][j];
						}
					}
				}	
				Node traverse = maxNode;
				SuperWord wordtags [] = new SuperWord[words.length];
				
				while((traverse.backp) != null)
				{
					--x;
					System.out.println(traverse.postag);
					wordtags[x] = new SuperWord(words[x], traverse.postag);
					traverse = traverse.backp;
				}
				
				return Arrays.asList(wordtags);

	}
	
	
}