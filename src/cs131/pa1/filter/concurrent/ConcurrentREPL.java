package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.LinkedList;
import java.util.Scanner;

public class ConcurrentREPL {

	static String currentWorkingDirectory;
	
	public static void main(String[] args){
		currentWorkingDirectory = System.getProperty("user.dir");
		Scanner s = new Scanner(System.in);
		System.out.print(Message.WELCOME);
		LinkedList<Thread> threads = new LinkedList<Thread>();
		String command;
		while(true) {
			//obtaining the command from the user
			System.out.print(Message.NEWCOMMAND);
			command = s.nextLine();
			if(command.equals("exit")) {
				break;
			} else if (command.trim().equals("repl_jobs")) {
				int number = 1;
				for (Thread t: threads) {
					if (t.isAlive()) {
						System.out.println(number + ". " + t.getName() + " & ");
						number++;
					}
				}
			} else if (command.contains("kill")) {
				String[] killList = command.split("\\s+");
				if(killList.length == 1) {
					System.out.printf(Message.REQUIRES_PARAMETER.toString(), command);
				} else if (killList.length>1) {
					int killNumber = 0;
					try {
						killNumber = Integer.parseInt(killList[1]);
					} catch (NumberFormatException e) {
						System.out.printf(Message.INVALID_PARAMETER.toString(), command);
					}
					if(killNumber !=0 ) {
						if((killNumber)>threads.size()) {
							System.out.printf(Message.INVALID_PARAMETER.toString(), command);
						} else {
							if (threads.get(killNumber-1).isAlive()) {
								threads.get(killNumber-1).interrupt();
							}
						}
					}
				}
			} else if(!command.trim().equals("")) {
				//building the filters list from the command
				String[] commandList = command.split("\\s+");
				String symbol = commandList[commandList.length-1];
				boolean backgroundMode = false;
				if (symbol.equals("&")) {
					backgroundMode = true;
					int symbolIndex = command.indexOf(symbol);
					command = command.substring(0, symbolIndex);
				}
				ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
				//System.out.println(filterlist.toString());
				Thread last = Thread.currentThread();
				while (filterlist != null) {
//					System.out.println("enter");
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
		s.close();
		System.out.print(Message.GOODBYE);
	}

}

