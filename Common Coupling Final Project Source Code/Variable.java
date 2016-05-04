//Used to compare/ count how many times a static var is mentioned in external files
//to create a top 10 list
import java.util.Comparator;

public class Variable implements Comparator<Variable>, Comparable<Variable>{

	private int counter;
	private String name;
	
	Variable() {
		
	}
	public Variable(String n, int a)
	{
		name = n;
		counter = a;
	}
	@Override
	public int compareTo(Variable v) {
		return Integer.compare(this.counter, v.counter);
	}

	@Override
	public int compare(Variable arg0, Variable arg1) {
		return arg1.counter - arg0.counter;
	}
	
	@Override public String toString() {
		return counter + " external files used global variable " + name;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public String getName() {
		return name;
	}
}
