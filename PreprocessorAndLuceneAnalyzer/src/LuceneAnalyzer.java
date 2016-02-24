import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;

/* 
 * Analyzer.java
 * =======================
 * This class uses Lucene to analyze and do text retrieval on documents
 * 
 * Author(s): Jacqueline Wong, Kevin Nguyen
 * Date: 2/20/2016
 * 
 * Assignment 2
 * Class: CS 6301.502 - Software Analysis and Comprehension
 */

public class LuceneAnalyzer {
	
	private Analyzer analyzer;
	private Directory index;
	private IndexWriterConfig config;
	private IndexWriter writer;
	private File[] documents;
	private Preprocessor preprocessor;
	
	/**
	 * ==========Constructors===========
	 */
	public LuceneAnalyzer() {}
	
	public LuceneAnalyzer(Analyzer analyzer, Directory index, File[] documents) throws IOException {
		this.analyzer = analyzer;
		this.index = index;
		this.documents = documents;
		
		try {
			config = new IndexWriterConfig(analyzer);
			writer = new IndexWriter(index, config);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		preprocessor = new Preprocessor();
		traverseFiles(documents);
	}
	
	
	/**
	 * This method is used to query the corpus
	 * 
	 * @param query
	 */
	public void query(String query, int hits) throws IOException{
		writer.close();
		String processedQuery = preprocess(query);
		analyzeFiles(processedQuery, hits);
	}
	
	/**
	 * This method preprocesses the input before usage (Ex. Adding to corpus, querying the corpus)
	 * 
	 * @param input
	 * @return processedInput
	 */
	protected String preprocess(String input) {
		String processedInput = "";
		processedInput = preprocessor.removeJavaReservedWords(input);
		//processedInput = preprocessor.removeSpecialStringCharacters(processedInput);
		processedInput = preprocessor.removeProgrammingTokens(processedInput);
		processedInput = preprocessor.removeJavaCommonClasses(processedInput);
		processedInput = preprocessor.removeStopWords(processedInput);
		processedInput = preprocessor.removeWordsLessThanCharMinimum(processedInput, 2);
		processedInput = preprocessor.splitCamelCase(processedInput);
		processedInput = preprocessor.porterStemmer(processedInput);
		return processedInput;
	}
	
	/**
	 * This method analyzes the files based on the query given and prints the results
	 * 
	 * @param query
	 */
	protected void analyzeFiles(String query, int hitsX) {	
		try {		
			// Searching code
			int hitsPerPage = hitsX;
		    IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    Query q = new QueryParser("content", analyzer).parse(query);
		    
		    //System.out.println(searcher.search(q, hitsPerPage));
		    /*
		    TermStats[] termArray = new TermStats[999];
		    termArray = HighFreqTerms.getHighFreqTerms(reader, 999, "content", new HighFreqTerms.TotalTermFreqComparator());
		    //PrintWriter pw = new PrintWriter(termsFile);
		    //Terms terms = SlowCompositeReaderWrapper.wrap(reader).terms("title");
		    //System.out.println(terms.size() + " is term size");
		    for(int i = 0; i < 20; i++) {
		    	System.out.println(termArray[i].toString());
		    }
		    		    */
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		    searcher.search(q, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    for (int i = 0; i < hits.length; i++) {
		    	int docId = hits[i].doc;
		    	Document d = searcher.doc(docId);
		    	System.out.println("====================================");
		    	System.out.println(d.get("name"));
		    	//System.out.println(d.get("content"));
		    	System.out.println("====================================");
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method traverses the files, sends them to the preprocessor, and adds them into the corpus
	 * 
	 * @param files
	 * @throws IOException 
	 */
	protected void traverseFiles(File[] files) throws IOException {
		String content = "";
		System.out.println("Beginning to traverse files...");
		
		for (File file : files) {
	        if (file.isDirectory()) {
	            System.out.println("Traversing files in directory " + file.getName());
	            traverseFiles(file.listFiles());
	        } else {
	            System.out.println("Looking at file: " + file.getName());
	            try {
	            	System.out.println("Preprocessing file: " + file.getName());
					content = new Scanner(file).useDelimiter("\\Z").next();
					content = preprocess(content);					
					try {
						addDoc(file.getName(), content);
					} catch (IOException e) {
						e.printStackTrace();
					}
	            } catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	        }
	    }
	}
	
	/**
	 * This method is used to add documents into the corpus
	 * 
	 * @param content
	 * @throws IOException
	 */
	protected void addDoc(String name, String content) throws IOException 
	{
		  Document doc = new Document();
		  doc.add(new TextField("name", name, Field.Store.YES));
		  doc.add(new TextField("content", content, Field.Store.YES));
		  writer.addDocument(doc);
	}
}