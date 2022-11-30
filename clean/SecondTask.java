/**
 * @author Xuan
 * Created on Apr 27, 2005
 */
public class SecondTask extends Thread {
    int iterations;
    Event event1,event2;

    public SecondTask(Event e1, Event e2,int iterations) {
        this.event1 = e1;
        this.event2 = e2;
        this.iterations=iterations;
    }

    public void run() {
        int count = 0;
        count = event2.count;
        for (int i=0;i<iterations;i++) {    
	      event1.signal_event();
	      event2.waitForEvent(count);
	      count = event2.count;
        }
    }
}
