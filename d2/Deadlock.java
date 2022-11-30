/**
 * The Deadlock example creates two threads.  Each thread
 * attempts to get two locks and then release them.  In this
 * example, the locks are given in reverse order to the threads
 * so a deadlock could happen.  To fix this behaviour, the locks should
 * be given in the same order.
 *
 * @author Todd Wallentine &lt;tcw@cis.ksu.edu&gt;
 * @version $Revision: 1.1.1.1 $ - $Date: 2003/10/21 21:00:42 $
 */
public class Deadlock {

    public static void main(String[] args) {
	Object lock1 = new Object();
	Object lock2 = new Object();

	Client client1 = new Client(lock1, lock2);
	Client client2 = new Client(lock2, lock1);

	// to fix this, use the following instead.
	//Client client2 = new Client(lock1, lock2);

	client1.start();
	client2.start();
    }
}

class Client extends Thread {

    public Object lock1;
    public Object lock2;

    public Client(Object lock1, Object lock2) {
	this.lock1 = lock1;
	this.lock2 = lock2;
	
    }

    public void run() {
	synchronized(lock1) {
	    synchronized(lock2) {
	    }
	}
    }
}
