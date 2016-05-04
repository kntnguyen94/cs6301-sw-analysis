import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
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
public class MainVisitor {
	
	// Change this flag to switch between file and method level granularity
	private static final boolean FILE_GRANULARITY = false;
	private static ArrayList<String> staticVars;
	private static ArrayList<String> staticVarLocations;

	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
				//File[] files = new File("C:\\Nam\\Adv Algo\\Freemind Java\\Freemind Java").listFiles();
				//File[] nonJava = new File("C:\\Nam\\Adv Algo\\Freemind Java\\Freemind Non-Java").listFiles();
				File[] files = new File("C:\\Nam\\CMS Java").listFiles();
				File[] nonJava = new File("C:\\Nam\\CMS Non-Java").listFiles();
				String baseFolder = "jabref-src";
				String filePath = baseFolder + File.separator + "GrammarBasedSearchRule.java";
				//File[] files = new File(filePath).listFiles();
				PrintWriter pw = new PrintWriter("CMS_Java_Method_Level.txt");
				System.out.println("start!");
		
		// file-level granularity
		if (FILE_GRANULARITY) {
			getAllstaticVars(files);
			ArrayList<ArrayList<String>> filesUsingGlobalVariables = new ArrayList<ArrayList<String>>(); 
			//EXTERNAL FILES
			ArrayList<String> nonJavaNames = new ArrayList<>();
			ArrayList<String> nonJavaCoupling = new ArrayList<>();
			for(File file : nonJava)
			{
				nonJavaNames.add(file.getName());
			}
			System.out.println("after nonjava");
			for(File file : files) {
				String fileName = file.getName();
				
				// parse the file
				CompilationUnit compUnit = parseFile(file);
				
				// create and accept the visitor
				GeneralVisitor visitor = new GeneralVisitor(compUnit);
				compUnit.accept(visitor);
				
				// get the methods in this file
				List<String> methodBodies = visitor.getMethodBodies();
				List<String> rightAssignments = visitor.getRightAssignments();
				List<String> methodInvoArgs = visitor.getMethodInvoArgs();
				
				for(int i = 0; i < nonJavaNames.size(); i++) {
					boolean breaker = false;
					for(String mBody : methodBodies) {
						if(mBody.contains(nonJavaNames.get(i)) && !nonJavaNames.get(i).equals("ld")) {
							nonJavaCoupling.add(nonJavaNames.get(i) + " -------- " + file.getName());
							breaker = true;
							break;
						}
					}
					if(breaker) {
						continue;
					}
					for(String rAssign : rightAssignments) {
						if(rAssign.contains(nonJavaNames.get(i)) && !nonJavaNames.get(i).equals("ld")) {
							nonJavaCoupling.add(nonJavaNames.get(i) + " -------- " + file.getName());
							breaker = true;
							break;
						}
					}
					if(breaker) {
						continue;
					}
					for(String mInvo : methodInvoArgs) {
						if(mInvo.contains(nonJavaNames.get(i)) && !nonJavaNames.get(i).equals("ld")) {
							nonJavaCoupling.add(nonJavaNames.get(i) + " -------- " + file.getName());
							breaker = true;
							break;
						}
					}
					if(breaker) {
						continue;
					}
				}
				// check to see where these public static variables are used.
				for (int i = 0; i < staticVars.size(); i++) {
					ArrayList<String> filesWithGlobalVar;
					ArrayList<String> filesWithExternalFile;
					// check to see if there's an arraylist at the position already
					// if not, instantiate a new arraylist at the position 
					try {
						filesWithGlobalVar = filesUsingGlobalVariables.get(i);
					} catch(Exception e) {
						filesUsingGlobalVariables.add(new ArrayList<String>());
						filesWithGlobalVar = filesUsingGlobalVariables.get(i);
					}		
					
					
					
					
					// checking assignments
					for (int j = 0; j < rightAssignments.size(); j++) {
						String varLocation = staticVarLocations.get(i);
						if (rightAssignments.get(j).indexOf(varLocation.substring(0, varLocation.length()-4) + staticVars.get(i)) > -1) {
							// check if the file is not already mentioned and is not the file the global variable is located in
							if (!filesWithGlobalVar.contains(fileName)) {
								filesWithGlobalVar.add(fileName);
							}
						}
					}
					
					// loop through the methods and check if the global variable is in any of the methods
					for (int j = 0; j < methodBodies.size(); j++) {
						String varLocation = staticVarLocations.get(i);
						//System.out.println(varLocation.substring(0, varLocation.length()-4) + staticVars.get(i));

						if (methodBodies.get(j).indexOf(varLocation.substring(0, varLocation.length()-4) + staticVars.get(i)) > -1) {
							// check if the file is not already mentioned and is not the file the global variable is located in
							if (!filesWithGlobalVar.contains(fileName) && !staticVarLocations.get(i).equals(fileName)) {
								filesWithGlobalVar.add(fileName);
							}
						}
					
					}
					
					
				}
			}
			
			
			int counter = 0;
			ArrayList<Variable> top10Files = new ArrayList<>();
			// loop through all the global variables
			for (int i = 0; i < staticVars.size(); i++) {
				String fileNamesUsingGlobalVar = "FILES: ";
				ArrayList<String> fileUsingGlobalVariable = filesUsingGlobalVariables.get(i);
				
				// check if the global variable is used in at least one external file
				if (fileUsingGlobalVariable.size() > 0) {
					int top10Counter = 0;
					counter++;
					// append all the file names to a string to be printed later
					for (int j = 0; j < fileUsingGlobalVariable.size(); j++) {
						fileNamesUsingGlobalVar = fileNamesUsingGlobalVar + fileUsingGlobalVariable.get(j) + ", ";
						top10Counter++;
					}
					fileNamesUsingGlobalVar = fileNamesUsingGlobalVar + "\n";
					
					// add the global variable name being used to the string and then print results
					 fileNamesUsingGlobalVar = fileNamesUsingGlobalVar + "GLOBAL VARIABLE: " + staticVars.get(i) + " from FILE: " + staticVarLocations.get(i) + "\n";
					 System.out.println(fileNamesUsingGlobalVar);
					 pw.println(fileNamesUsingGlobalVar);
					 top10Files.add(new Variable(staticVars.get(i) + "\n" + fileNamesUsingGlobalVar, top10Counter));
					//String varLocation = staticVarLocations.get(i);
					//System.out.println(varLocation.substring(0, varLocation.length()-4) + staticVars.get(i));
					
				}
			}
			Collections.sort(top10Files, new Variable());
			//Print top 10 list
			for(int i = 0;i < 10;i++)
			{
				System.out.println((i+1) + ". " + top10Files.get(i).toString());
				pw.println((i+1) + ". " + top10Files.get(i).toString());
			}
			System.out.println("We found " + staticVars.size() + " static variables and " + counter + " global variables used in at least one external file.\n");
			pw.println("We found " + staticVars.size() + " static variables and " + counter + " global variables used in at least one external file.\n");
			
			/*
			for(int i = 0; i < staticVars.size(); i++)
			{
				System.out.println(staticVarLocations.get(i) + "." + staticVars.get(i) + " is number " + i);
			}
			*/
			if(nonJavaCoupling.size() > 0)
			{
				System.out.println("LIST OF EXTERNAL FILES FROM DIRECTORY USED IN JAVA SYSTEM");
				PrintWriter externalWriter = new PrintWriter("External_Files.txt");
				externalWriter.println("LIST OF EXTERNAL FILES FROM DIRECTORY USED IN JAVA SYSTEM\n");
				for(int i = 0; i < nonJavaCoupling.size(); i++)
				{
					System.out.println(nonJavaCoupling.get(i));
					externalWriter.println(nonJavaCoupling.get(i));
				}
				externalWriter.close();
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
						pw.println(methodNamesUsingInstanceVar);
					}
				}
			}
		}
		pw.close();
	}
	
	/**
	 * Retrieves all the public static variables and locations in source files
	 * 
	 * @param files
	 * @throws IOException
	 */
	public static void getAllstaticVars(File[] files) throws IOException {
		staticVars = new ArrayList<String>();
		staticVarLocations = new ArrayList<String>();
		
		for(File file : files) {
			String fileName = file.getName();
			
			// parse the file
			CompilationUnit compUnit = parseFile(file);
			
			// create and accept the visitor
			GeneralVisitor visitor = new GeneralVisitor(compUnit);
			compUnit.accept(visitor);

			// get all the public static variables
			for (String publicStaticVar : visitor.getStaticVars()) {
				staticVars.add(publicStaticVar);
				// System.out.println("GLOBAL VARIABLE HOORAY: " + publicStaticVar);
				staticVarLocations.add(fileName);
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
