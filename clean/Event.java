/**
 * @author Xuan
 *  Created on Feb 27, 2005
 */
public class Event {
    public int count = 0;

    public synchronized void waitForEvent(int remote_count) {
	if (remote_count == count)
	  try {
	  	wait();
	  }catch(InterruptedException e) {};
    }

    public synchronized void signal_event() {
        count = (count + 1) % 100;
        notifyAll();
    }
}
