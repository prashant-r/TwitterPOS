package feature;

import java.util.List;

import code.SuperWord;
import feature.FeatureExtractor.PositionFeaturePairs;

public class StateAwareFeatures {

		public static void PosLabelWord(List<SuperWord> tokens, PositionFeaturePairs pairs) {
			for (int t=1; t < tokens.size(); t++) {
				if(t>=2)
					pairs.add(t, "prevlabelword|"+tokens.get(t-1).word+"|" + tokens.get(t-2).postag);
			}
	}
		public  static void CurrLabelWord(List<SuperWord> tokens, PositionFeaturePairs pairs) {
			for (int t=1; t < tokens.size(); t++) {
				if(t>=1)
					pairs.add(t, "currlabelword|"+tokens.get(t-1).word+"|" + tokens.get(t-1).postag);
			}
		}
	}
