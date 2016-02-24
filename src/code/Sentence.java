package code;
import java.util.List;

public class Sentence {
	
	public List<SuperWord> wordTags;
	
	public Sentence(List<SuperWord> wordTags)
	{
		this.wordTags = wordTags;
	}

	@Override
	public String toString() {
		return "Sentence [wordTags=" + wordTags + "]";
	}
	
}
