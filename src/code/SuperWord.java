package code;

import java.util.HashMap;

public class SuperWord {

	@Override
	public String toString() {
		return "SuperWord [word=" + word + ", postag=" + postag + ", features=" + features +  "]";
	}

	public String word;
	public String postag;
	public HashMap<String, Double> features;
	double[] getWordFeatureVector()
	{
		double doubArray [] = new double[TrainPOS.fSet.featureSet.size()];
		for(String key: features.keySet())
			if(TrainPOS.fSet.featureSet.containsKey(key))
				doubArray[TrainPOS.fSet.featureSet.get(key)] = features.get(key);
		return doubArray;
	}
	
	
	public void addStateAwareFeatures(String currLabel, String prevLabel)
	{
		String prevlabelWord = "prevlabelword|"+word+"|" + prevLabel;
		String currLabelWord = "currlabelword|"+word+"|" + currLabel;
		features.put(prevlabelWord, 3.0);
		features.put(currLabelWord, 3.0);
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

	
	public static boolean  validTag( String postag)
	{
		return true;
	}
	
	public static boolean validWord(String word)
	{
		return true;
	}
}
