package Detection;

import java.util.ArrayList;

public class functionAction {
	String className;
	String functionName;
	ArrayList<String> passedArgs;
	ArrayList<LockNode> locksAcquired;
	ArrayList<calledFunctions> functionsCalled;
	ArrayList<lockEdge> edgesMade;
	
	boolean visited=false;
	boolean runnable=false;

	public functionAction(String className, String functionName,boolean runnable) {
		this.className = className;
		this.functionName = functionName;
		locksAcquired = new ArrayList<>();
		functionsCalled = new ArrayList<>();
		passedArgs = new ArrayList<>();
		this.runnable =runnable;
		edgesMade=new ArrayList<lockEdge>();
	}

	public void addLock(LockNode lockName) {
		for (int i = 0; i < passedArgs.size(); i++) {
			if (lockName.lockName.equals(passedArgs.get(i))) {
				lockName.lockObj=i+"";
			//This is necessary for passed arguments, but for now I dont want it.
			}
		}
		System.out.println("add lock in function "+lockName.lockName);
		locksAcquired.add(lockName);
	}

	public void addFunction(calledFunctions function) {
		
		functionsCalled.add(function);
	}

	public void setArgs(String args) {
		String temp = args.replace("  ", " ");
		temp = temp.replace("\t", "");
	
		while (temp.indexOf(',') != -1) {
			String 	addString=temp.substring(0, temp.indexOf(','));
			System.out.println(addString);
			if(addString.contains(" ")) {
			addString=temp.substring(temp.indexOf(' ')+1, temp.indexOf(','));
	
			}
			passedArgs.add(addString);
			temp = temp.substring(temp.indexOf(',') + 1);
		}
		temp = temp.substring(temp.indexOf(',') + 1);
		if(temp.indexOf(' ')==0) {
			temp=temp.substring(1);
		}
		passedArgs.add(temp.substring(1+ temp.indexOf(' ')));
	}
}
