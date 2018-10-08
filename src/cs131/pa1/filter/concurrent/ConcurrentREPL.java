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
		LinkedList<Thread> threads = new LinkedList<Thread>();
		String command;
		Set<String> aliveThread = new HashSet<String>();
		while(true) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			boolean containKillCommand = false;	
			String[] commandList = command.split("\\s+");
			if (command.contains("kill")) {
				int killAtIndex=-1;
				for(int i = 0; i<commandList.length;i++){
					if(commandList[i].equals("kill")){
						containKillCommand = true;
						killAtIndex = i;
					}
				}
				if(containKillCommand){
					kill(killAtIndex, commandList, threads);
				} else {
					otherCommands(command, commandList, threads);
				}
			} else if(command.equals("exit")) {
				break;
			} else if (command.trim().equals("repl_jobs")) {
				int number = 1;
				for (Thread t: threads) {
					if (t.isAlive()) {
						if (!aliveThread.contains(t.getName())) {
							System.out.println("\t" + number + ". " + t.getName() + "&");
						}
						aliveThread.add(t.getName());
					}
					number++;
				}
			} else {
				otherCommands(command, commandList, threads);
			}
		}
		s.close();
		System.out.print(Message.GOODBYE);
	}

	public static void kill(int killAtIndex, String[] commandList, LinkedList<Thread> threads) {
		if(killAtIndex+1>=commandList.length){
			System.out.printf(Message.REQUIRES_PARAMETER.toString(), "kill");
		} else {
			int killNumber = 0;
			try {
				killNumber = Integer.parseInt(commandList[killAtIndex+1]);
			} catch (NumberFormatException e) {
				System.out.printf(Message.INVALID_PARAMETER.toString(), "kill "+commandList[killAtIndex+1]);
			}
			if(killNumber !=0 ) {
				if((killNumber)>threads.size()) {
					System.out.printf(Message.INVALID_PARAMETER.toString(), "kill "+commandList[killAtIndex+1]);
				} else {
					int killed = killNumber-1;
					if (threads.get(killed).isAlive()) {
						threads.get(killed).stop();
					}
				}
			}	
		}
	}
			
	public static void otherCommands(String command, String[] commandList, LinkedList<Thread> threads) {
		if(!command.trim().equals("")) {
			String symbol = commandList[commandList.length-1];
			boolean backgroundMode = false;
			if (symbol.equals("&")) {
				backgroundMode = true;
				int symbolIndex = command.indexOf(symbol);
				command = command.substring(0, symbolIndex);
			}
			ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
			Thread last = Thread.currentThread();
			while (filterlist != null) {
				Thread nextFilter = new Thread(filterlist,command);
				nextFilter.start();
				if (!filterlist.hasNext()) {
					last = nextFilter;
				}
				filterlist = (ConcurrentFilter) filterlist.getNext();
			}	
			if (backgroundMode) {
				threads.add(last);
			} else {
				try{
					if(!last.equals(Thread.currentThread())) {
						last.join();
					}
				} catch(InterruptedException e){}
			}
		} 
	}
}

