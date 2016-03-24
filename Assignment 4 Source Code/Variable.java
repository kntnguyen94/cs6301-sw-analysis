package seers.astvisitortest;

public class Variable {
	private int lineNumber;
	private String var;
	private String fieldOrVariable;
	
	public Variable(int num, String var, String foV) {
		lineNumber = num;
		this.var = var;
		fieldOrVariable = foV;
	}
	
	public int getLineNum() {
		return lineNumber;
	}
	
	public String getVariableName() {
		return var;
	}
	
	public String isFieldOrVariable() {
		return fieldOrVariable;
	}
	
	public String toString() {
		return "[" + var + "]";
	}
}
