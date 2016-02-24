package feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * String normalizations and other shared utilities for feature computation. 
 **/
public class FeatureUtil {
	
	public static Pattern URL = Pattern.compile(Twokenize.OR(Twokenize.url, Twokenize.Email));
	public static Pattern justbase = Pattern.compile("(?!www\\.|ww\\.|w\\.|@)[a-zA-Z0-9]+\\.[A-Za-z0-9\\.]+"); 

//	Pattern URL = Pattern.compile(Twokenize.url);

	
	public static ArrayList<String> normalize(List<String> toks){
		ArrayList<String> normtoks = new ArrayList<String>(toks.size());
		for (String s:toks){
			normtoks.add(FeatureUtil.normalize(s));
		}
		return normtoks;
	}

	/**
	 * @param str
	 * @return Lowercase normalized string
	 */
	public static String normalize(String str) {
	    str = str.toLowerCase();
		if (URL.matcher(str).matches()){	    	
	    	String base = "";
	    	Matcher m = justbase.matcher(str);
	    	if (m.find())
	    		base=m.group().toLowerCase();
	    	return "<URL-"+base+">";
	    }
	    if (Regex.VALID_MENTION_OR_LIST.matcher(str).matches())
	    	return "<@MENTION>";
	    return str;
	}

	public static ArrayList<String> normalizecap(List<String> toks){
		ArrayList<String> normtoks = new ArrayList<String>(toks.size());
		for (String s:toks){
			normtoks.add(FeatureUtil.normalizecap(s));
		}
		return normtoks;
	}

	//same as normalize but retains capitalization
	public static String normalizecap(String str) {
		if (URL.matcher(str).matches()){	    	
	    	String base = "";
	    	Matcher m = justbase.matcher(str);
	    	if (m.find())
	    		base=m.group().toLowerCase();
	    	return "<URL-"+base+">";
	    }
	    if (Regex.VALID_MENTION_OR_LIST.matcher(str).matches())
	    	return "<@MENTION>";
	    return str;
	}
	
	
	static Pattern repeatchar = Pattern.compile("([\\w])\\1{1,}");
	static Pattern repeatvowel = Pattern.compile("(a|e|i|o|u)\\1+");
	
	public static ArrayList<String> fuzztoken(String tok, boolean apos) {
	    ArrayList<String> fuzz = new ArrayList<String>();
	    fuzz.add(tok.replaceAll("[‘’´`]", "'").replaceAll("[“”]", "\""));
	    fuzz.add(tok);
	    fuzz.add(repeatchar.matcher(tok).replaceAll("$1"));//omggggggg->omg
	    fuzz.add(repeatchar.matcher(tok).replaceAll("$1$1"));//omggggggg->omgg
	    fuzz.add(repeatvowel.matcher(tok).replaceAll("$1"));//heeellloooo->helllo
	    if (apos && !(tok.startsWith("<URL"))){
	    	fuzz.add(tok.replaceAll("\\p{Punct}", ""));//t-swift->tswift
	    	//maybe a bad idea (bello's->bello, re-enable->re, croplife's->'s)
	    	fuzz.addAll(Arrays.asList(tok.split("\\p{Punct}")));
	    }
	    return fuzz;
	}

}

