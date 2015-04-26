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
