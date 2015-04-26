package de.learnlib.example.cav2015.stack;

public class BoundedArrayStack {
	
	public static final int MAX_CAPACITY = 3;
	private final int[] storage = new int[MAX_CAPACITY];
	private int size = 0;
	
	public void push(int value) {
		if (size >= MAX_CAPACITY) throw new FullException();
		storage[size++] = value;
	}
	
	public int pop() {
		if (size <= 0) throw new EmptyException();
		return storage[--size];
	}

}
