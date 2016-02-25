package code;

import java.util.List;

public class StructuredPerceptron {
	
	public static double [] learn(List<Sentence> sentences)
	{
		double[] weight = new double[TrainPOS.fSet.featureSet.size()];
		for (int i= 0; i< weight.length; i++) {
  				weight[i] = 1.0;
  		}		

		boolean run = true;
		int q = 0;
		while (run){
			// for each training example(x, y)
			System.out.println("the " + (q+1) + " iteration");
			for(Sentence sentence: sentences){
				
				List<SuperWord> input = sentence.wordTags;
				List<SuperWord> output = Viterbi.decode(sentence, weight);
				int convergenceCounter = 0;
				int wordCounter =0;
				System.out.println();
				double learningRate = 0.1;
				for(SuperWord superWord : output)
				{
					System.out.print(superWord.postag + " ");
					if(input.get(wordCounter).word.equals(superWord.word))
						{
							if(input.get(wordCounter).postag.equals(superWord.postag))
							{ 
								weight[wordCounter]+=learningRate*4;
								++convergenceCounter;
							}
							else
							{
								weight[wordCounter]-=learningRate*4;
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
			System.out.print(weight[j] + " ");
		}
		return weight;
	}
}
