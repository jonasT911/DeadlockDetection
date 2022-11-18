package DeadlockPackage;

public class MainFile implements Runnable {
	private static int sum = 0;

	static Object lock1 = new Object();
	static Object lock2 = new Object();
	static Object c = new Object();
	final int repetitions = 10000;

	public void garbage(Object A, Object B) {
		synchronized (A) {
			foo(A, B);
		}
	}

	public void foo(Object A, Object B) {
		synchronized (A) {
			synchronized (B) {
				synchronized (c) {
					for (int i = 0; i < repetitions; i++) {
						sum++;

					}
				}
			}
		}
	}

	public void bar(Object A, Object B) {
		synchronized (B) {
			synchronized (c) {
				synchronized (A) {
					for (int i = 0; i < repetitions; i++) {
						sum--;

					}
				}
			}
		}
	}

	public int getSum(Object A) {
		synchronized (A) {
			return sum;
		}
		synchronized (B) {
			System.out.println(sum);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(getSum(lock1));
		for (int x = 0; x < 2; x++) {
			foo(lock1, lock2);
			bar(lock1, lock2);
		}

	}
}
