package Detection;

import java.util.ArrayList;

public class LockNode {
String lockName;
String lockObj;
String lockLocation;
int level;
ArrayList<LockNode> locksAcquiredWithin;
ArrayList<lockEdge> edges;
	public LockNode(String name, int level) {
		
		lockName= name.replace(" ", "");
		this.level=level;
		lockLocation="Undefined";
		locksAcquiredWithin=new ArrayList<LockNode>();
		edges=new ArrayList<lockEdge>();
		lockObj=lockName; 
	}
	public LockNode(String name, int level,String fileLocation,int lineNumber) {
		lockName= name.replace(" ", "");
		lockLocation=fileLocation+":"+lineNumber;
		this.level=level;
		locksAcquiredWithin=new ArrayList<LockNode>();
		edges=new ArrayList<lockEdge>();
		lockObj=lockName; 
	}

	public  LockNode(LockNode copy) {
		lockName= copy.lockName;
		lockLocation=copy.lockLocation;
		this.level=copy.level;
		locksAcquiredWithin=copy.locksAcquiredWithin;
		edges=copy.edges;
		lockObj=copy.lockObj; 
	}
	
	public String toString() {
		return "Lock: " + lockName +" lock_object: "+lockObj+" at: "+lockLocation+" hash: " + this.hashCode();

	}
}
