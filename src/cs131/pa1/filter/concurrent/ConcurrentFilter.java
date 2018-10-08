package cs131.pa1.filter.concurrent;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
			if(line!=null && !line.equals("COMPLETED")) {
				String processedLine = processLine(line);
				if (processedLine != null){
					output.add(processedLine);
				}
			}	
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
		
		//For the case that previous filter has not completed
		if(input.peek() == null) {
			return false;
		} else if (!input.peek().equals("COMPLETED")){
			//For the case that not all the output from the previous filter has been dealed with
			return false;
		} else {
			return true;
		}
	}
	
	protected abstract String processLine(String line);
	
}
