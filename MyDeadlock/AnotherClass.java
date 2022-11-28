package DeadlockPackage;

public class AnotherClass {

	final int repetitions = 2;
	public void outside( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;

				}
			}
		System.out.print("hello");
		
	}
}
