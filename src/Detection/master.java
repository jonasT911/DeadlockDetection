package Detection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class master {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean runnable = false;
		System.out.println("Start Deadlock Detection");
		int openBrackets = 0;
		functionAction funct = new functionAction("Fake", "Fake", false);
		functionAction mainFunction = new functionAction("Fake", "Fake", false);
		ArrayList<String> classList = new ArrayList<String>();
		String currentClass = "";
		int lineNumber = 0;

		ArrayList<LockNode> locksCurrentlyHeld = new ArrayList<LockNode>();

		// STEP 1: Create list of all functions, as well as the functions called and the
		// locks acquired.
		ArrayList<functionAction> thisProgram = new ArrayList<functionAction>();
		File directoryPath = new File(System.getProperty("user.dir"));
		// List of all files and directories
		String contents[] = directoryPath.list();
		System.out.println("List of files and directories in the specified directory:");
		for (int j = 0; j < contents.length; j++) {
			// System.out.println(contents[j]);
			if (contents[j].contains(".java")) {

				// Begin reading the file
				try {
					File myObj = new File(contents[j]);
					Scanner myReader = new Scanner(myObj);
					locksCurrentlyHeld.clear();
					lineNumber = 0;
					while (myReader.hasNextLine()) {
						lineNumber++;
						String data = myReader.nextLine();// Note, this will need to be change to key off of ; and {
															// and not \r
						int commentIndex = data.indexOf("//");
						if (commentIndex != -1) {
							data = data.substring(0, commentIndex);
						}
						String tempClass = getClassName(data, runnable);
						if (tempClass != null) {
							currentClass = tempClass;
							classList.add(currentClass);
							if (data.contains("Runnable")) {
								runnable = true;
							} else {
								runnable = false;
							}
						}
						if (isFunctionDefinition(data, openBrackets)) {

							funct = new functionAction(currentClass, getFunctionName(data), runnable);
							funct.setArgs(data.substring(data.indexOf('(') + 1, (data.indexOf(')'))));
							// funct.locksAcquired.addAll(locksCurrentlyHeld);
						}

						if (data.contains("{")) {
							openBrackets++;
						}
						if (data.contains("}")) {

							openBrackets--;
							// System.out.println("brack "+openBrackets);
							if (locksCurrentlyHeld.size() != 0) {
								if (locksCurrentlyHeld.get(locksCurrentlyHeld.size() - 1).level >= openBrackets) {
									locksCurrentlyHeld.remove(locksCurrentlyHeld.size() - 1);

									// System.out.println("Removing lock "+temp.lockName);
									// System.out.println(locksCurrentlyHeld.size());
								}
							}
							if (openBrackets == 1) {

								thisProgram.add(funct);
							}
						}
						if (data.contains("synchronized")) {
							String temp = data.substring(data.indexOf('(') + 1, data.indexOf(')'));
							LockNode newLock = new LockNode(temp, openBrackets, contents[j], lineNumber);
							// newLock.heldLocks=locksCurrentlyHeld;//Change to add new lock lists on the
							// locks currently held.

							for (int x = 0; x < locksCurrentlyHeld.size(); x++) {
								locksCurrentlyHeld.get(x).heldLocks.add(newLock);
							}

							funct.addLock(newLock);
							locksCurrentlyHeld.add(newLock);
							System.out.println("Adding new lock " + newLock.lockName + newLock.level);
							System.out.println(locksCurrentlyHeld.size());
						}
						addFunctionCall(data, funct, false);

						// System.out.println(data);

					}
					myReader.close();
				} catch (FileNotFoundException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}

				if (funct.functionName.equals("main")) {
					mainFunction = funct;
				}
			}
		}

		for (int i = 0; i < thisProgram.size(); i++) {
			System.out.println(thisProgram.get(i).className + "." + thisProgram.get(i).functionName);
			for (int k = 0; k < thisProgram.get(i).locksAcquired.size(); k++) {
				System.out.print(thisProgram.get(i).locksAcquired.get(k).lockName + ", ");
			}
			System.out.println("]");
			System.out.print("[");
			for (int k = 0; k < thisProgram.get(i).functionsCalled.size(); k++) {
				System.out.print(thisProgram.get(i).functionsCalled.get(k).functionName + ", ");
			}
			System.out.println("]");
			System.out.println(thisProgram.get(i).passedArgs);
			System.out.println(thisProgram.get(i).runnable);

		}

		// Step 2
		System.out.println("STEP 2.");
		System.out.println(classList);
		for (int j = 0; j < contents.length; j++) {
			if (contents[j].contains(".java")) {

				// Begin reading the file
				try {
					File myObj = new File(contents[j]);
					Scanner myReader = new Scanner(myObj);
					while (myReader.hasNextLine()) {
						// Replace class variables with class names.
						String data = myReader.nextLine();
						for (int i = 0; i < classList.size(); i++) {
							if (data.contains(classList.get(i))) {
								System.out.println(data);
							}
						}

					}
					myReader.close();
				} catch (FileNotFoundException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
			}
		}

		ArrayList<LockNode> SearchTree = new ArrayList<LockNode>();
		// Step 3 Starting at main, find every place where run is called on a runnable
		// class.
		// From there add all the locks taken into a directed graph such that a lock
		// acquired while holding another lock is a directed edge.
		// Whenever a class is declared save the map from name to class. WHenever that
		// name is found, replace it with the class.
		// when lock name is same as existing lock node, add all held locks to the lcoks
		// held list
		traceExecution(SearchTree, thisProgram, new ArrayList<LockNode>(), mainFunction);// CHange later to use first
																							// multithreaded function.
		// Step 4 DFS to find cycles.
		System.out.println("\n\nBegin Search Tree");
		for (int i = 0; i < SearchTree.size(); i++) {
			System.out.println(SearchTree.get(i).lockName + " " + SearchTree.get(i));
			for (int j = 0; j < SearchTree.get(i).heldLocks.size(); j++) {
				System.out.print(
						SearchTree.get(i).heldLocks.get(j).lockName + " " + SearchTree.get(i).heldLocks.get(j) + ", ");
			}
			System.out.println();
		}

		ArrayList<LockNode> visited = new ArrayList<LockNode>();
		ArrayList<LockNode> recList = new ArrayList<LockNode>();
		if (isCycle(SearchTree.get(0), visited, recList)) {
			System.out.println("DEADLOCK FOUND!!!!!");
		}

		for (int i = 0; i < recList.size(); i++) {
			System.out.println("Lock " + recList.get(i).lockName + " at " + recList.get(i).lockLocation);
		}
	}

	static boolean isCycle(LockNode next, ArrayList<LockNode> visited, ArrayList<LockNode> recList) {
		System.out.println("Node examined is " + next.lockName);
		if (!visited.contains(next)) {

			visited.add(next);
			recList.add(next);
			for (int i = 0; i < next.heldLocks.size(); i++) {
				System.out.println(next.heldLocks.get(i).lockName + " Examine");
				if (recList.contains(next.heldLocks.get(i))) {
					System.out.println("Found Result");
					return true;
				} else {
					if (isCycle(next.heldLocks.get(i), visited, recList)) {
						return true;
					}
				}
			}
		}
		System.out.println("Failure");
		recList.remove(next);
		return false;

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
		while (data.charAt(beginIndex) != ' ' && data.charAt(beginIndex) != '.') {
			beginIndex--;

		}
		return data.substring(beginIndex + 1, endIndex);
	}

	static String getClassName(String data, boolean runnable) {
		int beginIndex = data.indexOf("class");
		if (beginIndex == -1) {
			return null;
		}

		String temp = data.substring(beginIndex + 6);
		return temp.substring(0, temp.indexOf(' '));
	}

	static void addFunctionCall(String data, functionAction funct, boolean isMultithreaded) {

		if (!(data.contains("(") && data.contains(")") && data.contains(";"))) {
			return;
		}
		String temp = data.replace("  ", " ");
		temp = data.replace(" .", ".");
		temp = data.replace(". ", ".");
		temp = temp.replace("\t", "");
		// Maybe to work on non static I could have a mapping of strings to the parent
		// class.
		int leftIndex = temp.indexOf('(') - 2;

		while (leftIndex >= 0 && temp.charAt(leftIndex) != ' ' && temp.charAt(leftIndex) != '.') {

			leftIndex--;
		}
		leftIndex++;

		calledFunctions out = new calledFunctions(temp.substring(leftIndex, temp.indexOf('(')), isMultithreaded);

		if (out.functionName.equals("for") || out.functionName.equals("for ")) {
			return;
		}
		funct.addFunction(out);
		boolean nextIsMultithreaded = false;
		if (out.functionName.equals("Thread")) {
			nextIsMultithreaded = true;
		}
		addFunctionCall(temp.substring(temp.indexOf('(') + 1, temp.length()), funct, nextIsMultithreaded);// Recursive
	}

	
	/*
	 * This function recursively traverses through the code starting at the first multithreaded call to run a thread.
	 * It gets all the lock acquired and adds them to a search tree such that locks on the same object are the same node.
	 * After that the code examines all the functions that were called by the current function.
	 */
	static void traceExecution(ArrayList<LockNode> SearchTree, ArrayList<functionAction> program,
			ArrayList<LockNode> locksHeld, functionAction currentFunction) {// Pass list of functions, an arraylist of
																			// locks held while going into this function
		// If a lock is held and another lock is acquired add that relation to the lock
		// relation graph.
		// Find all functions called by the passed function.
		// Recursively enter those functions while
		System.out.println("RECURSIVE");
		System.out.println(currentFunction.functionName);

		if (currentFunction.visited) {
			return;
		}
		currentFunction.visited = true;

		for (int i = 0; i < currentFunction.locksAcquired.size(); i++) {
			System.out.print(currentFunction.locksAcquired.get(i).lockName + ", ");
		}
		System.out.println();
		// Add locks to search tree
		for (int i = 0; i < currentFunction.locksAcquired.size(); i++) {

			LockNode temp = currentFunction.locksAcquired.get(i);
			int location = -1;
			for (int j = 0; j < SearchTree.size(); j++) {
				if (SearchTree.get(j).lockName.equals(temp.lockName)) {
					location = j;
				}
			}
			if (location != -1) {
				//Lock is already in tree
				//update the node
				System.out.println("Already found " + temp.lockName+" with size "+temp.heldLocks.size());
				SearchTree.get(location).heldLocks.addAll(locksHeld);
				SearchTree.get(location).lockLocation=SearchTree.get(location).lockLocation+" and "+temp.lockLocation;
				for (int k = 0; k < temp.heldLocks.size(); k++) {
					for (int j = 0; j < SearchTree.size(); j++) {
						if (SearchTree.get(j).lockName.equals(temp.heldLocks.get(k).lockName)) {
							System.out.println("Add from search tree "+temp.heldLocks.get(k).lockName);
							SearchTree.get(j).lockLocation=SearchTree.get(j).lockLocation;
							SearchTree.get(location).heldLocks.add(SearchTree.get(j));
						} else {
							System.out.println("add new temp "+temp.heldLocks.get(k).lockName);
							SearchTree.get(location).heldLocks.add(temp.heldLocks.get(k));
						}
					}
				}
				// SearchTree.get(location).heldLocks.addAll(temp.heldLocks);
			} else {
				//add a new node to the spanning tree.
				temp.heldLocks.addAll(locksHeld);
				System.out.println("ADD new node " + temp.lockName);
				SearchTree.add(temp);
			}
		}//End lock for loop

		// Go to next functions
		for (int i = 0; i < currentFunction.functionsCalled.size(); i++) {
			int location = -1;
			for (int j = 0; j < program.size(); j++) {
				// System.out.println(program.get(j).functionName+"
				// "+(currentFunction.functionsCalled.get(i).functionName));
				if (program.get(j).functionName.equals(currentFunction.functionsCalled.get(i).functionName)
						|| (program.get(j).functionName.equals("run")
								&& currentFunction.functionsCalled.get(i).functionName.equals("start"))) {
					// Does not factor in class
					// TODO: Add that to the comparison.
					location = j;
					break;
				}
			}

			if (location != -1) {
				locksHeld.addAll(currentFunction.locksAcquired);
				traceExecution(SearchTree, program, locksHeld, program.get(location));
				locksHeld.removeAll(currentFunction.locksAcquired);
			}

		}
	}
}
