import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * Update comments here later...
 */
public class Main {
	
	// Change this flag to switch between file and method level granularity
	private static final boolean FILE_GRANULARITY = false;
	private static ArrayList<String> publicStaticVars;
	private static ArrayList<String> publicStaticVarLocations;

	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// List other File[] files here, just comment them out based on what you want.
		//File[] files = new File("D:\\Users\\user1\\workspace\\LuceneDocumentParser\\freemindJava").listFiles();
		File[] files = new File("D:\\CMS Java").listFiles();
		
		// file-level granularity
		if (FILE_GRANULARITY) {
			getAllPublicStaticVars(files);
			ArrayList<ArrayList<String>> filesUsingGlobalVariables = new ArrayList<ArrayList<String>>(); 
			
			for(File file : files) {
				String fileName = file.getName();
				
				// parse the file
				CompilationUnit compUnit = parseFile(file);
				
				// create and accept the visitor
				GeneralVisitor visitor = new GeneralVisitor(compUnit);
				compUnit.accept(visitor);
				
				// get the methods in this file
				List<String> methodBodies = visitor.getMethodBodies();
				
				// check to see where these public static variables are used.
				for (int i = 0; i < publicStaticVars.size(); i++) {
					ArrayList<String> filesWithGlobalVar;
					
					// check to see if there's an arraylist at the position already
					// if not, instantiate a new arraylist at the position 
					try {
						filesWithGlobalVar = filesUsingGlobalVariables.get(i);
					} catch(Exception e) {
						filesUsingGlobalVariables.add(new ArrayList<String>());
						filesWithGlobalVar = filesUsingGlobalVariables.get(i);
					}		
					
					// loop through the methods and check if the global variable is in any of the methods
					for (int j = 0; j < methodBodies.size(); j++) {
						if (methodBodies.get(j).indexOf(publicStaticVars.get(i)) > -1) {
							// check if the file is not already mentioned and is not the file the global variable is located in
							if (!filesWithGlobalVar.contains(fileName) && !publicStaticVarLocations.get(i).equals(fileName)) {
								filesWithGlobalVar.add(fileName);
							}
						}
					}
				}
			}
			
			// loop through all the global variables
			for (int i = 0; i < publicStaticVars.size(); i++) {
				String fileNamesUsingGlobalVar = "FILES: ";
				ArrayList<String> fileUsingGlobalVariable = filesUsingGlobalVariables.get(i);
				
				// check if the global variable is used in more than one external file
				if (fileUsingGlobalVariable.size() > 1) {
					// append all the file names to a string to be printed later
					for (int j = 0; j < fileUsingGlobalVariable.size(); j++) {
						fileNamesUsingGlobalVar = fileNamesUsingGlobalVar + fileUsingGlobalVariable.get(j) + ", ";
					}
					fileNamesUsingGlobalVar = fileNamesUsingGlobalVar + "\n";
					
					// add the global variable name being used to the string and then print results
					fileNamesUsingGlobalVar = fileNamesUsingGlobalVar + "GLOBAL VARIABLE: " + publicStaticVars.get(i) + " from FILE: " + publicStaticVarLocations.get(i) + "\n";
					System.out.println(fileNamesUsingGlobalVar);
				}
			}
		}
		// method-level granularity
		else {
			ArrayList<ArrayList<String>> methodsUsingInstanceVariables = new ArrayList<ArrayList<String>>(); 
			
			for(File file : files) {
				// parse the file
				CompilationUnit compUnit = parseFile(file);
				
				// create and accept the visitor
				GeneralVisitor visitor = new GeneralVisitor(compUnit);
				compUnit.accept(visitor);
				
				// get all the instance variables, method names, and method bodies
				List<String> instanceVariables = visitor.getInstanceVariables();
				List<String> methodNames = visitor.getMethodNames();
				List<String> methodBodies = visitor.getMethodBodies();
				
				// loop through all the instance variables
				for (int i = 0; i < instanceVariables.size(); i++) {
					ArrayList<String> methodsWithInstanceVar;
					
					// check to see if there's an arraylist at the position already
					// if not, instantiate a new arraylist at the position 
					try {
						methodsWithInstanceVar = methodsUsingInstanceVariables.get(i);
					} catch(Exception e) {
						methodsUsingInstanceVariables.add(new ArrayList<String>());
						methodsWithInstanceVar = methodsUsingInstanceVariables.get(i);
					}
					
					// loop through all the methods
					for (int j = 0; j < methodNames.size(); j++) {
						// check if the instance variable occurs in the method body
						if (methodBodies.get(j).indexOf(instanceVariables.get(i)) > -1) {
							// add the method name to list
							methodsWithInstanceVar.add(methodNames.get(j));
						}
					}
				}
				
				// loop through all the instance variables
				for (int i = 0; i <instanceVariables.size(); i++) {
					String methodNamesUsingInstanceVar = "FILE: " + file.getName() + "\nMETHODS: ";
					ArrayList<String> methodUsingInstanceVariable = methodsUsingInstanceVariables.get(i);
					
					// check if the instance variable is used in more than one method
					if (methodUsingInstanceVariable.size() > 1) {
						// append all the file names to a string to be printed later
						for (int j = 0; j < methodUsingInstanceVariable.size(); j++) {
							methodNamesUsingInstanceVar = methodNamesUsingInstanceVar + methodUsingInstanceVariable.get(j) + ", ";
						}
						methodNamesUsingInstanceVar = methodNamesUsingInstanceVar + "\n";
						
						// add the instance variable name being used to the string and then print results
						methodNamesUsingInstanceVar = methodNamesUsingInstanceVar + "INSTANCE VARIABLE: " + instanceVariables.get(i) + "\n";
						System.out.println(methodNamesUsingInstanceVar);
					}
				}
			}
		}
	}
	
	/**
	 * Retrieves all the public static variables and locations in source files
	 * 
	 * @param files
	 * @throws IOException
	 */
	public static void getAllPublicStaticVars(File[] files) throws IOException {
		publicStaticVars = new ArrayList<String>();
		publicStaticVarLocations = new ArrayList<String>();
		
		for(File file : files) {
			String fileName = file.getName();
			
			// parse the file
			CompilationUnit compUnit = parseFile(file);
			
			// create and accept the visitor
			GeneralVisitor visitor = new GeneralVisitor(compUnit);
			compUnit.accept(visitor);

			// get all the public static variables
			for (String publicStaticVar : visitor.getPublicStaticVars()) {
				publicStaticVars.add(publicStaticVar);
				// System.out.println("GLOBAL VARIABLE HOORAY: " + publicStaticVar);
				publicStaticVarLocations.add(fileName);
			}
		}
	}

	/**
	 * Parses a java file
	 * 
	 * @param file the file to parse
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
	 * @param parser the AST parser
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
	}
}
