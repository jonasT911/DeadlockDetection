package Detection;

import java.util.ArrayList;

public class calledFunctions {
	String functionName;
	boolean isMultithreaded;
	ArrayList<LockNode> heldLocks;

	public calledFunctions(String name,boolean multithread) {
		functionName=name;
		isMultithreaded=multithread;
		heldLocks=new ArrayList<LockNode>();
	}
}
