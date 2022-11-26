package DeadlockPackage;


import org.junit.Test;


public class DeadlockTest {
	
static final int numThreads=5;
	@Test
	public void test1() {
		Thread list[] = new Thread[numThreads];
		for (int i = 0; i < numThreads; i++) {
			Thread t = new Thread(new MainFile());
			list[i] = t;
			t.start();
		}

		for (Thread thread : list) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Finished");
		
	}


}
