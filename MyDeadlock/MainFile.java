package DeadlockPackage;

public class MainFile implements Runnable {
	private static int sum = 0;

	static Object lock1 = new Object();
	static Object lock2 = new Object();
	static Object lock3 = new Object();
	final int repetitions = 10000;

	public void garbage(Object A, Object B) {
		AnotherClass var = new AnotherClass();
		synchronized (A) {
			var.outside(B, sum);
		}
	}

	public void foo(Object A, Object B) {
		synchronized (A) {
			synchronized (B) {
				for (int i = 0; i < repetitions; i++) {
					sum++;
					foo(A,B);
				}
			}

		}
	}

	public void bar(Object A, Object B) {
		synchronized (B) {
			synchronized (A) {
				for (int i = 0; i < repetitions; i++) {
					sum--;

				}
			}
		}
	}

	public int getSum(Object A) {
		synchronized (A) {
			return sum;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		for (int x = 0; x < 2; x++) {
			//bar(lock1, lock2);
			foo(lock1, lock2);
			
			
		}
		System.out.println(getSum(lock1));
	}
}
