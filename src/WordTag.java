
public class WordTag {

	public String word;
	public String postag;
	
	WordTag(String word, String postag)
	{
		this.postag = postag;
		this.word = word;
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
