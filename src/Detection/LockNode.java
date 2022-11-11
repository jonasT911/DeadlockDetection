package Detection;

import java.util.ArrayList;

public class LockNode {
String lockName;
int level;
ArrayList<LockNode> heldLocks;
	public LockNode(String name, int level) {
		lockName=name;
		this.level=level;
		heldLocks=new ArrayList<LockNode>();
	}
}
