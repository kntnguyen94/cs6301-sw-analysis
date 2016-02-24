import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;


public class Main {
	public static void main(String[] args) throws IOException {
		//QUERIES FOR ASPECTJ
		String bug482990Title = "Load time weaving silently generates invalid classes without StackMapTables if aj.org.objectweb.asm package is not found ";

		String bug482990Description = "After upgrading from AspectJ 1.7.2 (Spring EBR osgi bundle) to 1.8.7 (Apache ServiceMix osgi bundle) in our OSGI environment, we started seeing VerifyErrors like these for load-time woven classes: HttpServiceContext{httpContext=our.paclage.BundleHttpContext@5552fc77}: java.lang.VerifyError: Expecting a stackmap frame at branch target 63|Exception Details:|  Location:|    our/package/OurResource.<init>()V @23: ifnull|  Reason:|    Expected stackmap frame at this location.|  Bytecode:|    0x0000000: 2ab7 0001 b200 c62a 2ab8 009c 4db2 0096|    0x0000010: 2a2a b800 9c4c 2ac6 0028 2ab6 00ac 12ae|    0x0000020: b600 bc99 001c 2ab6 00ac 12ae b600 b4c0|    0x0000030: 00ae b800 b899 000a b800 a22a b600 a800|    0x0000040: 2ac6 0021 2ab6 00ac 12ae b600 bc99 0015|    0x0000050: 2ab6 00ac 12ae b600 b4c0 00ae b800 b89a|    0x0000060: 0021 2ac6 001d 2ab6 00ac 12ae b600 bc99|    0x0000070: 0011 2bb8 00c3 9900 0ab8 00a2 2ab6 00bf|    0x0000080: 00a7 0003 2ab6 00ac 12ae b600 b4c0 00ae|    0x0000090: b800 b89a 0011 2cb8 00c3 9900 0ab8 00a2|    0x00000a0: 2ab6 00bf b1                           | java.lang.VerifyError: Expecting a stackmap frame at branch target 63|Exception Details:|  Location:|    our/package/OurResource.<init>()V @23: ifnull|  Reason:|    Expected stackmap frame at this location.|  Bytecode:|    0x0000000: 2ab7 0001 b200 c62a 2ab8 009c 4db2 0096|    0x0000010: 2a2a b800 9c4c 2ac6 0028 2ab6 00ac 12ae|    0x0000020: b600 bc99 001c 2ab6 00ac 12ae b600 b4c0|    0x0000030: 00ae b800 b899 000a b800 a22a b600 a800|    0x0000040: 2ac6 0021 2ab6 00ac 12ae b600 bc99 0015|    0x0000050: 2ab6 00ac 12ae b600 b4c0 00ae b800 b89a|    0x0000060: 0021 2ac6 001d 2ab6 00ac 12ae b600 bc99|    0x0000070: 0011 2bb8 00c3 9900 0ab8 00a2 2ab6 00bf|    0x0000080: 00a7 0003 2ab6 00ac 12ae b600 b4c0 00ae|    0x0000090: b800 b89a 0011 2cb8 00c3 9900 0ab8 00a2|    0x00000a0: 2ab6 00bf b1                           | at java.lang.Class.getDeclaredConstructors0(Native Method)... Investigation of the woven bytecode in _ajdump revealed that the woven classes were indeed missing their StackMapTables. After much searching, we found out that the root cause was the fact that the ServiceMix bundle did not include the renamed ASM library (aj.org.objectweb.asm.* packages). We logged a bug for this here: https://issues.apache.org/jira/browse/SM-2744 It took us a long time to find this, because instead of throwing an Exception (ClassNotFoundException or similar), AspectJ simply decides to not generate StackMapTables, thus generating bytecode that is completely invalid for Java 1.7 or higher. The relevant code snippets: org.aspectj.weaver.bcel.LazyClassGen: if (((myGen.getMajor() == Constants.MAJOR_1_6 && world.shouldGenerateStackMaps()) || myGen.getMajor() > Constants.MAJOR_1_6) && AsmDetector.isAsmAround) { wovenClassFileData = StackMapAdder.addStackMaps(world, wovenClassFileData); } org.aspectj.weaver.bcel.asm.AsmDetector: public class AsmDetector { public static boolean isAsmAround; static { try { Class<?> reader = Class.forName(\"aj.org.objectweb.asm.ClassReader\"); Class<?> visitor = Class.forName(\"aj.org.objectweb.asm.ClassVisitor\"); Method m = reader.getMethod(\"accept\", new Class[] { visitor, Integer.TYPE }); isAsmAround = m != null;  catch (Exception e) { isAsmAround = false; } // System.out.println(isAsmAround?\"ASM detected\":\"No ASM found\"); } } As far as I can see, The LazyClassGen class should throw an Exception if myGen.getMajor() > Constants.MAJOR_1_6 && !AsmDetector.isAsmAround  Also, I think AsmDetector could be more lenient and also accept a regular (non-included) ASM. If I've seen correctly, the aj-prefixed inlined version is simply a renamed copy of ASM 5.0.4, so using the regular ASM should work. I'm also not sure why this ASM needs to be renamed and embedded in the first place, but there is probably a reason that I'm not aware of.";

		String q1Bug482990 = bug482990Title + " " + bug482990Description;
		String q2Bug482990 = bug482990Title;
		String q3Bug482990 = bug482990Description;



		String bug470658Title = "Corrupted Local Variable Table";

		String bug470658Description = "Created attachment 254594 [details] Zip containing all files to reproduce the issue Hi :-) I have found an issue during weaving, which can be reproduced by the attached files. The weaved class after running ajc has some corrupted Local Variable Tables, where some slots are missing. When opening the weaved class - com/crashlytics/android/v.class - and looking at it using Sublime for example, I could see the following: LocalVariableTable: Start  Length  Slot  Name   Signature 0     154     0  arg0   Lcom/crashlytics/android/internal/aq; 0     154     1  arg1   I 0     154     2  arg2   J 0     154     4  arg3   Ljava/lang/String; As seen above, Slot 3 is missing... This issue causes some problems with Android's build framework. To reproduce the problem please do the following: - Unzip the attached - Define a ASPECTJ_HOME env variable, or simply edit the run.cmd file - Execute run.cmd The result would be ajc weaving the class v.class, using FilesApsect.aj found under the src dir. And the weaved jar will contain a v.class with the problem. Thanks! Ariel";

		String q1Bug470658 = bug470658Title + " " + bug470658Description;
		String q2Bug470658 = bug470658Title;
		String q3Bug470658 = bug470658Description;



		String bug461323Title = "ClassFormatError \"invalid modifier 0x8\" when trying to make a default method @Loggable";

		String bug461323Description = "Created attachment 251258 [details] JUnit test case I am using JDK 1.8, jcabi-aspects 0.21.1 and AspectJ 1.8.5 in Eclipse Luna SR2. I build either in Eclipse or with the jcabi-maven-plugin 0.12. I have annotated a default method in an interface as @Loggable. After weaving, trying to load that class leads to an error: java.lang.BootstrapMethodError: java.lang.ClassFormatError: Method apply_aroundBody0 in class java8/aspectsjtest/TheInterface has illegal modifiers: 0x8 That's the \"static\" modifier. I have attached an example. Please compile and try to run java8.aspectsjtest.TheInterfaceTest as a JUnit 4 test. (The intent is to log all function invocations through TheInterface, so I override java.util.functions.Function#apply/1 (which is called in existing code) to delegate to a new abstract method, and annotate the apply-method.)";

		String q1Bug461323 = bug461323Title + " " + bug461323Description;
		String q2Bug461323 = bug461323Title;
		String q3Bug461323 = bug461323Description;



		String bug478003Title = "NullPointerException with generic inter type method declaration";

		String bug478003Description = "Created attachment 256733 [details] dump Below snippet produce NullPointerException. If I replace line public OrientDBValue OrientKey<T>.getOrientDBValue() with  public OrientDBValue OrientKey.getOrientDBValue() it compiles ok import com.flickbay.orientdb.OrientKey; public aspect OrientDBKeyIO { public interface IO<T> { OrientDBValue<T> getOrientDBValue(); } declare parents : OrientKey implements IO; public SimpleOrientDBValue OrientKey<T>.value = null; public OrientDBValue OrientKey<T>.getOrientDBValue() { return this.value; } }";

		String q1Bug478003 = bug478003Title + " " + bug478003Description;
		String q2Bug478003 = bug478003Title;
		String q3Bug478003 = bug478003Description;




		String bug484941Title = "NPE AnnotationDiscoveryVisitor";
		String bug484941Description = "Created attachment 258921 [details] Gradle project which produces the bug Getting a NPE in AnnotationDiscoveryVisitor when compiling a Test class: java.lang.NullPointerException at org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.AnnotationDiscoveryVisitor.resolveAnnotations(AnnotationDiscoveryVisitor.java:238) at org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.AnnotationDiscoveryVisitor.visit(AnnotationDiscoveryVisitor.java:217) at org.aspectj.org.eclipse.jdt.internal.compiler.ast.TypeDeclaration.traverse(TypeDeclaration.java:1348) at org.aspectj.org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration.traverse(CompilationUnitDeclaration.java:748) at org.aspectj.org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration.traverse(CompilationUnitDeclaration.java:709) at org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.RoundEnvImpl.<init>(RoundEnvImpl.java:60) at org.aspectj.org.eclipse.jdt.internal.compiler.apt.dispatch.BaseAnnotationProcessorManager.processAnnotations(BaseAnnotationProcessorManager.java:148) at org.aspectj.org.eclipse.jdt.internal.compiler.Compiler.processAnnotations(Compiler.java:924) at org.aspectj.org.eclipse.jdt.internal.compiler.Compiler.compile(Compiler.java:434) at org.aspectj.ajdt.internal.core.builder.AjBuildManager.performCompilation(AjBuildManager.java:1036) at org.aspectj.ajdt.internal.core.builder.AjBuildManager.performBuild(AjBuildManager.java:272) at org.aspectj.ajdt.internal.core.builder.AjBuildManager.batchBuild(AjBuildManager.java:185) at org.aspectj.ajdt.ajc.AjdtCommand.doCommand(AjdtCommand.java:114) at org.aspectj.ajdt.ajc.AjdtCommand.runCommand(AjdtCommand.java:60) at org.aspectj.tools.ajc.Main.run(Main.java:371) at org.aspectj.tools.ajc.Main.runMain(Main.java:248) at org.aspectj.tools.ajc.Main.main(Main.java:84) Comment 1 Andrew Clement CLA Friend 2016-01-05 16:09:23 EST";

		String q1Bug484941 = bug484941Title + " " + bug484941Description;
		String q2Bug484941 = bug484941Title;
		String q3Bug484941 = bug484941Description;








		//QUERIES FOR RHINO
		String rhinoBug774083Title = "Interactive shell line editing broken with recent JLine";
		String rhinoBug774083Description = "When using JLine in the interactive shell, the first left-movement action (either backspace or left arrow) results in moving left 4 characters before completing the action (so deleting a character using backspace results in the last 5 characters on the line being removed, including prompt characters - moving left results in moving the cursor left 5 characters).  This occurs in every terminal that I have tested in Linux. After investigating further, it appears that this behavior was introduced by https://github.com/jline/jline/commit/de5f3664425cbfeb683e7b0c8abd40f475444cbc and the behavior appears in all subsequent commits that I tested.  It appears to be a result of how the prompt is printed and that JLine doesn't realize additional characters have been printed; if I set the prompts to /\"/\" the problem disappears. One solution is to use jline.ConsoleReader.readLine(String) to print the prompt and read user input (with an appropriate bridge to System.in/out in the case JLine is not available).  Another might be to see if the JLine devs could provide a fix for the old behavior (since it looks like Bean Shell is broken in the same way).</description>";

		String rhinoBug688023Title = "Calling toSource on a parsed script containing if without braces not indented - sample attached";
		String rhinoBug688023Description = "Fix broken test cases which relied on the old (and erroneous) toSource() output";

		String rhinoBug685403Title = "finally block executed more than 1 time when using continuation";
		String rhinoBug685403Description = "Created attachment 559039 [details] Java code to run my example script code. User Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_6_8) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50 Steps to reproduce: this is my example code. function openFile() { java.lang.System.out.println(\"openFile\"); return new Object(); } function doSomethingWithFile(file) { java.lang.System.out.println(\"doSomethingWithFile\"); } function sendEventToOther() { java.lang.System.out.println(/\"sendEventToOther/\"); } function doSomethingWithEvent(file , event) { java.lang.System.out.println(/\"doSomethingWithEvent event = \" + event); } function closeFile(file) { java.lang.System.out.println(/\"closeFile/\"); } try { file = openFile(); doSomethingWithFile(file); sendEventToOther(); event = waitSomeEvent(); doSomethingWithEvent(file, event); } finally { closeFile(file); } waitSomeEvent() function throws ContinuationPending Exception, if there is no event in event queue. Actual results: this is output of above example script code. openFile doSomethingWithFile sendEventToOther closeFile doSomethingWithEvent event = hello world closeFile In this case, the finally block (closeFile()) is executed twice when after ContinuationPending Exception thrown and after cx.resumeContinuation() called. Expected results: finally block should be executed only once.";

		String rhinoBug773573Title = "ScriptRuntime.notFunctionError(Object,Object,String) uses wrong object representation for functions with object destructuring parameters";
		String rhinoBug773573Description = "User Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:13.0) Gecko/20100101 Firefox/13.0.1 Build ID: 20120614114901 Steps to reproduce: ScriptRuntime.notFunctionError(Object,Object,String) computes wrong function string representation for functions with object destructuring parameters.  Actual results: js> (function({a}) { return a }).foo() js: \"<stdin>\", line 10: uncaught JavaScript runtime exception: TypeError: Cannot find function foo in object function ({...}. at <stdin>:10 Expected results: Function string representation should not abbreviate the object destructuring parameter but only the actual function body. That means in this case: function ({a}) {...}";

		String rhinoBug780147Title = "VerifyError for --a[i]() or --a(i) with opt=9";
		String rhinoBug780147Description = "User Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:14.0) Gecko/20100101 Firefox/14.0.1 Build ID: 20120713134347 Steps to reproduce: Try to compile: --- function F () { var i=0, a=[]; --a[i]() } function G () { var i=0, a=[]; --a(i) --- Actual results: VerifyError thrown during class-file verification";

		String rhinoQ1Bug774083 = rhinoBug774083Title + " " + rhinoBug774083Description;
		String rhinoQ2Bug774083 = rhinoBug774083Title;
		String rhinoQ3Bug774083 = rhinoBug774083Description;

		String rhinoQ1Bug688023 = rhinoBug688023Title + " " + rhinoBug688023Description;
		String rhinoQ2Bug688023 = rhinoBug688023Title;
		String rhinoQ3Bug688023 = rhinoBug688023Description;

		String rhinoQ1Bug685403 = rhinoBug685403Title + " " + rhinoBug685403Description;
		String rhinoQ2Bug685403 = rhinoBug685403Title;
		String rhinoQ3Bug685403 = rhinoBug685403Description;

		String rhinoQ1Bug773573 = rhinoBug773573Title + " " + rhinoBug773573Description;
		String rhinoQ2Bug773573 = rhinoBug773573Title;
		String rhinoQ3Bug773573 = rhinoBug773573Description;

		String rhinoQ1Bug780147 = rhinoBug780147Title + " " + rhinoBug780147Description;
		String rhinoQ2Bug780147 = rhinoBug780147Title;
		String rhinoQ3Bug780147 = rhinoBug780147Description;

		//File[] files = new File("D:\\Users\\user1\\workspace\\MethodDocSplitter\\rhino_methods").listFiles();
		File[] files = new File("D:\\Users\\user1\\workspace\\MethodDocSplitter\\aspectj_methods").listFiles();
		StandardAnalyzer analyzer = new StandardAnalyzer();
		Directory index = new RAMDirectory();
		
		LuceneAnalyzer luceneAnalyzer = new LuceneAnalyzer(analyzer, index, files);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 482990");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(q1Bug482990, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(q1Bug482990, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(q1Bug482990, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 482990");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(q2Bug482990, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(q2Bug482990, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(q2Bug482990, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 482990");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(q3Bug482990, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(q3Bug482990, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(q3Bug482990, 20);

		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 470658");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(q1Bug470658, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(q1Bug470658, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(q1Bug470658, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 470658");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(q2Bug470658, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(q2Bug470658, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(q2Bug470658, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 470658");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(q3Bug470658, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(q3Bug470658, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(q3Bug470658, 20);

		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 461323");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(q1Bug461323, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(q1Bug461323, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(q1Bug461323, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 461323");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(q2Bug461323, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(q2Bug461323, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(q2Bug461323, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 461323");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(q3Bug461323, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(q3Bug461323, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(q3Bug461323, 20);

		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 478003");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(q1Bug478003, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(q1Bug478003, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(q1Bug478003, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 478003");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(q2Bug478003, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(q2Bug478003, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(q2Bug478003, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 478003");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(q3Bug478003, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(q3Bug478003, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(q3Bug478003, 20);

		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 484941");
		System.out.println("Query 1, 5 Hits");
		luceneAnalyzer.query(q1Bug484941, 5);
		System.out.println("Query 1, 10 Hits");
		luceneAnalyzer.query(q1Bug484941, 10);
		System.out.println("Query 1, 20 Hits");
		luceneAnalyzer.query(q1Bug484941, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 484941");
		System.out.println("Query 2, 5 Hits");
		luceneAnalyzer.query(q2Bug484941, 5);
		System.out.println("Query 2, 10 Hits");
		luceneAnalyzer.query(q2Bug484941, 10);
		System.out.println("Query 2, 20 Hits");
		luceneAnalyzer.query(q2Bug484941, 20);
		System.out.println("****************************************************");
		System.out.println("AspectJ Bug 484941");
		System.out.println("Query 3, 5 Hits");
		luceneAnalyzer.query(q3Bug484941, 5);
		System.out.println("Query 3, 10 Hits");
		luceneAnalyzer.query(q3Bug484941, 10);
		System.out.println("Query 3, 20 Hits");
		luceneAnalyzer.query(q3Bug484941, 20);
	}
}
