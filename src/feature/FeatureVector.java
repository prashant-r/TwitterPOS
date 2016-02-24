package feature;

public class FeatureVector {

	String prevCurrWord;
	String ngramSuffix[];
	String ngramPrefix[];
	String prevWord;
	String nextWord; 
	String wordFormFeatures;
	String capitalizationFeatures;
	String orthFeatures;
	String positions;
	String urlFeatures;
	String prevLabel;
	
	FeatureVector()
	{
		prevCurrWord = "";
		ngramSuffix = new String[20];
		ngramPrefix = new String[20];
		prevWord = "";
		nextWord = "";
		wordFormFeatures = "";
		capitalizationFeatures = "";
		orthFeatures = "";
		positions = "";
		urlFeatures = "";
	}
	
	
}
