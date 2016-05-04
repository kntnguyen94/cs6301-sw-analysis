import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

/**
 * General visitor that extracts methods and fields of a Java compilation unit
 *
 */
public class GeneralVisitor extends ASTVisitor {

	// List of method names and bodies
	private List<String> methodNames;
	private List<String> methodBodies;
	
	private List<String> methodInvoArgs;

	// List of instance variables
	private List<String> instanceVariables;
	
	// List of public static variables
	private List<String> staticVars;
	
	// Assignments
	private List<String> rightAssignments;
	
	// compilation unit
	private CompilationUnit compilationUnit;
	
	/**
	 * Default constructor
	 */
	public GeneralVisitor(CompilationUnit compUnit) {
		methodNames = new ArrayList<String>();
		methodBodies = new ArrayList<String>();
		instanceVariables = new ArrayList<>();
		staticVars = new ArrayList<>();
		rightAssignments = new ArrayList<>();
		methodInvoArgs = new ArrayList<>();
		
		compilationUnit = compUnit;
	}
	
	@Override
	public boolean visit(MethodInvocation node) {
		List<Expression> args = node.arguments();
		for(Expression exp : args) {
			methodInvoArgs.add(exp.toString());
			}
		return super.visit(node);
	}
	
	@Override
	public boolean visit(Assignment node) {
		
		int lineNumber = 0;
		try {
		lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		} catch (NullPointerException e) {
			System.out.println("NPE on finding line number VarDeclStatement");
		}
		String left = node.getLeftHandSide().toString();
		String right = node.getRightHandSide().toString();
		
		//check whihc type of expression it is
		//check left reads
			
		//check right reads
		
		//assignments.add("L (" + lineNumber + "): " + left);
		//assignments.add("R (" + lineNumber + "): " + right);
		rightAssignments.add(left + " " + right);
		//readVars.add(right);
		return super.visit(node);
	}
	
	public List<String> getRightAssignments() {
		return rightAssignments;
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
				if (!Modifier.isPrivate(modifiers) && Modifier.isStatic(modifiers)) {
				    // add public static variable
					staticVars.add(fragment.getName().getFullyQualifiedName());
				}
				try {
					//System.out.println(fragment.getName().getFullyQualifiedName() + " initialized with: " + fragment.getInitializer().toString());
					rightAssignments.add(fragment.toString());
					
				} catch (NullPointerException e) {
						//System.out.println("Variable no initializer for " + fragment.getName().getFullyQualifiedName());
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
	
	public List<String> getStaticVars() {
		return staticVars;
	}
	
	public List<String> getMethodInvoArgs() {
		return methodInvoArgs;
	}
}