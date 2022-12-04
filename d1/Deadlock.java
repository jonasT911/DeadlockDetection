public class Deadlock {
    static Lock lock1;
    static Lock lock2;
    static int  state; 

    public static void main(String[] args) {
        lock1 = new Lock();
        lock2 = new Lock();
        Process1 p1 = new Process1();
        Process2 p2 = new Process2();
        p1.start();
        p2.start();
    }
}

class Process1 extends Thread {
    public void run() {
        Deadlock.state++;
        synchronized (Deadlock.lock1) {
            synchronized (Deadlock.lock2) {
                Deadlock.state++;
            }
        }
    }
}

class Process2 extends Thread {
    public void run() {
        Deadlock.state++;
        synchronized (Deadlock.lock2) {
            synchronized (Deadlock.lock1) {
                Deadlock.state++;
            }
        }
    }
}

class Lock {
}
