package Detection;

import java.util.ArrayList;

public class LockNode {
String lockName;
String lockLocation;
int level;
ArrayList<LockNode> heldLocks;
	public LockNode(String name, int level) {
		lockName=name;
		this.level=level;
		lockLocation="Undefined";
		heldLocks=new ArrayList<LockNode>();
	}
	public LockNode(String name, int level,String fileLocation,int lineNumber) {
		lockName=name;
		lockLocation=fileLocation+":"+lineNumber;
		this.level=level;
		heldLocks=new ArrayList<LockNode>();
	}
}
