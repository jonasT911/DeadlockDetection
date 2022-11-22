package Detection;

import java.util.ArrayList;

public class LockNode {
String lockName;
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
	}
	public LockNode(String name, int level,String fileLocation,int lineNumber) {
		lockName= name.replace(" ", "");
		lockLocation=fileLocation+":"+lineNumber;
		this.level=level;
		locksAcquiredWithin=new ArrayList<LockNode>();
		edges=new ArrayList<lockEdge>();
	}

}
