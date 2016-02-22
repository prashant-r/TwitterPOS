import java.io.Serializable;
import java.util.HashMap;

public class RecordedState implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<String, HashMap<String, Integer>> emissionMatrix;
	HashMap<String, HashMap<String, Integer>> transitionMatrix;
	HashMap<String, Integer> observedTagCountMatrix;
	
	public RecordedState(HashMap<String, HashMap<String, Integer>> emissionMatrix,
	HashMap<String, HashMap<String, Integer>> transitionMatrix,
	HashMap<String, Integer> observedTagCountMatrix)
	{
		this.emissionMatrix = emissionMatrix;
		this.transitionMatrix = transitionMatrix;
		this.observedTagCountMatrix = observedTagCountMatrix;
	}
	
}
