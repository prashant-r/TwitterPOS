package code;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class TwitterPOS {
	
	public static List<Sentence> sentences;
	private static final String oraclePath = System.getProperty("user.dir")+"//data//oracle//oracle.txt";
	public static void main(String args[]) throws IOException
	{
		String textToTag;
		if(args.length ==0)
		{	
			System.out.println("Enter a sentence to generate POS tags");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			textToTag = br.readLine();
		}
		else
		{
			StringBuilder strBuilder = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
			   strBuilder.append(args[i]);
			}
			textToTag = strBuilder.toString();
		}
		
		System.out.println(oraclePath);
		RecordedState recordedState = Utility.readRecordedStateFromFile(oraclePath);
		Viterbi viterbi = new Viterbi(recordedState);
		
		String tagMapping = viterbi.decode(textToTag);
		System.out.println(" Tagged as " + tagMapping);
	}
}
