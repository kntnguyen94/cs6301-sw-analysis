import java.util.Arrays;
import java.util.Scanner;

/* 
 * Preprocessor.java
 * =======================
 * This class does preprocessing on inputted documents to 
 * prepare for corpus creationg and indexing.
 * 
 * Author(s): Jacqueline Wong, Kevin Nguyen
 * Date: 2/20/2016
 * 
 * Assignment 2
 * Class: CS 6301.502 - Software Analysis and Comprehension
 */

public class Preprocessor {
	
	// Attributes
	private static final String[] STOP_WORDS = {"a","about","above","according","across","after","afterwards","again","against","albeit","all","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anywhere","apart","are","around","as","at","av","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","below","beside","besides","between","beyond","both","but","by","can","cannot","canst","certain","cf","choose","contrariwise","cos","could","cu","day","do","does","doesn\'t","doing","dost","doth","double","down","dual","during","each","either","else","elsewhere","enough","et","etc","even","ever","every","everybody","everyone","everything","everywhere","except","excepted","excepting","exception","exclude","excluding","exclusive","far","farther","farthest","few","ff","first","for","formerly","forth","forward","from","front","further","furthermore","furthest","get","go","had","halves","hardly","has","hast","hath","have","he","hence","henceforth","her","here","hereabouts","hereafter","hereby","herein","hereto","hereupon","hers","herself","him","himself","hindmost","his","hither","hitherto","how","however","howsoever","i","ie","if","in","inasmuch","inc","include","included","including","indeed","indoors","inside","insomuch","instead","into","inward","inwards","is","it","its","itself","just","kind","kg","km","last","latter","latterly","less","lest","let","like","little","ltd","many","may","maybe","me","meantime","meanwhile","might","moreover","most","mostly","more","mr","mrs","ms","much","must","my","myself","namely","need","neither","never","nevertheless","next","no","nobody","none","nonetheless","noone","nope","nor","not","nothing","notwithstanding","now","nowadays","nowhere","of","off","often","ok","on","once","one","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","own","per","perhaps","plenty","provide","quite","rather","really","round","said","sake","same","sang","save","saw","see","seeing","seem","seemed","seeming","seems","seen","seldom","selves","sent","several","shalt","she","should","shown","sideways","since","slept","slew","slung","slunk","smote","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","spake","spat","spoke","spoken","sprang","sprung","stave","staves","still","such","supposing","than","that","the","thee","their","them","themselves","then","thence","thenceforth","there","thereabout","thereabouts","thereafter","thereby","therefore","therein","thereof","thereon","thereto","thereupon","these","they","this","those","thou","though","thrice","through","throughout","thru","thus","thy","thyself","till","to","together","too","toward","towards","ugh","unable","under","underneath","unless","unlike","until","up","upon","upward","upwards","us","use","used","using","very","via","vs","want","was","we","week","well","were","what","whatever","whatsoever","when","whence","whenever","whensoever","where","whereabouts","whereafter","whereas","whereat","whereby","wherefore","wherefrom","wherein","whereinto","whereof","whereon","wheresoever","whereto","whereunto","whereupon","wherever","wherewith","whether","whew","which","whichever","whichsoever","while","whilst","whither","who","whoa","whoever","whole","whom","whomever","whomsoever","whose","whosoever","why","will","wilt","with","within","without","worse","worst","would","wow","ye","yet","year","yippee","you","your","yours","yourself","yourselves"};
	private static final String[] JAVA_RESERVED_WORDS = {"abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","do","double","else","enum","extends","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","try","void","volatile","while"};
	private static final String[] JAVA_COMMON_CLASSES = {"String", "Integer", "Double", "Boolean", "Float", "Long", "Set", "List", "ArrayList", "Map", "Exception", "System", "HashMap", "Object", "Thread", "Class", "Date", "Iterator", "Math", };
	private Stemmer s;

	// Methods
	public Preprocessor() {
		s = new Stemmer();
	}
	
	/**
	 * This method removes stop words (List of stop words in String[] STOP_WORDS
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	public String removeStopWords(String document) {
		String processedDocument = "";
		
		Scanner in = new Scanner(document);
		
		while(in.hasNext()) {
			String word = in.next();
			
			if (Arrays.asList(STOP_WORDS).contains(word.trim().toLowerCase())){
				word = "";
			}
			processedDocument = processedDocument + word + " ";
		}
		in.close();
		
		return processedDocument;
	}
	
	/**
	 * Removes imports and packages from the inputted string
	 * 
	 * @param String content
	 * @return String newContent
	 */
	public static String removeImportsAndPackages(String content) {
		String newContent = "";
		
		Scanner in = new Scanner(content);
		
		while (in.hasNext()) {
			String contentPiece = in.next();
			
			if (contentPiece.equals("import") || contentPiece.equals("package")) {
				in.next();
			}
			else {
				newContent = newContent + contentPiece + " ";
			}
		}
		in.close();
		
		return newContent;
	}

	/**
	 * This method removes stop words (List of stop words in String[] STOP_WORDS
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	public String removeJavaReservedWords(String document) {
		String processedDocument = "";
		
		Scanner in = new Scanner(document);
		
		while(in.hasNext()) {
			String word = in.next();
			
			if (Arrays.asList(JAVA_RESERVED_WORDS).contains(word.trim().toLowerCase())){
				word = "";
			}
			processedDocument = processedDocument + word + " ";
		}
		in.close();
		
		return processedDocument;
	}
	
	/**
	 * This method removes stop words (List of stop words in String[] STOP_WORDS
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	public String removeJavaCommonClasses(String document) {
		String processedDocument = "";
		
		Scanner in = new Scanner(document);
		
		while(in.hasNext()) {
			String word = in.next();
			
			if (Arrays.asList(JAVA_COMMON_CLASSES).contains(word.trim().toLowerCase())){
				word = "";
			}
			processedDocument = processedDocument + word + " ";
		}
		in.close();
		
		return processedDocument;
	}
	
	/**
	 * This method removes stop words (List of stop words in String[] STOP_WORDS
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	public String removeWordsLessThanCharMinimum(String document, int num) {
		String processedDocument = "";
		
		Scanner in = new Scanner(document);
		
		while(in.hasNext()) {
			String word = in.next().trim();
			
			if (word.trim().length() <= num) {
				word = "";
			}
			processedDocument = processedDocument + word + " ";
		}
		in.close();
		
		return processedDocument;
	}
	
	/**
	 * This method splits camel case words found in the document
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	public String splitCamelCase(String document) {
		String processedDocument = "";
		
		Scanner in = new Scanner(document);
		
		while(in.hasNext()) {
			String word = in.next();
			if(isCamelCase(word)) {
				word = word + " " + splitCamelCaseWord(word);
			}
			processedDocument = processedDocument + word + " ";
		}
		in.close();
		
		return processedDocument;
	}
	
	/**
	 * This method stems words in a document using PorterStemmer (See Stemmer.java)
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	public String porterStemmer(String document) {
		String processedDocument = "";
		
		Scanner in = new Scanner(document);
		
		while(in.hasNext()) {
			String word = in.next();
			
			//We check for camel case because we don't want to stem camel case words
			if(!isCamelCase(word)) {
				word = porterStemmerWord(word);
			}
			processedDocument = processedDocument + word + " ";
		}
		in.close();
		
		return processedDocument;
	}
	
	/**
	 * This method stems a word using Porter Stemmer
	 */
	private String porterStemmerWord(String word) {		
		for (int charPosition = 0; charPosition < word.length(); charPosition++) {
			s.add(word.charAt(charPosition));
		}
		s.stem();
		
		return s.toString();
	}
	
	/**
	 * Split the camel-cased inputted word
	 * 
	 * @param String word
	 * @return String splittedWord
	 */
	public static String splitCamelCaseWord(String word) {
		String splitted = word.replaceAll(
				String.format(
						"%s|%s|%s",
						"(?<=[A-Z])(?=[A-Z][a-z])", 
						"(?<=[^A-Z])(?=[A-Z])", 
						"(?<=[A-Za-z])(?=[^A-Za-z])"), 
						" "
		);

		return splitted;
	}
	
	/**
	 * This method checks whether a word is camel case
	 * 
	 * @param String word
	 * @return boolean camelCase
	 */
	private boolean isCamelCase(String word) {
		boolean camelCase = false;
		
		if (word.matches("([a-z]+[A-Z]+\\w+)+")) {
			camelCase = true;
		}
		else if (word.matches("([A-Z][a-z]+[A-Z]+\\w+)+")) {
			camelCase = true;
		}
		
		return camelCase;
	}
	
	/**
	 * This method removes numbers from the document
	 * 
	 * @param String word
	 * @return boolean isCamelCase
	 */
	public String removeNumbers(String document) {
		String processedDocument = "";
		
		Scanner in = new Scanner(document);
		while(in.hasNext()) {
			String word = in.next();
			
			for (char c : word.toCharArray()) {
				if (Character.isDigit(c)) {
					word = "";
				}
			}
			
			processedDocument = processedDocument + word + " ";
		}
		in.close();
		
		return processedDocument;
	}
	
	/**
	 * This method removes programming tokens (e.g., '{', '}', '[', '<', etc.) from the document
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	public String removeProgrammingTokens(String document) {
		String processedDocument = "";
		processedDocument = document.replaceAll("\\p{P}"," ");
		processedDocument = processedDocument.replaceAll("[-+^:, ;.*]"," ");
		return processedDocument;
	}
	
	/**
	 * This method removes comments from the input (Ex. Strings that start with // or /***\*\/)
	 * 
	 * @param String document
	 * @return String processedDocument
	 */
	 public String removeSpecialStringCharacters(String document) {
		 String processedDocument = "";
		 processedDocument = document.replaceAll("//", "");
		 processedDocument = document.replaceAll("/*", "");
		 processedDocument = document.replaceAll("/**", "");
		 processedDocument = document.replaceAll("*", "");
		 processedDocument = document.replaceAll("*/", "");
		 processedDocument = document.replaceAll("**/", "");
		 return processedDocument;
	 }
}
