package cs131.pa1.filter.concurrent;

public class PrintFilter extends ConcurrentFilter {
	public PrintFilter() {
		super();
	}
	
	public void process() {
		while(!isDone()) {
			//System.out.println(input.isEmpty());
			String line = input.peek();
			if(line !=null && !line.equals("COMPLETED")) {
				line = input.poll();
				processLine(line);
//				System.out.println(line);
			}
		}
	}
	
	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
	
	public String toString() {
		return "print";
	}
	
//	public boolean isDone() {
//		return output.contains("COMPLETED");
//	}
}
