import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;

public class Main {
    private static final int MAX_LUCENE_ISSUE_NUM = 7077;
    private static final int MAX_OFBIZ_ISSUE_NUM = 100;
    
    public static ArrayList<String> titles;
    public static ArrayList<String> descriptions;
    public static ArrayList<String> both;
    
    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read); 

            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
    static class Item {
        Field fields;
    }
    
    static class Field {
    	String summary;
    	String description;
    }

    public static void main(String[] args) throws Exception {
    	
    	titles = new ArrayList<String>();
        descriptions = new ArrayList<String>();
        both = new ArrayList<String>();
        
    	LemmaSplit titlePP = new LemmaSplit();
    	LemmaSplit descPP = new LemmaSplit();
    	LemmaSplit titleDescPP = new LemmaSplit();
    	int numBad = 0;

    	//for (int i = 1; i <= 200; i++) {
    	for (int i = 1; i <= MAX_OFBIZ_ISSUE_NUM; i++) {
    		//String url = "https://issues.apache.org/jira/rest/api/latest/issue/LUCENE-" + i + "?fields=summary,description";
    		String url = "https://issues.apache.org/jira/rest/api/latest/issue/OFBIZ-" + i + "?fields=summary,description";
    		try {
	    		String json = readUrl(url);
	    		Gson gson = new Gson();        
	    	    Item item = gson.fromJson(json, Item.class);
	    	    
	    		String title = item.fields.summary;
	    		String description = item.fields.description;
	    		
	    		titlePP.preprocess(title);
	    		descPP.preprocess(description);
	    		titleDescPP.preprocess(title + " " + description);
	    		
	    		System.out.println(i);
    		} catch (Exception e) {
    			System.out.println("BAD " + i);
    			numBad++;
    		}
    	}
    	
    	//process the stuff here after
    	
    	
    	Scanner in = new Scanner(System.in);
    	
    	//titles only
    	System.out.println("======================================");
    	System.out.println("Title Only");
    	System.out.println("======================================");
    	System.out.println("Sentence Average: " + titlePP.getSentenceAverage());
    	System.out.println("Sentence Median: " + titlePP.getSentenceMedian());
    	System.out.println("Noun Average: " + titlePP.getNounAverage());
    	System.out.println("Noun Median: " + titlePP.getNounMedian());
    	System.out.println("Verb Average: " + titlePP.getVerbAverage());
    	System.out.println("Verb Median: " + titlePP.getVerbMedian());
    	System.out.println("Adjective Average: " + titlePP.getAdjectiveAverage());
    	System.out.println("Adjective Median: " + titlePP.getAdjectiveMedian());
    	System.out.println("Adverb Average: " + titlePP.getAdverbAverage());
    	System.out.println("Adverb Median: " + titlePP.getAdverbMedian());
    	System.out.println("Term Average: " + titleDescPP.getTermAverage());
    	System.out.println("Term Median: " + titleDescPP.getTermMedian());
    	in.next();
    	
    	titlePP.getTopTenAll();

    	//descriptions only
    	System.out.println("======================================");
    	System.out.println("Description Only");
    	System.out.println("======================================");
    	
    	System.out.println("Sentence Average: " + descPP.getSentenceAverage());
    	System.out.println("Sentence Median: " + descPP.getSentenceMedian());
    	System.out.println("Noun Average: " + descPP.getNounAverage());
    	System.out.println("Noun Median: " + descPP.getNounMedian());
    	System.out.println("Verb Average: " + descPP.getVerbAverage());
    	System.out.println("Verb Median: " + descPP.getVerbMedian());
    	System.out.println("Adjective Average: " + descPP.getAdjectiveAverage());
    	System.out.println("Adjective Median: " + descPP.getAdjectiveMedian());
    	System.out.println("Adverb Average: " + descPP.getAdverbAverage());
    	System.out.println("Adverb Median: " + descPP.getAdverbMedian());
    	System.out.println("Term Average: " + titleDescPP.getTermAverage());
    	System.out.println("Term Median: " + titleDescPP.getTermMedian());
    	descPP.getTopTenAll();
    	in.next();
    	
    	//both
    	System.out.println("======================================");
    	System.out.println("Both");
    	System.out.println("======================================");

    	System.out.println("Sentence Average: " + titleDescPP.getSentenceAverage());
    	System.out.println("Sentence Median: " + titleDescPP.getSentenceMedian());
    	System.out.println("Noun Average: " + titleDescPP.getNounAverage());
    	System.out.println("Noun Median: " + titleDescPP.getNounMedian());
    	System.out.println("Verb Average: " + titleDescPP.getVerbAverage());
    	System.out.println("Verb Median: " + titleDescPP.getVerbMedian());
    	System.out.println("Adjective Average: " + titleDescPP.getAdjectiveAverage());
    	System.out.println("Adjective Median: " + titleDescPP.getAdjectiveMedian());
    	System.out.println("Adverb Average: " + titleDescPP.getAdverbAverage());
    	System.out.println("Adverb Median: " + titleDescPP.getAdverbMedian());
    	System.out.println("Term Average: " + titleDescPP.getTermAverage());
    	System.out.println("Term Median: " + titleDescPP.getTermMedian());
    	
    	titleDescPP.getTopTenAll();
    	System.out.println("Total Bad: " + numBad);
    }
}
