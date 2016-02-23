package code;

public class Node {
	
	public Node( double probability, String postag, Node backp) {
		super();
		this.postag = postag;
		this.backp = backp;
		this.probability = probability;
	}

	String postag;
	Node backp;
	double probability;
	
	@Override
	public String toString() {
		return "[tag=" + postag  + ", p="
				+ probability + "]";
	}
	
}
