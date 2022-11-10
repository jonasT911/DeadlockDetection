package Detection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class master {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Start Deadlock Detection");
		int openBrackets = 0;
		functionAction funct = new functionAction("Fake", "Fake");
		String currentClass="";
		ArrayList<functionAction> thisProgram = new ArrayList<functionAction>();
		try {
			File myObj = new File("MainFile.java");
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();// Note, this will need to be change to key off of ; and '{' and not \r
						
				String tempClass=getClassName(data);
				if(tempClass!=null) {
					currentClass=tempClass;
				}
				if (isFunctionDefinition(data, openBrackets)) {
					funct = new functionAction(currentClass, getFunctionName(data));
				}

				if (data.contains("{")) {
					openBrackets++;
				}
				if (data.contains("}")) {
					openBrackets--;
					if (openBrackets == 1) {

						thisProgram.add(funct);
					}
				}
				System.out.println(data);

			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		for (int i = 0; i < thisProgram.size(); i++) {
			System.out.println(thisProgram.get(i).className + "." + thisProgram.get(i).functionName);
		}
	}

	static boolean isFunctionDefinition(String data, int openBrackets) {
		if (data.contains("(") && data.contains(")")) {
			if (openBrackets == 1) {
				return true;
			}

		}
		return false;
	}

	static String getFunctionName(String data) {

		int endIndex = data.indexOf('(');
		int beginIndex = endIndex - 1;
		while (data.charAt(beginIndex) == ' ') {
			beginIndex--;
			endIndex--;
		}
		while (data.charAt(beginIndex) != ' ') {
			beginIndex--;

		}
		return data.substring(beginIndex + 1, endIndex);
	}

	static String getClassName (String data){
		int beginIndex=data.indexOf("class");
		if(beginIndex==-1) {
			return null;
		}
		String temp= data.substring(beginIndex+6);
		return temp.substring(0, temp.indexOf(' '));
	}

}


