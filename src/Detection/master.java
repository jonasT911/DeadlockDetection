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
		String currentClass = "";
	

		File directoryPath = new File(System.getProperty("user.dir"));
		// List of all files and directories
		String contents[] = directoryPath.list();
		System.out.println("List of files and directories in the specified directory:");
		for (int j = 0; j < contents.length; j++) {
			System.out.println(contents[j]);
			if (contents[j].contains(".java")) {
				ArrayList<functionAction> thisProgram = new ArrayList<functionAction>();
				//Begin reading the file
				try {
					File myObj = new File(contents[j]);
					Scanner myReader = new Scanner(myObj);
					while (myReader.hasNextLine()) {
						String data = myReader.nextLine();// Note, this will need to be change to key off of ; and {
															// and not \r
						int commentIndex = data.indexOf("//");
						if (commentIndex != -1) {
							data = data.substring(0, commentIndex);
						}
						String tempClass = getClassName(data);
						if (tempClass != null) {
							currentClass = tempClass;
						}
						if (isFunctionDefinition(data, openBrackets)) {

							funct = new functionAction(currentClass, getFunctionName(data));
							funct.setArgs(data.substring(data.indexOf('(') + 1, (data.indexOf(')'))));
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
						if (data.contains("synchronized")) {
							funct.addLock(data.substring(data.indexOf('(') + 1, data.indexOf(')')));
						}
						String tempFunction = getFunctionCall(data);
						if (tempFunction != null) {
							funct.addFunction(tempFunction);
						}
						//System.out.println(data);

					}
					myReader.close();
				} catch (FileNotFoundException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}

				System.out.println("\n\nThisClass");
				for (int i = 0; i < thisProgram.size(); i++) {
					System.out.println(thisProgram.get(i).className + "." + thisProgram.get(i).functionName);
					System.out.println(thisProgram.get(i).locksAcquired);
					System.out.println(thisProgram.get(i).functionsCalled);
					System.out.println(thisProgram.get(i).passedArgs);
				}
			}
		}
	}

	static boolean isFunctionDefinition(String data, int openBrackets) {
		if (data.contains("(") && data.contains(")") && data.contains("{")) {
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

	static String getClassName(String data) {
		int beginIndex = data.indexOf("class");
		if (beginIndex == -1) {
			return null;
		}
		String temp = data.substring(beginIndex + 6);
		return temp.substring(0, temp.indexOf(' '));
	}

	static String getFunctionCall(String data) {
		if (!(data.contains("(") && data.contains(")") && data.contains(";"))) {
			return null;
		}
		String temp = data.replace(" ", "");
		temp = temp.replace("\t", "");
		// Maybe to work on non static I could have a mapping of strings to the parent
		// class.
		temp = temp.substring(0, temp.indexOf('(')); // Only works on static functions. Does not work on nested calls.
		if (temp.equals("for")) {
			return null;
		}
		return temp;
	}
}
