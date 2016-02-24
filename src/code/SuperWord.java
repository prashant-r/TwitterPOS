package code;

import java.util.HashMap;

public class SuperWord {

	@Override
	public String toString() {
		return "SuperWord [word=" + word + ", postag=" + postag + ", features=" + features + ", guessTag=" + guessTag
				+ ", previousTag=" + previousTag + "]";
	}

	public String word;
	public String postag;
	public HashMap<String, Double> features;
	public String guessTag;
	public String previousTag;
	
	
	double[] getWordFeatureVector()
	{
		double doubArray [] = new double[TrainPOS.fSet.featureSet.size()];
		for(String key: features.keySet())
			if(TrainPOS.fSet.featureSet.containsKey(key))
				doubArray[TrainPOS.fSet.featureSet.get(key)] = features.get(key);
		return doubArray;
	}
	
	
	SuperWord(String word, String postag)
	{
		this.postag = postag;
		this.word = word;
		features = new HashMap<String, Double>();
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPostag() {
		return postag;
	}

	public void setPostag(String postag) {
		this.postag = postag;
	}

	public String getGuessTag() {
		return guessTag;
	}

	public void setGuessTag(String guessTag) {
		this.guessTag = guessTag;
	}

	public String getPreviousTag() {
		return previousTag;
	}

	public void setPreviousTag(String previousTag) {
		this.previousTag = previousTag;
	}

	
	public static boolean  validTag( String postag)
	{
		return true;
	}
	
	public static boolean validWord(String word)
	{
		return true;
	}
}
