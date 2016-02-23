package code;

public class Node {
	
	String backpointer;
	double probability;
	public Node(double probability, String backpointer) {
		super();
		this.backpointer = backpointer;
		this.probability = probability;
	}
	public String getBackpointer() {
		return backpointer;
	}
	public void setBackpointer(String backpointer) {
		this.backpointer = backpointer;
	}
	public double getProbability() {
		return probability;
	}
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
}
