package feature;

import java.util.HashMap;

public class FeatureSet {

	public HashMap<String, Integer> featureSet;

	public FeatureSet(HashMap<String, Integer> featureSet) {
		super();
		this.featureSet = featureSet;
	}

	public HashMap<String, Integer> getFeatureSet() {
		return featureSet;
	}

	public void setFeatureSet(HashMap<String, Integer> featureSet) {
		this.featureSet = featureSet;
	}
	
	
}
