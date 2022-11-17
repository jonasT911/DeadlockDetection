package Detection;

public class lockEdge {
	String startingLock;
	String endingLock;

	String startingLocation;
	String endingLocation;

	public lockEdge(LockNode startingLock, LockNode endingLock) {
		this.startingLock = startingLock.lockName;
		this.endingLock = endingLock.lockName;
		this.startingLocation = startingLock.lockLocation;
		this.endingLocation = endingLock.lockLocation;

	}

	@Override
	public String toString() {
		return "Lock " + startingLock + " at " + startingLocation + "and lock " + endingLock + " at " + endingLocation;

	}
}
