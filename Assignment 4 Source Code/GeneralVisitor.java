package seers.astvisitortest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

/**
 * General visitor that extracts methods and fields of a Java compilation unit
 *
 */
public class GeneralVisitor extends ASTVisitor {

	/**
	 * List of methods
	 */
	private List<String> methods;
	/**
	 * List of fields
	 */
	private List<String> fields;
	/**
	 *  List of parameter types
	 */
	private List<String> parameterType;
	private List<String> parameters;
	private List<String> test;
	private List<Variable> variables;
	
	private List<String> assignments;
	private List<String> leftAssignments;
	private List<String> rightAssignments;
	private CompilationUnit compilationUnit;
	private List<String> readVars;
	private List<String> fieldAccess;
	private List<String> returnStatements;
	private List<String> methodInvocationArguments;
	private List<String> methodExp;
	/**
	 * Default constructor
	 */
	public GeneralVisitor(CompilationUnit compUnit) {
		methods = new ArrayList<String>();
		fields = new ArrayList<>();
		parameterType = new ArrayList<>();
		parameters = new ArrayList<>();
		test = new ArrayList<>();
		assignments = new ArrayList<>();
		leftAssignments = new ArrayList<>();
		rightAssignments = new ArrayList<>();
		readVars = new ArrayList<>();
		fieldAccess = new ArrayList<>();
		returnStatements = new ArrayList<>();
		variables = new ArrayList<Variable>();
		methodInvocationArguments = new ArrayList<>();
		methodExp = new ArrayList<>();
		compilationUnit = compUnit;
	}

	/**
	 * Method that visits the method declarations of the AST
	 * 
	 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.MethodDeclaration)
	 */
	@Override
	public boolean visit(MethodDeclaration node) {

		// add the name of the method to the list
		SimpleName name = node.getName();
		methods.add(name.getFullyQualifiedName());
		String entry = "M: " + name.getFullyQualifiedName() + " (";
		for (Object parameter : node.parameters()) {
            VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
            String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY).toString();
            String param = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.NAME_PROPERTY).toString();
            parameterType.add(type);
            parameters.add(param);
            entry += param + ":" + type + ", ";
        }
		if (entry != null && entry.length() > 2 && entry.contains(",")) {
		      entry = entry.substring(0, entry.length()-2);
		    }
		entry += ")";
		test.add(entry);
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
		int lineNumber = 0;
		try {
		lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		} catch (NullPointerException e) {
			//System.out.println("NPE on finding line number FieldDec");
		}
		for (VariableDeclarationFragment fragment : varFragments) {
			String type = node.getType().toString();
			fields.add("F (" + lineNumber + "): " + fragment.getName().getFullyQualifiedName() + ":" +  type);
			variables.add(new Variable(lineNumber, fragment.getName().getFullyQualifiedName(), "field"));
			try {
				//System.out.println(fragment.getName().getFullyQualifiedName() + " initialized with: " + fragment.getInitializer().toString());
				readVars.add(fragment.getInitializer().toString());
			} catch (NullPointerException e) {
				//System.out.println("Field no initializer for " + fragment.getName().getFullyQualifiedName());
			}
		}
		
		return super.visit(node);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(VariableDeclarationStatement node) {
		List<VariableDeclarationFragment> varFragments = node.fragments();
		int lineNumber = 0;
		try {
		lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		} catch (NullPointerException e) {
			System.out.println("NPE on finding line number VarDeclStatement");
		}
		for (VariableDeclarationFragment fragment : varFragments) {
			String type = node.getType().toString();
			fields.add("V (" + lineNumber + "): " + fragment.getName().getFullyQualifiedName() + ":" +  type);
			variables.add(new Variable(lineNumber, fragment.getName().getFullyQualifiedName(), "variable"));
			try {
				//System.out.println(fragment.getName().getFullyQualifiedName() + " initialized with: " + fragment.getInitializer().toString());
				rightAssignments.add(fragment.getInitializer().toString());
			} catch (NullPointerException e) {
					//System.out.println("Variable no initializer for " + fragment.getName().getFullyQualifiedName());
				}
		}
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ReturnStatement node) {
		try {
			returnStatements.add(node.getExpression().toString());
		}
		catch (Exception e) {
			
		}
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldAccess node) {
		int lineNumber = 0;
		try {
		lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		} catch (NullPointerException e) {
			//System.out.println("NPE on finding line number FieldAccess");
		}
		String fieldName = node.getName().getFullyQualifiedName().toString();
		fieldAccess.add("Field Access (" + lineNumber + "): " + fieldName);
		return super.visit(node);
	}
	
	public boolean visit(MethodInvocation node) {
		int lineNumber = 0;
		try {
		lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		} catch (NullPointerException e) {
			System.out.println("NPE on finding line number MethodInvocation");
		}
		try {
			methodExp.add(node.getExpression().toString());
		} catch (Exception e) {}
		
		List<Expression> arguments = node.arguments();
		for (Expression args : arguments) {
			String argString = args.toString();
			methodInvocationArguments.add(argString);
		}
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ArrayAccess node) {
		int lineNumber = 0;
		try {
		lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		} catch (NullPointerException e) {
			System.out.println("NPE on finding line number FieldAccess");
		}
		
		String fieldName = node.getArray().toString();
		fieldAccess.add(fieldName);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperFieldAccess node) {
		int lineNumber = 0;
		try {
		lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
		} catch (NullPointerException e) {
			System.out.println("NPE on finding line number FieldAccess");
		}
		
		String fieldName = node.getName().getFullyQualifiedName().toString();
		fieldAccess.add("Super Field Access (" + lineNumber + "): " + fieldName);
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
		leftAssignments.add(left);
		rightAssignments.add(right);
		//readVars.add(right);
		return super.visit(node);
	}
	
	public void setCompUnit(CompilationUnit compUnit) {
		compilationUnit = compUnit;
	}
	public List<String> getMethods() {
		return methods;
	}

	public List<String> getFields() {
		return fields;
	}
	
	public List<String> getParameters() {
		return parameters;
	}
	
	public List<Variable> getVariables() {
		return variables;
	}
	
	public List<String> getParameterType() {
		return parameterType;
	}

	public List<String> getTest() {
		return test;
	}
	
	public List<String> getAssignments() {
		return assignments;
	}
	
	public List<String> getLeftAssignments() {
		return leftAssignments;
	}
	
	public List<String> getRightAssignments() {
		return rightAssignments;
	}
	
	public List<String> getReturnStatements() {
		return returnStatements;
	}
	
	public List<String> getReadVars() {
		return readVars;
	}
	
	public List<String> getFieldAccess() {
		return fieldAccess;
	}
	
	public List<String> getMethodInvocationArguments() {
		return methodInvocationArguments;
	}
	
	public List<String> getMethodExp() {
		return methodExp;
	}
	
	//A read is any use of a variable/ field that is not a write 
	public boolean isRead(String var, String lhs) {
		if (isVariableWrite(var, lhs) || isFieldWrite(var, lhs)) {
			return false;
		}
		else return true;
	}
	
	//A variable write occurs when a variable is directly used on the left-hand side of an assignment.
	private boolean isVariableWrite(String var, String lhs) {
		String res;
		//idk was thinking about x.y.z = 5 scenario
		if(lhs.contains(".")) {
			int lastIndex = lhs.lastIndexOf(".");
			res = lhs.substring(lastIndex + 1).trim();
		}
		if(lhs.contains(var)) {
			return true;
		}
		else return false;
	}
	
	//A field write occurs when it is dereferenced in the outermost expression on the left-hand of an assignment.
	private boolean isFieldWrite(String var, String lhs) {
		return false;
	}
}