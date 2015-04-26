package de.learnlib.example.cav2015.stack;

import java.util.Stack;

public class BoundedJavaStack {
	
	private final Stack<Integer> stack = new Stack<>();
	
	public void push(int value) {
		if (stack.size() >= 3) throw new FullException();
		stack.push(value);
	}
	
	public int pop() {
		if (stack.isEmpty()) throw new EmptyException();
		return stack.pop().intValue();
	}

}