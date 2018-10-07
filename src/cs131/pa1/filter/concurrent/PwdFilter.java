package cs131.pa1.filter.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

public class PwdFilter extends ConcurrentFilter {
	public PwdFilter() {
		super();
	}
	
	public void process() {
		output.add(processLine(""));
		output.add("COMPLETED");
	}
	
	public String processLine(String line) {
		return ConcurrentREPL.currentWorkingDirectory;
	}
	
	public String toString() {
		return "pwd";
	}
	
	public boolean isDone() {
		return output.contains("COMPLETED");
	}
}
