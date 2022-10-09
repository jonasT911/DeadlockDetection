package Detection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class master {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Start Deadlock Detection");
		
		   try {
			      File myObj = new File("Input.txt");
			      Scanner myReader = new Scanner(myObj);
			      while (myReader.hasNextLine()) {
			        String data = myReader.nextLine();
			        System.out.println(data);
			      }
			      myReader.close();
			    } catch (FileNotFoundException e) {
			      System.out.println("An error occurred.");
			      e.printStackTrace();
			    }
		
	}

}
