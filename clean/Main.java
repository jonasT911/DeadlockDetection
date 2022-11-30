/**
 * @author Xuan
 * Created on Feb 27, 2005
 * 
 * Test Case 1 
   number of first task  :  1
   number of second task :  1
 */
public class Main {
    static int iFirstTask=1;
    static int iSecondTask=1;
    static int iterations=12;

    public void run() {
        Event new_event1 = new Event();
        Event new_event2 = new Event();
	    
	for (int i=0;i<iFirstTask;i++)
	   new FirstTask(new_event1,new_event2,iterations).start();
	for (int i=0;i<iSecondTask;i++)
	   new SecondTask(new_event1,new_event2,iterations).start();
    }
	
    public static void main(String[] args) {
      if (args.length < 3){
	System.out.println("ERROR: Expected 3 parameters");   
      }else{
	iFirstTask = Integer.parseInt(args[0]);
	iSecondTask = Integer.parseInt(args[1]);
	iterations = Integer.parseInt(args[2]);
	Main t= new Main();
	t.run();
      }
   }
}
