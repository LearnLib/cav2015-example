/* Copyright (C) 2015 TU Dortmund
 * This file is part of LearnLib, http://www.learnlib.de/.
 *
 * LearnLib is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 3.0 as published by the Free Software Foundation.
 *
 * LearnLib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LearnLib; if not, see
 * <http://www.gnu.de/documents/lgpl.en.html>.
 */
package de.learnlib.example.cav2015.stack;

import java.util.Stack;

/**
 * An implementation of a bounded integer stack, using a
 * {@link java.util.Stack} internally.
 * 
 * @author Malte Isberner
 */
public class BoundedJavaStack {
	
	private final Stack<Integer> stack = new Stack<>();
	
	/**
	 * Pushes a value on the stack.
	 * @param value the value to push
	 * @throws FullException if the stack is already full
	 */
	public void push(int value) {
		if (stack.size() >= 3) throw new FullException();
		stack.push(value);
	}
	
	/**
	 * Pops a value off the stack.
	 * @return the popped value
	 * @throws EmptyException if the stack is empty
	 */
	public int pop() {
		if (stack.isEmpty()) throw new EmptyException();
		return stack.pop().intValue();
	}

}