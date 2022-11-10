package Detection;

import java.util.ArrayList;

public class functionAction {
	String className;
	String functionName;
	ArrayList<String> passedArgs;
	ArrayList<String> locksAcquired;
	ArrayList<String> functionsCalled;

	public functionAction(String className, String functionName) {
		this.className = className;
		this.functionName = functionName;
		locksAcquired = new ArrayList<>();
		functionsCalled = new ArrayList<>();
		passedArgs = new ArrayList<>();
	}

	public void addLock(String lockName) {
		for (int i = 0; i < passedArgs.size(); i++) {
			if (lockName.equals(passedArgs.get(i))) {
				locksAcquired.add(i+"");
				return;
			}
		}
		locksAcquired.add(lockName);
	}

	public void addFunction(String function) {
		
		functionsCalled.add(function);
	}

	public void setArgs(String args) {
		String temp = args.replace("  ", " ");
		temp = temp.replace("\t", "");
	
		while (temp.indexOf(',') != -1) {
			passedArgs.add(temp.substring(temp.indexOf(' ')+1, temp.indexOf(',')));
			temp = temp.substring(temp.indexOf(',') + 1);
		}
		temp = temp.substring(temp.indexOf(',') + 1);
		if(temp.indexOf(' ')==0) {
			temp=temp.substring(1);
		}
		passedArgs.add(temp.substring(1+ temp.indexOf(' ')));
	}
}
