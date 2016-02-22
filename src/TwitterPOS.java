import java.io.IOException;
import java.util.List;

public class TwitterPOS {
	
	public static List<Sentence> sentences;
	private static String pathNameTest = System.getProperty("user.dir")+"/data/oct27.splits/oct27.train";
	public static void main(String args[]) throws IOException
	{
		sentences = Utility.readInCoNLLFormat(pathNameTest);
		System.out.println(pathNameTest);
	}
}
