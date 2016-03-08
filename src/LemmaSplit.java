import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;


/* 
 * LemmaSplit.java
 * =======================
 * This class uses StanfordNLP to lemmatize documents.
 * 
 * Author(s): Jacqueline Wong, Kevin Nguyen
 * Date: 3/5/2016
 * 
 * Assignment 3
 * Class: CS 6301.502 - Software Analysis and Comprehension
 */

public class LemmaSplit {
	

	private File[] documents;
	private Preprocessor preprocessor;
	
	public ArrayList<Integer> sentenceCount;
	public ArrayList<Integer> termCount;
	public ArrayList<Integer> nounCount;
	public ArrayList<Integer> verbCount;
	public ArrayList<Integer> adjectiveCount;
	public ArrayList<Integer> adverbCount;
	
	public ArrayList<String> nouns;
	public ArrayList<String> verbs;
	public ArrayList<String> adjectives;
	public ArrayList<String> adverbs;
	
	public ArrayList<Integer> nounWordCount;
	public ArrayList<Integer> verbWordCount;
	public ArrayList<Integer> adjectiveWordCount;
	public ArrayList<Integer> adverbWordCount;
	
	public ArrayList<WordCount> nounList;
	public ArrayList<WordCount> verbList;
	public ArrayList<WordCount> adjectiveList;
	public ArrayList<WordCount> adverbList;
	
	/**
	 * ==========Constructors===========
	 */	
	public LemmaSplit() throws IOException {
		preprocessor = new Preprocessor();
		sentenceCount = new ArrayList<Integer>();
		termCount = new ArrayList<Integer>();
		nounCount = new ArrayList<Integer>();
		verbCount = new ArrayList<Integer>();
		adjectiveCount = new ArrayList<Integer>();
		adverbCount = new ArrayList<Integer>();
		
		nouns = new ArrayList<String>();
		verbs = new ArrayList<String>();
		adjectives = new ArrayList<String>();
		adverbs = new ArrayList<String>();
		
		nounWordCount = new ArrayList<Integer>();
		verbWordCount = new ArrayList<Integer>();
		adjectiveWordCount = new ArrayList<Integer>();
		adverbWordCount = new ArrayList<Integer>();
		
		nounList = new ArrayList<WordCount>();
		verbList = new ArrayList<WordCount>();
		adjectiveList = new ArrayList<WordCount>();
		adverbList = new ArrayList<WordCount>();
	}
	
	/**
	 * This method preprocesses the input before usage (Ex. Adding to corpus, querying the corpus)
	 * 
	 * @param input
	 * @return processedInput
	 */
	protected void preprocess(String input) throws Exception {
		
		try {
			String processedInput = input;
			processedInput = preprocessor.removeJIRANotation(processedInput);
			processedInput = preprocessor.removeJavaReservedWords(processedInput);
			processedInput = preprocessor.removeNumbers(processedInput);
			processedInput = preprocessor.removeSpecialStringCharacters(processedInput);
			processedInput = preprocessor.removeProgrammingTokens(processedInput);
			sentenceCount.add(getNumSentences(processedInput));
			processedInput = preprocessor.removePunctuation(processedInput);
			processedInput = preprocessor.removeTokensWithSpecialCharacters(processedInput);
			processedInput = preprocessor.removeJavaCommonClasses(processedInput);
			processedInput = preprocessor.removeStopWords(processedInput);
			processedInput = preprocessor.removeWordsLessThanCharMinimum(processedInput, 2);
			processedInput = preprocessor.splitCamelCase(processedInput);
			processedInput = preprocessor.lemmatize(processedInput);
			MaxentTagger tagger =  new MaxentTagger("lib/tagger/taggers/english-left3words-distsim.tagger");
			String tagged = tagger.tagString(processedInput);
			
			countPOS(tagged);
			//getNumTerms(processedInput);
		}
		catch (Exception e) {
			System.out.println("Preprocess exception");
		}
	}
	
	protected int getNumSentences(String document) {
		int counter = 0;
		Reader reader = new StringReader(document);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();
		for (List<HasWord> sentence : dp) {
			   String sentenceString = Sentence.listToString(sentence);
			   sentenceList.add(sentenceString.toString());
			   counter++;
			}
			/*
			for (String sentence : sentenceList) {
			   System.out.println(sentence);
				counter++;
			}
			*/
		return counter;
	}
	
	protected void countPOS(String document) {
		int nounCounter = 0;
		int verbCounter = 0;
		int adjectiveCounter = 0;
		int adverbCounter = 0;
		int totalTerms = 0;
		
		Scanner in = new Scanner(document);
		while(in.hasNext()) {
			String word = in.next();
			totalTerms++;
			//includes pronouns
			if(word.contains("NN") || word.contains("PRP") || word.contains("WP")) {
				nounCounter++;
				
				if (nouns.indexOf(word) < 0) {
					nouns.add(word);
					nounWordCount.add(1);
				}
				else {
					int i = nounWordCount.get(nouns.indexOf(word));
					i++;
					nounWordCount.set(nouns.indexOf(word), i);
				}
			} else if (word.contains("JJ")) {
				adjectiveCounter++;
				
				if (adjectives.indexOf(word) < 0) {
					adjectives.add(word);
					adjectiveWordCount.add(1);
				}
				else {
					int i = adjectiveWordCount.get(adjectives.indexOf(word));
					i++;
					adjectiveWordCount.set(adjectives.indexOf(word), i);
				}
			} else if (word.contains("VB")) {
				verbCounter++;
				
				if (verbs.indexOf(word) < 0) {
					verbs.add(word);
					verbWordCount.add(1);
				}
				else {
					int i = verbWordCount.get(verbs.indexOf(word));
					i++;
					verbWordCount.set(verbs.indexOf(word), i);
				}
			} else if (word.contains("RB")) {
				adverbCounter++;
			
				if (adverbs.indexOf(word) < 0) {
					adverbs.add(word);
					adverbWordCount.add(1);
				}
				else {
					int i = adverbWordCount.get(adverbs.indexOf(word));
					i++;
					adverbWordCount.set(adverbs.indexOf(word), i);
				}
			} else {}
		}
		in.close();
		nounCount.add(nounCounter);
		adjectiveCount.add(adjectiveCounter);
		verbCount.add(verbCounter);
		adverbCount.add(adverbCounter);
		termCount.add(totalTerms);
		//return counter;
	}
	
	protected int getNumAdjectives(String document) {
		int counter = 0;
		MaxentTagger tagger =  new MaxentTagger("lib/tagger/taggers/english-left3words-distsim.tagger");
		String tagged = tagger.tagString(document);
		Scanner in = new Scanner(tagged);
		while(in.hasNext()) {
			String word = in.next();
			//doesn't count adverbs
			if(word.contains("JJ")) {
				counter++;
				
				if (adjectives.indexOf(word) < 0) {
					adjectives.add(word);
					adjectiveWordCount.add(1);
				}
				else {
					int i = adjectiveWordCount.get(adjectives.indexOf(word));
					i++;
					adjectiveWordCount.set(adjectives.indexOf(word), i);
				}
			}
		}
		in.close();
		return counter;
	}
	
	protected int getNumVerbs(String document) {
		int counter = 0;
		MaxentTagger tagger =  new MaxentTagger("lib/tagger/taggers/english-left3words-distsim.tagger");
		String tagged = tagger.tagString(document);
		Scanner in = new Scanner(tagged);
		while(in.hasNext()) {
			String word = in.next();
			//doesn't count adverbs
			if(word.contains("VB")) {
				counter++;
				
				if (verbs.indexOf(word) < 0) {
					verbs.add(word);
					verbWordCount.add(1);
				}
				else {
					int i = verbWordCount.get(verbs.indexOf(word));
					i++;
					verbWordCount.set(verbs.indexOf(word), i);
				}
			}
		}
		in.close();
		return counter;
	}
	
	protected int getNumAdverbs(String document) {
		int counter = 0;

		Scanner in = new Scanner(document);
		while(in.hasNext()) {
			String word = in.next();
			
			if(word.contains("RB")) {
				counter++;
			
				if (adverbs.indexOf(word) < 0) {
					adverbs.add(word);
					adverbWordCount.add(1);
				}
				else {
					int i = adverbWordCount.get(adverbs.indexOf(word));
					i++;
					adverbWordCount.set(adverbs.indexOf(word), i);
				}
			}
		}
		in.close();
		return counter;
	}
	
	protected int getNumTerms(String document) {
		int counter = 0;
		Scanner in = new Scanner(document);
		while(in.hasNext()) {
			counter++;
			in.next();
		}
		in.close();
		return counter;
	}
	
	private int getAverage(ArrayList<Integer> list) {
		int total = 0;
		int avg = 0;
		for(int i : list) {
			total += i;
		}
		avg = total/list.size();
		return avg;
	}
	
	private int getMedian(ArrayList<Integer> list) {
		Collections.sort(list);
		return list.get(list.size()/2);
	}
	
	protected int getSentenceAverage() {
		return getAverage(sentenceCount);
	}
	
	protected int getSentenceMedian() {
		return getMedian(sentenceCount);
	}
	
	protected int getNounAverage() {
		return getAverage(nounCount);
	}
	
	protected int getNounMedian() {
		return getMedian(nounCount);
	}
	
	protected int getVerbAverage() {
		return getAverage(verbCount);
	}
	
	protected int getVerbMedian() {
		return getMedian(verbCount);
	}
	
	protected int getAdjectiveAverage() {
		return getAverage(adjectiveCount);
	}
	
	protected int getAdjectiveMedian() {
		return getMedian(adjectiveCount);
	}
	
	protected int getAdverbAverage() {
		return getAverage(adverbCount);
	}
	
	protected int getAdverbMedian() {
		return getMedian(adverbCount);
	}
	
	static class WordCount implements Comparable {
		int count;
		String word;
		
		public WordCount(int count, String word) {
			this.count = count;
			this.word = word;
		}
		
		public int compareTo(Object o) {
			return ((WordCount)o).count - count;
		}
		
		public String toString() {
			return word + ": " + count;
		}
	}
	
	private ArrayList<WordCount> createWordCount(ArrayList<Integer> count, ArrayList<String> words) {
		ArrayList<WordCount> list = new ArrayList<WordCount>();
		for(int i = 0; i < count.size(); i++) {
			list.add(new WordCount(count.get(i), words.get(i)));
		}
		
		return list;
	}
	
	private void createWordCountArrays() {
		nounList = createWordCount(nounWordCount, nouns);
		verbList = createWordCount(verbWordCount, verbs);
		adjectiveList = createWordCount(adjectiveWordCount, adjectives);
		adverbList = createWordCount(adverbWordCount, adverbs);
	}
	protected int getTermAverage() {
		return getAverage(termCount);
	}
	
	protected int getTermMedian() {
		return getMedian(termCount);
	}
	private void getTopTen(ArrayList<WordCount> list) {
		int size = 0;
		if (list.size() < 20) {
			size = list.size();
		}
		else {
			size = 20;
		}
		Collections.sort(list);
		for (int i = 0; i < size; i++) {
			System.out.println(list.get(i));
		}
	}
	
	protected void getTopTenAll() {
		createWordCountArrays();
		System.out.println("======================================");
		System.out.println("Nouns");
		System.out.println("======================================");
		getTopTen(nounList);
		System.out.println("======================================");
		System.out.println("Verbs");
		System.out.println("======================================");
		getTopTen(verbList);
		System.out.println("======================================");
		System.out.println("Adjectives");
		System.out.println("======================================");
		getTopTen(adjectiveList);
		System.out.println("======================================");
		System.out.println("Adverbs");
		System.out.println("======================================");
		getTopTen(adverbList);
	}
}