package code;

import java.util.List;

public class StructuredPerceptron {
	
	public static double [] learn(List<Sentence> sentences)
	{
		double[] weight = new double[TrainPOS.fSet.featureSet.size()];
		for (int i= 0; i< weight.length; i++) {
  				weight[i] = 0.0;
  		}		

		boolean run = true;
		int q = 0;
		while (run){
			// for each training example(x, y)
			for(Sentence sentence: sentences){
				System.out.println("the " + (q+1) + " iteration");
				List<SuperWord> input = sentence.wordTags;
				List<SuperWord> output = Viterbi.decode(sentence, weight);
				int convergenceCounter = 0;
				int wordCounter =0;
				for(SuperWord superWord : output)
				{	
					if(input.get(wordCounter).word.equals(superWord.word))
						{
							if(!input.get(wordCounter).postag.equals(superWord.postag))
							{ 
								weight[wordCounter]+=1;
							}
							else
							{
								weight[wordCounter]-=1;
								++convergenceCounter;
							}
						}
						++wordCounter;
						
				}
				if (convergenceCounter == wordCounter)
					{
						run = false;
						break;
					}
			}
				
				q = q + 1;	
			}
		
		for (int j = 0; j < weight.length; j++) {
			System.out.println(weight[j]);
		}
		return weight;
	}
}
