/**
 *  @author Xuan
 *  Created on Apr 27, 2005
 */
public class FirstTask extends Thread {
    int iterations;
    Event event1,event2;

    public FirstTask(Event e1, Event e2,int iterations) {
	this.event1 = e1;
	this.event2 = e2;
	this.iterations=iterations;
    }

   public void run() {
	int count = 0;
        count = event1.count;
        for (int i=0;i<iterations;i++) {
	      event1.waitForEvent(count);
	      count = event1.count;
	      event2.signal_event();
        }
   }
}
