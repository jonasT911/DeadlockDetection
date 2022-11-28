package Detection;

import java.awt.image.ConvolveOp;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class master {

	static int numberOfRecursions;
	public static String slash = "\\";
	public static void main(String[] args) {
		String os = System.getProperty("os.name");
//		System.out.println(os);
		if(os.charAt(0) == 'M'){
			slash = "/";
		}
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

		String path;
		if (args.length == 0) {
			path = "";
		} else {
			path = slash + args[0];
		}
		System.out.println(System.getProperty("user.dir") + path);
		File directoryPath = new File(System.getProperty("user.dir") + path);

		// List of all files and directories
		if (directoryPath == null) {
			System.out.println("directory is null somehow.");
		}
		if (!directoryPath.isDirectory()) {
			System.out.println("The passed path does not lead to a directory");
		}
		String contents[] = directoryPath.list();
		if (contents == null) {
			System.out.println("Listing did not work");
		}
		for (int j = 0; j < contents.length; j++) {
			System.out.println(System.getProperty("user.dir") + path+slash+contents[j]);
			if (contents[j].contains(".java")) {

				// Begin reading the file
				try {
					File myObj = new File(System.getProperty("user.dir") + path+slash+contents[j]);
					Scanner myReader = new Scanner(myObj);
					locksCurrentlyHeld.clear();
					lineNumber = 0;
					boolean hasMoreLines = true;

					while (hasMoreLines) {
						String data = "";
						while (!data.contains(";") && !data.contains("{") && !data.contains("}") && hasMoreLines) {
							if (myReader.hasNextLine()) {
								String temp;

								lineNumber++;
								temp = myReader.nextLine();// Note, this will need to be change to key off of ; and {
															// and not \r
								// System.out.println(temp + "pre temp");
								int commentIndex = temp.indexOf("//");
								if (commentIndex != -1) {
									temp = temp.substring(0, commentIndex);
								}
								// System.out.println(temp + "was temp");
								data = data.concat(temp);

							} else {
								// System.out.println("End file reading");
								hasMoreLines = false;
							}
						}
						data = data.replace("\n", " ");
						data = data.replace("\t", " ");
						data = data.replace("  ", " ");
						/*
						 * while(myReader.hasNextLine()) { String data=myReader.nextLine();
						 */
						// System.out.println("|"+data+"|");
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

							funct.addLock(newLock);
							for (int x = 0; x < locksCurrentlyHeld.size(); x++) {
								locksCurrentlyHeld.get(x).locksAcquiredWithin.add(newLock);
								// Change this so the edge is only added if the node is run.
								funct.edgesMade.add(new lockEdge(locksCurrentlyHeld.get(x), newLock));// Error is here
							}
							locksCurrentlyHeld.add(newLock);

							// System.out.println("Adding new lock " + newLock.lockName + newLock.level);
							// System.out.println(locksCurrentlyHeld.size());
						}
						addFunctionCall(data, funct, false);

						// System.out.println(data);

					} // File reading while loop end
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
//		System.out.println("Locks found");
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
//			System.out.println("Passed args are "+thisProgram.get(i).passedArgs);
//			System.out.println(thisProgram.get(i).runnable);
//
//		}

		System.out.println(classList);
		// Step 2
		// Removed
		Map<String, String> VariableToClass = new HashMap<String, String>();
		if (contents == null) {
			System.out.println("Listing did not work");
		}
		for (int j = 0; j < contents.length; j++) {
			// System.out.println(contents[j]);
			if (contents[j].contains(".java")) {

				// Begin reading the file
				try {
					File myObj = new File(System.getProperty("user.dir") + path+slash+contents[j]);
					Scanner myReader = new Scanner(myObj);
					locksCurrentlyHeld.clear();
					lineNumber = 0;
					boolean hasMoreLines = true;

					while (hasMoreLines) {
						String data = "";
						while (!data.contains(";") && !data.contains("{") && !data.contains("}") && hasMoreLines) {
							if (myReader.hasNextLine()) {
								String temp;

								lineNumber++;
								temp = myReader.nextLine();// Note, this will need to be change to key off of ; and {
															// and not \r
								// System.out.println(temp + "pre temp");
								int commentIndex = temp.indexOf("//");
								if (commentIndex != -1) {
									temp = temp.substring(0, commentIndex);
								}
								// System.out.println(temp + "was temp");
								data = data.concat(temp);

							} else {
								// System.out.println("End file reading");
								hasMoreLines = false;
							}
						}
						data = data.replace("\n", " ");
						data = data.replace("\t", " ");
						data = data.replace("  ", " ");

						// Check if the data contains the class as the type for a variable
						// If it does, add the variable name and class as a mapping
						// System.out.println("Line: " + data);
						for (int i = 0; i < classList.size(); i++) {
							int location = data.indexOf(classList.get(i));
							if (location != -1 && !data.contains(" class ")) {

								if (data.contains("Thread")) {
									System.out.println("Its a thread");//
									String temp = data.substring(data.indexOf("Thread") + 7);
									String newFunct;
									if (temp.contains("Thread") && temp.contains("new ")) {
										newFunct = temp.substring(temp.indexOf("("));
										newFunct = newFunct.substring(newFunct.indexOf("new ") + 4,
												newFunct.indexOf(")") - 1);
										System.out.println("Thread class is " + newFunct);

									} else {
										newFunct = "Something else";
									}

									temp = temp.substring(0, temp.indexOf(" "));
									System.out.println(temp);
									VariableToClass.put(temp, classList.get(i));

								} else {
									// A non thread class variable is declared.
									String temp = data.substring(location + classList.get(i).length() + 1);
									// System.out.println("CLASS NAME IS " + temp);
									if (temp.indexOf(' ') != -1) {
										temp = temp.substring(0, temp.indexOf(' '));
									}
									if (temp.indexOf('=') != -1) {
										temp = temp.substring(0, temp.indexOf('='));
									}
									if (temp.indexOf(';') != -1) {
										temp = temp.substring(0, temp.indexOf(';'));
									}
									System.out.println(temp);
									VariableToClass.put(temp, classList.get(i));
								}
//								System.out.println("Found class name " + classList.get(i));
//								System.out.println("Found class " + data);
							}
						}

					}
				} catch (FileNotFoundException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
			}
		}
		System.out.println("Map is :" + VariableToClass);
//		for (int i = 0; i < classList.size(); i++) {
//			System.out.print(classList.get(i) + " ");
//			// System.out.println(classList.get )
//		}
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
				new ArrayList<LockNode>(), VariableToClass);
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
		if (SearchTree.size() >= 2) {
			if (isCycle(SearchTree.get(1), visited, recList)) {
				System.out.println("DEADLOCK FOUND!!!!!");

				for (int k = 0; k < recList.size() - 1; k++) {
					for (int l = 0; l < listOfEdges.size(); l++) {
						// System.out.println(recList.get(k).lockName+"
						// "+listOfEdges.get(l).startingLock);
						if (recList.get(k).lockName.equals(listOfEdges.get(l).startingLock)
								&& (recList.get(k + 1).lockName.equals(listOfEdges.get(l).endingLock))) {
							System.out.println(listOfEdges.get(l));
						}
					}
				}
				// Check for final edge
				for (int l = 0; l < listOfEdges.size(); l++) {
					// System.out.println("Last "+recList.get(recList.size()-1).lockName+"
					// "+listOfEdges.get(l).startingLock);
					if (recList.get(recList.size() - 1).lockName.equals(listOfEdges.get(l).startingLock)
							&& (recList.get(0).lockName.equals(listOfEdges.get(l).endingLock))) {
						System.out.println(listOfEdges.get(l));
					}
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

		// WHile the function is using numbers and letters
		while (leftIndex >= 0 && ((temp.charAt(leftIndex) >= 'a' && temp.charAt(leftIndex) <= 'z')
				|| (temp.charAt(leftIndex) >= 'A' && temp.charAt(leftIndex) <= 'Z')
				|| (temp.charAt(leftIndex) >= '0' && temp.charAt(leftIndex) <= '9'))) {

			leftIndex--;
		}
		leftIndex++;

		String targetClass = "";

		if (leftIndex > 0 && temp.charAt(leftIndex - 1) == '.') {

			targetClass = temp.substring(0, leftIndex - 1);
			targetClass = targetClass.replace(" ", "");

		} else {
			targetClass = funct.className;
		}
		calledFunctions out = new calledFunctions(temp.substring(leftIndex, temp.indexOf('(')), targetClass,
				isMultithreaded);

		if (out.functionName.equals("for") || out.functionName.equals("for ")) {
			return;
		}
		if (temp.contains("(") && temp.contains(")")) {
			if (temp.indexOf('(') < temp.indexOf(')')) {
				out.argsPassed = temp.substring(temp.indexOf('(') + 1, temp.indexOf(')'));
				funct.addFunction(out);
				boolean nextIsMultithreaded = false;
				if (out.functionName.equals("Thread")) {
					nextIsMultithreaded = true;
				}
				addFunctionCall(temp.substring(temp.indexOf('(') + 1, temp.length()), funct, nextIsMultithreaded);// Recursive
			}
		}
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
			ArrayList<LockNode> oldLocks, Map<String, String> VariableToClass) {
		// Pass list of functions, an arraylist of
		// locks held while going into this function
		// If a lock is held and another lock is acquired add that relation to the lock
		// relation graph.
		// Find all functions called by the passed function.
		// Recursively enter those functions while

		System.out.println("\nRECURSIVE START");
		System.out.println(currentFunction.className);
		System.out.println(currentFunction.functionName);
		System.out.println("Args " + currentFunction.passedArgs);
		System.out.println("St " + SearchTree);
		System.out.println(locksHeld);
		System.out.println("List of edges " + listOfEdges);
		System.out.println(oldLocks);

		ArrayList<LockNode> locksAddedThisCycle = new ArrayList<LockNode>();
		ArrayList<LockNode> oldLocksAddedThisCycle = new ArrayList<LockNode>();
		listOfEdges.addAll(currentFunction.edgesMade);
		// Add locks to search tree
		for (int i = 0; i < currentFunction.locksAcquired.size(); i++) {

			LockNode temp = currentFunction.locksAcquired.get(i);
//			System.out.println(temp.lockName);
//			System.out.println(temp.locksAcquiredWithin);
//			System.out.println();
			convertFromArg(temp, currentFunction);
			oldLocksAddedThisCycle.add(temp);
			// Maintains location of each dependency's location
			for (int j = 0; j < oldLocks.size(); j++) {
				listOfEdges.add(new lockEdge(oldLocks.get(j), temp));

			}

			int location = -1;

			// Search for the acquired lock in the search tree
			for (int j = 0; j < SearchTree.size(); j++) {
				if (SearchTree.get(j).lockObj.equals(temp.lockObj)) {
					location = j;
				}
			}
			if (location != -1) {
				// Lock is already in tree
				// update the node
				// System.out.println("Already found " + temp.lockName + " with size " +
				// temp.locksAcquiredWithin.size());

				for (int k = 0; k < temp.locksAcquiredWithin.size(); k++) {// For each held lock
					boolean found = false;
					convertFromArg(temp.locksAcquiredWithin.get(k), currentFunction);
					for (int j = 0; j < SearchTree.size(); j++) {// Check if the held lock is in the tree
						if (SearchTree.get(j).lockObj.equals(temp.locksAcquiredWithin.get(k).lockObj)) {
							// Held lock matches a node in the tree.
							// System.out.println("Add held from search tree " +
							// temp.locksAcquiredWithin.get(k).lockName);
							found = true;
							// Add the object from the tree
							// System.out.println("Add "+SearchTree.get(j));
							SearchTree.get(location).locksAcquiredWithin.add(SearchTree.get(j));

						}
					}
					if (!found) {

						// System.out.println("add new held temp " +
						// temp.locksAcquiredWithin.get(k).lockName);

						// Otherwise add a new object to the held lock list
						// System.out.println("Add to lock "+SearchTree.get(location)+" new lock
						// "+temp.locksAcquiredWithin.get(k));
						SearchTree.get(location).locksAcquiredWithin.add(temp.locksAcquiredWithin.get(k));
					}
				}
				locksAddedThisCycle.add(SearchTree.get(location));
				// Add the old lock to the locks acquired for all currently held locks
				for (int k = 0; k < locksHeld.size(); k++) {
					// System.out.println("ADD " + SearchTree.get(location).lockName + " to " +
					// locksHeld.get(k).lockName);
					locksHeld.get(k).locksAcquiredWithin.add(SearchTree.get(location));

				}

			} else {
				// add a new node to the spanning tree.
				// temp.locksAcquiredWithin.addAll(locksHeld);

				for (int k = 0; k < locksHeld.size(); k++) {
					locksHeld.get(k).locksAcquiredWithin.add(temp);
				}

				locksAddedThisCycle.add(temp);
				// System.out.println("ADD "+temp);
				SearchTree.add(temp);
			}
		} // End lock for loop

		// Go to next functions
		for (int i = 0; i < currentFunction.functionsCalled.size(); i++) {
			int location = -1;
			String targetClass = currentFunction.functionsCalled.get(i).className;
			String targetFunction = currentFunction.functionsCalled.get(i).functionName;
			System.out.println("Next class: " + targetClass + " next funct: " + targetFunction);
			for (int j = 0; j < program.size(); j++) {
				// System.out.println(program.get(j).functionName+"
				// "+(currentFunction.functionsCalled.get(i).functionName));

				// Replace class name if function is in the mapping

				String nextClass = program.get(j).className;
				String nextFunct = program.get(j).functionName;

				if (VariableToClass.containsKey(targetClass)) {
					System.out.println("Founf" + targetClass);
					targetClass = VariableToClass.get(targetClass);
					System.out.println("New class is " + targetClass);
				}

				boolean foundFunction = nextFunct.equals(targetFunction) && (nextClass.equals(targetClass));
				boolean foundThread = ((program.get(j).functionName.equals("run")
						&& currentFunction.functionsCalled.get(i).functionName.equals("start")));

				if (foundFunction || foundThread) {
					// Does not factor in class
					// TODO: Add that to the comparison.

					// This is useful for propagating passed args
					program.get(j).setArgs(currentFunction.functionsCalled.get(i).argsPassed);
					program.get(j).updatePropagatedArgs(currentFunction);
					System.out.println("New function args " + currentFunction.functionsCalled.get(i).argsPassed);
					location = j;
					break;
				}
			}

			if (location != -1) {
				if (program.get(location).visited) {
					System.out.println("Recursion Detected in Code under test\n");
					return;
				}
				if(numberOfRecursions<4) {
					numberOfRecursions++;
				currentFunction.visited = true;
				locksHeld.addAll(locksAddedThisCycle);
				oldLocks.addAll(oldLocksAddedThisCycle);

				traceExecution(SearchTree, program, locksHeld, program.get(location), listOfEdges, oldLocks,
						VariableToClass);
				locksHeld.removeAll(locksAddedThisCycle);
				oldLocks.removeAll(oldLocksAddedThisCycle);
				currentFunction.visited = false;
			//	numberOfRecursions--;
			}
			}

		}
	}

	static void convertFromArg(LockNode temp, functionAction currentFunction) {
		if (temp.lockObj.charAt(0) >= '0' && temp.lockObj.charAt(0) <= '9') {
			// System.out.println("Its an arg");
			int argIndex = Integer.parseInt(temp.lockObj);
			// System.out.println(temp.lockName+" is now
			// "+currentFunction.passedArgs.get(argIndex));//Change lock to be this
			temp.lockObj = currentFunction.passedArgs.get(argIndex);
		}
	}
}
