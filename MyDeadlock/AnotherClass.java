package DeadlockPackage;

public class AnotherClass {

	final int repetitions = 2;
	public void outside( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;
					//ab(B,sum);

				}
			}
		System.out.print("hello");
		
	}
	
	public void ab( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;
					cd(B,sum);

				}
			}
		System.out.print("hello");
		
	}
	
	public void cd( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;
					ef(B,sum);

				}
			}
		System.out.print("hello");
		
	}
	
	public void ef( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;
					hi(B,sum);

				}
			}
		System.out.print("hello");
		
	}
	
	
	public void hi( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;
					jk(B,sum);

				}
			}
		System.out.print("hello");
		
	}
	
	public void jk( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;
					lm(B,sum);

				}
			}
		System.out.print("hello");
		
	}
	
	public void lm( Object B, int sum) {
		synchronized (B) {
		
				for (int i = 0; i < repetitions; i++) {
					sum++
					;
					outside(B,sum);

				}
			}
		System.out.print("hello");
		
	}
}
