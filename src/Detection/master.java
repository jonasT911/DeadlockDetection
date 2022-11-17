package Detection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class master {

	public static void main(String[] args) {
		ArrayList<lockEdge> listOfEdges = new ArrayList<lockEdge>();
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
							// newLock.locksAcquiredWithin=locksCurrentlyHeld;//Change to add new lock lists
							// on the
							// locks currently held.

							for (int x = 0; x < locksCurrentlyHeld.size(); x++) {
								locksCurrentlyHeld.get(x).locksAcquiredWithin.add(newLock);
								listOfEdges.add(new lockEdge(locksCurrentlyHeld.get(x), newLock));
							}

							funct.addLock(newLock);
							locksCurrentlyHeld.add(newLock);

							// System.out.println("Adding new lock " + newLock.lockName + newLock.level);
							// System.out.println(locksCurrentlyHeld.size());
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

		// prints the locks found
//		for (int i = 0; i < thisProgram.size(); i++) {
//			System.out.println(thisProgram.get(i).className + "." + thisProgram.get(i).functionName);
//			for (int k = 0; k < thisProgram.get(i).locksAcquired.size(); k++) {
//				System.out.print(thisProgram.get(i).locksAcquired.get(k).lockName + ", ");
//			}
//			System.out.println("]");
//			System.out.print("[");
//			for (int k = 0; k < thisProgram.get(i).functionsCalled.size(); k++) {
//				System.out.print(thisProgram.get(i).functionsCalled.get(k).functionName + ", ");
//			}
//			System.out.println("]");
//			System.out.println(thisProgram.get(i).passedArgs);
//			System.out.println(thisProgram.get(i).runnable);
//
//		}

		// Step 2
		//Removed
		
		// Step 3 Starting at main, find every place where run is called on a runnable
		// class.
		// From there add all the locks taken into a directed graph such that a lock
		// acquired while holding another lock is a directed edge.
		// Whenever a class is declared save the map from name to class. WHenever that
		// name is found, replace it with the class.
		// when lock name is same as existing lock node, add all held locks to the lcoks
		// held list
		ArrayList<LockNode> SearchTree = new ArrayList<LockNode>();

		traceExecution(SearchTree, thisProgram, new ArrayList<LockNode>(), mainFunction, listOfEdges,
				new ArrayList<LockNode>());
		// TODO: CHange later to use first multithreaded function.

		// Step 4 DFS to find cycles.
//		System.out.println("\n\nBegin Search Tree");
//		for (int i = 0; i < SearchTree.size(); i++) {
//			System.out.println(SearchTree.get(i).lockName + " " + SearchTree.get(i) + "\nAcquires: ");
//			for (int j = 0; j < SearchTree.get(i).locksAcquiredWithin.size(); j++) {
//				System.out.print(SearchTree.get(i).locksAcquiredWithin.get(j).lockName + " "
//						+ SearchTree.get(i).locksAcquiredWithin.get(j) + ", ");
//			}
//			System.out.println("\n");
//		}
	
		ArrayList<LockNode> visited = new ArrayList<LockNode>();
		ArrayList<LockNode> recList = new ArrayList<LockNode>();
		if (isCycle(SearchTree.get(1), visited, recList)) {
			System.out.println("DEADLOCK FOUND!!!!!");

			for (int k = 0; k < recList.size() - 1; k++) {
				for (int l = 0; l < listOfEdges.size(); l++) {
					if (recList.get(k).lockName.equals(listOfEdges.get(l).startingLock)
							&& (recList.get(k + 1).lockName.equals(listOfEdges.get(l).endingLock))) {
						System.out.println(listOfEdges.get(l));
					}
				}
			}
			// Check for final edge
			for (int l = 0; l < listOfEdges.size(); l++) {
				if (recList.get(recList.size() - 1).lockName.equals(listOfEdges.get(l).startingLock)
						&& (recList.get(0).lockName.equals(listOfEdges.get(l).endingLock))) {
					System.out.println(listOfEdges.get(l));
				}
			}
		}
		System.out.println("Finished");

	}
//End main function

	static boolean isCycle(LockNode next, ArrayList<LockNode> visited, ArrayList<LockNode> recList) {
//		System.out.println("Node examined is " + next.lockName);
		if (!visited.contains(next)) {

			visited.add(next);
			recList.add(next);
			for (int i = 0; i < next.locksAcquiredWithin.size(); i++) {
				if (recList.contains(next.locksAcquiredWithin.get(i))) {

					recList.add(next.locksAcquiredWithin.get(i));
					return true;
				} else {
					if (isCycle(next.locksAcquiredWithin.get(i), visited, recList)) {
						return true;
					}
				}
			}
		}

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
	 * This function recursively traverses through the code starting at the first
	 * multithreaded call to run a thread. It gets all the lock acquired and adds
	 * them to a search tree such that locks on the same object are the same node.
	 * After that the code examines all the functions that were called by the
	 * current function.
	 */
	static void traceExecution(ArrayList<LockNode> SearchTree, ArrayList<functionAction> program,
			ArrayList<LockNode> locksHeld, functionAction currentFunction, ArrayList<lockEdge> listOfEdges,
			ArrayList<LockNode> oldLocks) {
		// Pass list of functions, an arraylist of
		// locks held while going into this function
		// If a lock is held and another lock is acquired add that relation to the lock
		// relation graph.
		// Find all functions called by the passed function.
		// Recursively enter those functions while
		
//		System.out.println("\nRECURSIVE START");
//		System.out.println(currentFunction.functionName);
//		System.out.println(locksHeld);

		ArrayList<LockNode> locksAddedThisCycle = new ArrayList<LockNode>();
		ArrayList<LockNode> oldLocksAddedThisCycle = new ArrayList<LockNode>();

		// Add locks to search tree
		for (int i = 0; i < currentFunction.locksAcquired.size(); i++) {
			LockNode temp = currentFunction.locksAcquired.get(i);
			oldLocksAddedThisCycle.add(temp);
			// Maintains location of each dependency's location
			for (int j = 0; j < oldLocks.size(); j++) {
				listOfEdges.add(new lockEdge(oldLocks.get(j), temp));

			}

			int location = -1;

			// Search for the acquired lock in the search tree
			for (int j = 0; j < SearchTree.size(); j++) {
				if (SearchTree.get(j).lockName.equals(temp.lockName)) {
					location = j;
				}
			}
			if (location != -1) {
				// Lock is already in tree
				// update the node
			//System.out.println("Already found " + temp.lockName + " with size " + temp.locksAcquiredWithin.size());

				SearchTree.get(location).lockLocation = SearchTree.get(location).lockLocation; // + " and "+
																								// temp.lockLocation;//
																								// Updates where lock is
																								// found. This will be
																								// changed

				for (int k = 0; k < temp.locksAcquiredWithin.size(); k++) {// For each held lock
					boolean found = false;
					for (int j = 0; j < SearchTree.size(); j++) {// Check if the held lock is in the tree
						if (SearchTree.get(j).lockName.equals(temp.locksAcquiredWithin.get(k).lockName)) {
							// Held lock matches a node in the tree.
						//	System.out.println("Add held from search tree " + temp.locksAcquiredWithin.get(k).lockName);
							found = true;
							// Add the object from the tree
							SearchTree.get(location).locksAcquiredWithin.add(SearchTree.get(j));

						}
					}
					if (!found) {

						//System.out.println("add new held temp " + temp.locksAcquiredWithin.get(k).lockName);

						// Otherwise add a new object to the held lock list
						SearchTree.get(location).locksAcquiredWithin.add(temp.locksAcquiredWithin.get(k));
					}
				}
				locksAddedThisCycle.add(SearchTree.get(location));
				// Add the old lock to the locks acquired for all currently held locks
				for (int k = 0; k < locksHeld.size(); k++) {
					//System.out.println("ADD " + SearchTree.get(location).lockName + " to " + locksHeld.get(k).lockName);
					locksHeld.get(k).locksAcquiredWithin.add(SearchTree.get(location));

				}

			} else {
				// add a new node to the spanning tree.
				// temp.locksAcquiredWithin.addAll(locksHeld);

				for (int k = 0; k < locksHeld.size(); k++) {
					temp.locksAcquiredWithin.add(locksHeld.get(k));
				}
			//	System.out.println("ADD new node " + temp.lockName);

				for (int k = 0; k < locksHeld.size(); k++) {
					locksHeld.get(k).locksAcquiredWithin.add(temp);
				}

				locksAddedThisCycle.add(temp);
				SearchTree.add(temp);
			}
		} // End lock for loop

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
				if (program.get(location).visited) {
					System.out.println("Recursion Detected in Code under test\n");
					return;
				}
				currentFunction.visited = true;
				locksHeld.addAll(locksAddedThisCycle);
				oldLocks.addAll(oldLocksAddedThisCycle);
				traceExecution(SearchTree, program, locksHeld, program.get(location), listOfEdges, oldLocks);
				locksHeld.removeAll(locksAddedThisCycle);
				oldLocks.removeAll(oldLocksAddedThisCycle);
				currentFunction.visited = false;
			}

		}
	}

}
