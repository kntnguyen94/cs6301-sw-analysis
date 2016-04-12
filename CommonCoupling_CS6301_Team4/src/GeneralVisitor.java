import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * General visitor that extracts methods and fields of a Java compilation unit
 *
 */
public class GeneralVisitor extends ASTVisitor {

	// List of method names and bodies
	private List<String> methodNames;
	private List<String> methodBodies;

	// List of instance variables
	private List<String> instanceVariables;
	
	// List of public static variables
	private List<String> publicStaticVars;
	
	// compilation unit
	private CompilationUnit compilationUnit;
	
	/**
	 * Default constructor
	 */
	public GeneralVisitor(CompilationUnit compUnit) {
		methodNames = new ArrayList<String>();
		methodBodies = new ArrayList<String>();
		instanceVariables = new ArrayList<>();
		publicStaticVars = new ArrayList<>();
		
		compilationUnit = compUnit;
	}

	/**
	 * Method that visits the method declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		// add the name of the method
		methodNames.add(node.getName().getFullyQualifiedName());
		
		// check to see if the method has a body
		if (node.getBody() != null) {
			// add the method body
			methodBodies.add(node.getBody().toString());
		}
		else {
			// if null, add an empty string as the method body
			methodBodies.add("");
		}
		return super.visit(node);	
	} 

	/**
	 * Method that visits the field declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.FieldDeclaration)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration node) {
		// get the fragments of the field declaration
		List<VariableDeclarationFragment> varFragments = node.fragments();
		for (VariableDeclarationFragment fragment : varFragments) {
			instanceVariables.add(fragment.getName().getFullyQualifiedName());
			try {
				int modifiers = node.getModifiers();
				// check if the variable is public and static
				if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)) {
				    // add public static variable
					publicStaticVars.add(fragment.getName().getFullyQualifiedName());
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		return super.visit(node);
	}
	
	public void setCompUnit(CompilationUnit compUnit) {
		compilationUnit = compUnit;
	}
	
	public List<String> getMethodNames() {
		return methodNames;
	}
	
	public List<String> getMethodBodies() {
		return methodBodies;
	}

	public List<String> getInstanceVariables() {
		return instanceVariables;
	}
	
	public List<String> getPublicStaticVars() {
		return publicStaticVars;
	}
}