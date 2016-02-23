package code;

import java.util.List;

public class SuperWord {

	public String word;
	public String postag;
	public List<String> features;
	public String guessTag;
	public String previousTag;
	
	SuperWord(String word, String postag)
	{
		this.postag = postag;
		this.word = word;
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

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
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
