package cs131.pa1.filter.concurrent;

public class WcFilter extends ConcurrentFilter {
	private int linecount = 0;
	private int wordcount = 0;
	private int charcount = 0;
	
	public WcFilter() {
		super();
	}
	
	public void process() {
		while(!isDone()) {
//			if(input != null) {
				String line = input.peek();
				if(line != null && !line.equals("COMPLETED")) {
					line = input.poll();
					linecount++;
					String[] wct = line.split(" ");
					wordcount += wct.length;
					String[] cct = line.split("|");
					charcount += cct.length;
				}
//			}
		}
		output.add(linecount + " " + wordcount + " " + charcount);
		output.add("COMPLETED");
//		for(String s:output) {
//			System.out.println(s);
//		}
//		System.out.println("wc COMPLETED");
	}
	
	public String processLine(String line) {
//		//prints current result if ever passed a null
//		if(line == null) {
			return null;
//		}
		
//		if(isDone()) {
//			String[] wct = line.split(" ");
//			wordcount += wct.length;
//			String[] cct = line.split("|");
//			charcount += cct.length;
//			return ++linecount + " " + wordcount + " " + charcount;
//		} else {
//			linecount++;
//			String[] wct = line.split(" ");
//			wordcount += wct.length;
//			String[] cct = line.split("|");
//			charcount += cct.length;
//			return null;
//		}
	}
	
	public String toString() {
		return "wc";
	}
}
