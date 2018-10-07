package cs131.pa1.filter.concurrent;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Filter;


public abstract class ConcurrentFilter extends Filter implements Runnable{
	
	protected LinkedBlockingQueue<String> input;
	protected LinkedBlockingQueue<String> output;
	
	
	@Override
	public void setPrevFilter(Filter prevFilter) {
		prevFilter.setNextFilter(this);
	}
	
	@Override
	public void setNextFilter(Filter nextFilter) {
		if (nextFilter instanceof ConcurrentFilter){
			ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
			this.next = sequentialNext;
			sequentialNext.prev = this;
			if (this.output == null){
				this.output = new LinkedBlockingQueue<String>();
			}
			sequentialNext.input = this.output;
		} else {
			throw new RuntimeException("Should not attempt to link dissimilar filter types.");
		}
	}
	
	public Filter getNext() {
		return next;
	}
	
	public boolean hasNext() {
		if (this.next != null) {
			return true;
		}
		return false;
	}
	
	public void process(){
		while (!isDone()){
			String line = input.poll();
//			if(!line.equals("COMPLETED")) {
				String processedLine = processLine(line);
				if (processedLine != null){
					output.add(processedLine);
				}
//			}
		}	
		//To indicate this thread is completed
		output.add("COMPLETED");
	}
	
	public void run() {
		process();
	}
	
	@Override
	public boolean isDone() {
		//For each filter that requires input, an extra line "COMPLETED" is added to the output queue
		//Only when that line is the head of the input queue, meaning the filter execution is completed
		//For any other cases, the filter execution is not completed
		if(input.peek() != null && input.peek().equals("COMPLETED")) {
			return true;
		} else {
//			if(input.peek().equals("COMPLETED")) {
//				return true;
//			}
			return false;
		}
	}
	
	protected abstract String processLine(String line);
	
}
