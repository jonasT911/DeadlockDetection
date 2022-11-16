package Detection;

import java.util.ArrayList;

public class LockNode {
String lockName;
String lockLocation;
int level;
ArrayList<LockNode> locksAcquiredWithin;
ArrayList<lockEdge> edges;
	public LockNode(String name, int level) {
		lockName=name;
		this.level=level;
		lockLocation="Undefined";
		locksAcquiredWithin=new ArrayList<LockNode>();
		edges=new ArrayList<lockEdge>();
	}
	public LockNode(String name, int level,String fileLocation,int lineNumber) {
		lockName=name;
		lockLocation=fileLocation+":"+lineNumber;
		this.level=level;
		locksAcquiredWithin=new ArrayList<LockNode>();
		edges=new ArrayList<lockEdge>();
	}
	
	public void addEdge(String startName, String startLocation,LockNode end) {
		
		lockEdge temp=new lockEdge(startName, end.lockName, startLocation, end.lockLocation);
		edges.add(temp);
	}
}
