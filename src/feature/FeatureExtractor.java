package feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import code.Sentence;
import code.Utility;

/*
 * DISCLAIMER: Code below is NOT MINE. Its from CMU ark-tweet-nlp POS Tagger, which itself derived some code from Twitter.
 * According to this projects licence requirements, citing CMU's project is sufficient as it implies all of its
 * citations are being respected.  
 * Location of code : https://github.com/brendano/ark-tweet-nlp
 * 
Information
===========

Version 0.3 of the tagger is much faster and more accurate.  Please see the
tech report on the website for details.

For the Java API, see src/cmu/arktweetnlp; especially Tagger.java.
See also documentation in docs/ and src/cmu/arktweetnlp/package.html.

This tagger is described in the following two papers, available at the website.
Please cite these if you write a research paper using this software.

Part-of-Speech Tagging for Twitter: Annotation, Features, and Experiments
Kevin Gimpel, Nathan Schneider, Brendan O'Connor, Dipanjan Das, Daniel Mills,
  Jacob Eisenstein, Michael Heilman, Dani Yogatama, Jeffrey Flanigan, and 
  Noah A. Smith
In Proceedings of the Annual Meeting of the Association
  for Computational Linguistics, companion volume, Portland, OR, June 2011.
http://www.ark.cs.cmu.edu/TweetNLP/gimpel+etal.acl11.pdf

Part-of-Speech Tagging for Twitter: Word Clusters and Other Advances
Olutobi Owoputi, Brendan O'Connor, Chris Dyer, Kevin Gimpel, and
  Nathan Schneider.
Technical Report, Machine Learning Department. CMU-ML-12-107. September 2012.

Contact
=======

Please contact Brendan O'Connor (brenocon@cs.cmu.edu) and Kevin Gimpel
(kgimpel@cs.cmu.edu) if you encounter any problems.
 * 
 * 
 */

/**
 * Extracts features and numberizes them
 * Also numberizes other things if necessary (e.g. label numberizations for MEMM training)
 */
public class FeatureExtractor {
	
	public ArrayList<FeatureExtractorInterface> allFeatureExtractors;
	
	
	FeatureExtractor() throws IOException
	{
		initializeFeatureExtractors();
	}
	
	public interface FeatureExtractorInterface {
		/**
		 * Input: sentence
		 * Output: labelIndexes, featureIDs/Values through positionFeaturePairs
		 *
		 * We want to yield a sequence of (t, featID, featValue) pairs,
		 * to be conjuncted against label IDs at position t.
		 * Represent as parallel arrays.  Ick yes, but we want to save object allocations (is this crazy?)
		 * This method should append to them.
		 */
		public void addFeatures(List<String> tokens, PositionFeaturePairs positionFeaturePairs);
	}

	public static void getFeatures(Sentence sentence) throws IOException
	{
		List<String> strings = Utility.sentenceAsListString(sentence);
		
		PositionFeaturePairs posFairs = new PositionFeaturePairs(); 
		FeatureExtractor featureExtractor = new FeatureExtractor();
		for(FeatureExtractorInterface fint : featureExtractor.allFeatureExtractors)
			fint.addFeatures(strings, posFairs);
		for (int i=0; i < posFairs.size(); i++) {
			int t = posFairs.labelIndexes.get(i);
			String fName = posFairs.featureNames.get(i);
			double fValue = posFairs.featureValues.get(i);
			sentence.wordTags.get(t).features.put(fName,fValue);
		}
	}
	
	public static void main(String args[]) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter your text");
		String input = br.readLine();
		List<String> spilt = new ArrayList<String>();
		spilt.add(input);
		List<Sentence> sentences = new ArrayList<Sentence>();
		Utility.addSentence(sentences,spilt );
		
	}
	
	public static class PositionFeaturePairs {
		public ArrayList<Integer> labelIndexes;
		public ArrayList<String> featureNames;
		public ArrayList<Double> featureValues;

		public PositionFeaturePairs() {
			labelIndexes = new ArrayList<Integer>();
			featureNames = new ArrayList<String>();
			featureValues = new ArrayList<Double>();
		}
		public void add(int labelIndex, String featureID) {
			add(labelIndex, featureID, 1.0);
		}
		public void add(int labelIndex, String featureID, double featureValue) {
			labelIndexes.add(labelIndex);
			featureNames.add(featureID);
			featureValues.add(featureValue);
		}
		public int size() { return featureNames.size(); }
	}


	///////////////////////////////////////////////////////////////////////////
	//
	// Actual feature extractors



	private void initializeFeatureExtractors() throws IOException {
		allFeatureExtractors = new ArrayList<FeatureExtractorInterface>();
		allFeatureExtractors.add(new MiscFeatures.NgramSuffix(20));
		allFeatureExtractors.add(new MiscFeatures.NgramPrefix(20));
		allFeatureExtractors.add(new MiscFeatures.PrevWord());
		allFeatureExtractors.add(new MiscFeatures.NextWord());
		allFeatureExtractors.add(new MiscFeatures.WordformFeatures());
		allFeatureExtractors.add(new MiscFeatures.CapitalizationFeatures());
		allFeatureExtractors.add(new MiscFeatures.SimpleOrthFeatures());
		allFeatureExtractors.add(new MiscFeatures.PrevNext());
		allFeatureExtractors.add(new MiscFeatures.Positions());
		allFeatureExtractors.add(new MiscFeatures.URLFeatures());

	}


	// for performance, figuring out a numberization approach faster than string concatenation might help
	// internet suggests that String.format() is slower than string concat
	// maybe can reuse a StringBuilder object? Ideally, would do direct manipulation of a char[] with reuse.
	// Or, if we move to randomized feature hashing, there are far faster methods
	// e.g. http://www.hpl.hp.com/techreports/2008/HPL-2008-91R1.pdf
}