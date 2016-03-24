package seers.astvisitortest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Program that extracts AST info from a Java file
 * 
 * @author ojcch
 *
 */
public class MainVisitor {
	
	private static final String[] JAVA_RESERVED_WORDS = {"abstract","assert","boolean","break","byte","case","catch","char","class","const","continue","default","do","double","else","enum","extends","final","finally","float","for","goto","if","implements","import","instanceof","int","interface","long","native","new","package","private","protected","public","return","short","static","strictfp","super","switch","synchronized","this","throw","throws","transient","try","void","volatile","while"};
	private static final String[] JAVA_COMMON_CLASSES = {"String", "Integer", "Double", "Boolean", "Float", "Long", "Set", "List", "ArrayList", "Map", "Exception", "System", "HashMap", "Object", "Thread", "Class", "Date", "Iterator", "Math", };

	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// file to parse
		String baseFolder = "jabref-src";
		String fileName = "";
		//String filePath = baseFolder + File.separator + "GrammarBasedSearchRule.java";
		//String filePath = baseFolder + File.separator + "Countdown.java";
		//File[] files = new File("D:\\Users\\user1\\workspace\\LuceneDocumentParser\\freemindJava").listFiles();
		File[] files = new File("D:\\CMS Java").listFiles();
		for(File file : files) {	
			// parse the file
			CompilationUnit compUnit = parseFile(file);

			// create and accept the visitor
			GeneralVisitor visitor = new GeneralVisitor(compUnit);
			compUnit.accept(visitor);
			//visitor.setCompUnit(compUnit);
			// print the list of methods and fields of the Java file
			List<Variable> variables = visitor.getVariables();
			List<String> fields = visitor.getFields();
			List<String> methods = visitor.getMethods();
			List<String> assigns = visitor.getAssignments();
			List<String> leftAssigns = visitor.getLeftAssignments();
			List<String> rightAssigns = visitor.getRightAssignments();
			List<String> readVars = visitor.getReadVars();
			List<String> fieldAcc = visitor.getFieldAccess();
			List<String> invoArgs = visitor.getMethodInvocationArguments();
			List<String> methodExp = visitor.getMethodExp();
			List<String> returnStatements = visitor.getReturnStatements();
		
			
			ArrayList<String> processedReads = new ArrayList<String>();
			//check left Assigns for read
			for(int i = 0; i < leftAssigns.size(); i++) {
				String[] splitAssign = leftAssigns.get(i).split("\\.");
				int num = splitAssign.length;
				
				if(num > 1) {
					num = num - 1;
				}
				
				for(int j = 0; j < num; j++) {
					
					String word = splitAssign[j];
					boolean passesProcessing = true;
					if (Arrays.asList(JAVA_RESERVED_WORDS).contains(word)){
						passesProcessing = false;
					}
					
					if (Arrays.asList(JAVA_COMMON_CLASSES).contains(word)){
						passesProcessing = false;
					}
					
					if(word.indexOf("\"") > -1) {
						passesProcessing = false;
					}
					
					if(word.indexOf("[") > -1) {
						word = word.substring(0, word.indexOf("["));
					}
					
					if (passesProcessing) {
						processedReads.add(word);
					}
				}
			}
			
			//check right Assigns for read
			for(int i = 0; i < rightAssigns.size(); i++) {
				String[] splitAssign = rightAssigns.get(i).split("\\.");
				
				for(int j = 0; j < splitAssign.length; j++) {
					String word = splitAssign[j];
					boolean passesProcessing = true;
					if (Arrays.asList(JAVA_RESERVED_WORDS).contains(word)){
						passesProcessing = false;
					}
					
					if (Arrays.asList(JAVA_COMMON_CLASSES).contains(word)){
						passesProcessing = false;
					}
					
					if(word.indexOf("(") > -1) {
						passesProcessing = false;
					}
					
					if(word.indexOf("\"") > -1) {
						passesProcessing = false;
					}
					
					if(word.indexOf("[") > -1) {
						word = word.substring(0, word.indexOf("["));
					}
					
					if (passesProcessing) {
						processedReads.add(word);
					}
				}
			}
			
			for(int i = 0; i < methodExp.size(); i++) {
				String[] splitAssign = methodExp.get(i).split("\\.");
				
				for(int j = 0; j < splitAssign.length; j++) {
					String word = splitAssign[j];
					boolean passesProcessing = true;
					if (Arrays.asList(JAVA_RESERVED_WORDS).contains(word)){
						passesProcessing = false;
					}
					
					if (Arrays.asList(JAVA_COMMON_CLASSES).contains(word)){
						passesProcessing = false;
					}
					
					if(word.indexOf("(") > -1) {
						passesProcessing = false;
					}
					
					if(word.indexOf("\"") > -1) {
						passesProcessing = false;
					}
					
					if(word.indexOf("[") > -1) {
						word = word.substring(0, word.indexOf("["));
					}
					
					if (passesProcessing) {
						processedReads.add(word);
					}
				}
			}
			
			for(int i = 0; i < returnStatements.size(); i++) {
				String[] splitAssign = returnStatements.get(i).split("\\.");
				
				for(int j = 0; j < splitAssign.length; j++) {
					String word = splitAssign[j];
					boolean passesProcessing = true;
					if (Arrays.asList(JAVA_RESERVED_WORDS).contains(word)){
						passesProcessing = false;
					}
					
					if (Arrays.asList(JAVA_COMMON_CLASSES).contains(word)){
						passesProcessing = false;
					}
					
					if(word.indexOf("(") > -1) {
						passesProcessing = false;
					}
					
					if(word.indexOf("\"") > -1) {
						passesProcessing = false;
					}
					
					if(word.indexOf("[") > -1) {
						word = word.substring(0, word.indexOf("["));
					}
					
					if (passesProcessing) {
						processedReads.add(word);
					}
				}
			}
			
			//remove variables
			for(int i = 0; i < invoArgs.size(); i++) {
				//System.out.println("ARGS: " + invoArgs.get(i));
				String[] splitAssign = invoArgs.get(i).split("\\s+|\\.|\\,|\\)");
				
				for(int j = 0; j < splitAssign.length; j++) {
					String word = splitAssign[j];
					
					
					boolean passesProcessing = true;
					if (Arrays.asList(JAVA_RESERVED_WORDS).contains(word)){
						passesProcessing = false;
					}
					
					if (Arrays.asList(JAVA_COMMON_CLASSES).contains(word)){
						passesProcessing = false;
					}
					
					if(word.indexOf("\"") > -1) {
						passesProcessing = false;
					}
					
					word = word.replaceAll("[(]", "");

					if(word.indexOf("[") > -1) {
						word = word.substring(0, word.indexOf("["));
					}
					
					if (passesProcessing) {
						processedReads.add(word);
					}
				}
			}
			
			ArrayList<String> variableNames = new ArrayList<String>();
			
			for(Variable v : variables) {
				variableNames.add(v.getVariableName());
			}
			
			for (String s : processedReads) {
				if(variableNames.contains(s)) {
					variables.remove(variableNames.indexOf(s));
					variableNames.remove(s);
				}
			}
			
			//print magical list
			if (variables.size() > 0) {
				System.out.println("File: " + file.getName());
			}
			
			for (int i = 0; i < variables.size(); i++) {
				Variable variable = variables.get(i);
				System.out.println("* The [" + variable.isFieldOrVariable() + "] [" + variable.getVariableName() + "] is declared but never read in the code (line:[" + variable.getLineNum() + "])");
			}
		}

	}

	/**
	 * Parses a java file
	 * 
	 * @param file
	 *            the file to parse
	 * @return the CompilationUnit of a java file (i.e., its AST)
	 * @throws IOException
	 */
	private static CompilationUnit parseFile(File file) throws IOException {

		// read the content of the file
		char[] fileContent = FileUtils.readFileToString(file).toCharArray();

		// create the AST parser
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setUnitName(file.getName());
		parser.setSource(fileContent);
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		// set some default configuration
		setParserConfiguration(parser);

		// parse and return the AST
		return (CompilationUnit) parser.createAST(null);

	}

	/**
	 * Sets the default configuration of an AST parser
	 * 
	 * @param parser
	 *            the AST parser
	 */
	public static void setParserConfiguration(ASTParser parser) {
		@SuppressWarnings("unchecked")
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_8, options);

		parser.setCompilerOptions(options);
		parser.setResolveBindings(true);

		// parser.setEnvironment(classPaths, sourceFolders, encodings, true);
	}
}
