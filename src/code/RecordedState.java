package code;
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
	
	void printRecordedState()
	{
		System.out.println("-------------------------");
		System.out.println("Displaying Recorded State");
		System.out.println("-------------------------");
		System.out.println("Transition Matrix");
		printTransitionMatrix();
//		System.out.println("-------------------------");
//		System.out.println("Emission Matrix");
//		printEmissionMatrix();
		System.out.println("-------------------------");
		System.out.println("Observed Tag Count Matrix");
		printObservedCountMatrix();
		System.out.println("-------------------------");
	}
	
	
	public void printTransitionMatrix()
	{
		Utility.printHashOHashMap(transitionMatrix);
	}
	
	public void printEmissionMatrix()
	{
		Utility.printHashOHashMap(emissionMatrix);
	}
	
	public void printObservedCountMatrix()
	{
		Utility.printHashMapA(observedTagCountMatrix);
	}
	
	
}
