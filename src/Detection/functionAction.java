package Detection;

import java.util.ArrayList;

public class functionAction {
	String className;
	String functionName;
	ArrayList<String> passedArgs;
	ArrayList<String> argsMapping;
	ArrayList<LockNode> locksAcquired;
	ArrayList<calledFunctions> functionsCalled;
	ArrayList<lockEdge> edgesMade;
	
	int visited=0;
	boolean runnable=false;

	public functionAction(String className, String functionName,boolean runnable) {
		this.className = className;
		this.functionName = functionName;
		locksAcquired = new ArrayList<>();
		functionsCalled = new ArrayList<>();
		passedArgs = new ArrayList<>();
		argsMapping = new ArrayList<>();
		this.runnable =runnable;
		edgesMade=new ArrayList<lockEdge>();
	}

	public void addLock(LockNode lockName) {
		//System.out.println("Passed args "+passedArgs);
		for (int i = 0; i < passedArgs.size(); i++) {
			if (lockName.lockName.equals(passedArgs.get(i))) {
				lockName.lockObj=i+"";
			//This is necessary for passed arguments, but for now I dont want it.
			}
		}
		//System.out.println("Function "+ functionName+" add lock: "+lockName);
		locksAcquired.add(lockName);
	}

	public void addFunction(calledFunctions function) {
		
		functionsCalled.add(function);
	}
	
	public void updatePropagatedArgs(functionAction currentFunction) {
		for(int i=0; i<passedArgs.size();i++) {
			for(int j=0; j<currentFunction.argsMapping.size();j++) {
				System.out.println("Try Swap args "+currentFunction.passedArgs.get(j)+ " and "+passedArgs.get(i));
				if(passedArgs.get(i).equals(currentFunction.argsMapping.get(j))) {
					System.out.println("Swap args "+currentFunction.passedArgs.get(j)+ " and "+passedArgs.get(i));
				//	passedArgs.remove(i);
					passedArgs.set(i,currentFunction.passedArgs.get(j));
					
				}
			}
		}
	}

	public void setArgs(String args) {
		
		argsMapping=passedArgs;
		passedArgs = new ArrayList<>();
		String temp = args.replace("  ", " ");
		temp = temp.replace("\t", "");
		while(temp.length()>0 && temp.charAt(0)==' ') {
			temp=temp.substring(1);
			//System.out.println(temp);
		}
		while (temp.indexOf(',') != -1) {
			String 	addString=temp.substring(0, temp.indexOf(','));
			//System.out.println(addString);
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
