package cs131.pa1.filter.concurrent;

public class PrintFilter extends ConcurrentFilter {
	public PrintFilter() {
		super();
	}
	
	public void process() {
		while(!isDone()){
			String line = input.poll();
			//System.out.println(line);
			if(line !=null && !line.equals("COMPLETED")) {
				processLine(line);
//				System.out.println(line);
			} 
			if(line != null && line.equals("COMPLETED")){
				break;
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
