package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;

public class ConcurrentREPL {

	static String currentWorkingDirectory;
	
	public static void main(String[] args){
		currentWorkingDirectory = System.getProperty("user.dir");
		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		// list of thread
		LinkedList<Thread> threads = new LinkedList<Thread>();
		String command;
		// set that contains alive threads
		Set<String> aliveThread = new HashSet<String>();
		while(true) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			// split the command by space
			String[] commandList = command.split("\\s+");
			// if the command is a kill command
			if (command.startsWith("kill")) {
				// run kill command
				kill(commandList, threads);
			// if the command is an exit command
			} else if(command.equals("exit")) {
				break;
			// if the command is repl_jobs
			} else if (command.trim().equals("repl_jobs")) {
				// number the thread
				int threadNumber = 1;
				// loop through all threads in the thread list
				for (Thread t: threads) {
					// check if the thread is alive
					if (t.isAlive()) {
						// check if the thread is alive and hasn't been printed out before
						if (!aliveThread.contains(t.getName())) {
							System.out.println("\t" + threadNumber + ". " + t.getName() + "&");
						}
						// add the thread to the alive thread set 
						aliveThread.add(t.getName());
					}
					// adding thread number
					threadNumber++;
				}
			} else {
				// method for running other commands
				otherCommands(command, commandList, threads);
			}
		}
		// close the console
		s.close();
		System.out.print(Message.GOODBYE);
	}

	public static void kill(String[] commandList, LinkedList<Thread> threads) {
		// check if the kill command has a parameter
		if(commandList.length<2){
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), "kill");
		} else {
			// use this integer to identify the thread to be killed
			int killNumber = 0;
			try {
				killNumber = Integer.parseInt(commandList[1]);
			} catch (NumberFormatException e) {
				System.out.printf(Message.INVALID_PARAMETER.toString(), "kill "+commandList[1]);
			}
			// check if the kill number is correct
			if(killNumber !=0 ) {
				// check the invalid kill number parameter
				if((killNumber)>threads.size()) {
					System.out.printf(Message.INVALID_PARAMETER.toString(), "kill "+commandList[1]);
				} else {
					// killed the thread 
					int killed = killNumber-1;
					if (threads.get(killed).isAlive()) {
						threads.get(killed).stop();
					}
				}
			}	
		}
	}
			
	public static void otherCommands(String command, String[] commandList, LinkedList<Thread> threads) {
		// check if the command is not empty
		if(!command.trim().equals("")) {
			// get the & symbol
			String symbol = commandList[commandList.length-1];
			// boolean for determining whether to run background mode
			boolean backgroundMode = false;
			// if the command has & symbol, then get all commands before &
			if (symbol.equals("&")) {
				backgroundMode = true;
				int symbolIndex = command.indexOf(symbol);
				command = command.substring(0, symbolIndex);
			}
			// build the concurrent filter from command
			ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
			// get the current thread
			Thread last = Thread.currentThread();
			while (filterlist != null) {
				// build new thread from the concurrent filter
				Thread nextFilter = new Thread(filterlist,command);
				// start the thread
				nextFilter.start();
				// if it is the last thread in line, identify it as the last one
				if (!filterlist.hasNext()) {
					last = nextFilter;
				}
				filterlist = (ConcurrentFilter) filterlist.getNext();
			}	
			// if the background mode is on
			if (backgroundMode) {
				// add the last one to the thread list
				threads.add(last);
			} else {
				// if the background mode is off
				try{
					// join the main thread
					if(!last.equals(Thread.currentThread())) {
						last.join();
					}
				} catch(InterruptedException e){}
			}
		} 
	}
}

