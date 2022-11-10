package Detection;

import java.util.ArrayList;

public class functionAction {
	String className;
	String functionName;
	ArrayList<String> locksAcquired;
	ArrayList<String> functionsCalled;

	public functionAction(String className, String functionName) {
		this.className=className;
		this.functionName=functionName;
		locksAcquired = new ArrayList<>();
		functionsCalled = new ArrayList<>();
	}

	public void addLock(String lockName) {
		locksAcquired.add(lockName);
	}

	public void addFunction(String function) {
		functionsCalled.add(function);
	}
}
