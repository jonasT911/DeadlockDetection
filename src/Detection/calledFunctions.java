package Detection;

import java.util.ArrayList;

public class calledFunctions {
	String functionName;
	String className;
	boolean isMultithreaded;
	ArrayList<LockNode> heldLocks;
	String argsPassed;

	public calledFunctions(String name,String className,boolean multithread) {
		functionName=name;
		isMultithreaded=multithread;
		heldLocks=new ArrayList<LockNode>();
		this.className=className;
	}
	
	@Override
	public String toString() {
		return className+"."+functionName;
	}
}
