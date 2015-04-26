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

import java.util.Random;

import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.util.automata.Automata;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import de.learnlib.algorithms.lstargeneric.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.lstargeneric.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.api.EquivalenceOracle;
import de.learnlib.api.MembershipOracle;
import de.learnlib.api.SUL;
import de.learnlib.drivers.reflect.AbstractMethodInput;
import de.learnlib.drivers.reflect.SimplePOJOTestDriver;
import de.learnlib.eqtests.basic.RandomWordsEQOracle;
import de.learnlib.mapper.Mappers;
import de.learnlib.mapper.StringMapper;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.oracles.SULOracle;


public class Example3 {
	
	/**
	 * This methods learns an implementation of a stack storing integer values,
	 * and returns the model using a string representation for inputs and outputs.
	 * 
	 * @param clazz the Java class of the stack to learn. This class needs to define
	 * a default constructor, a method named "push" taking a single {@code int}
	 * parameter, and a nullary method named "pop". If the class does not fulfill
	 * these requirements, an exception is thrown.
	 * @return a model of the stack
	 * @throws Exception if an error occurrs
	 */
	public static CompactMealy<String,String>
	learnStack(Class<?> clazz) throws Exception {
		// Create a test driver for the specified class
		SimplePOJOTestDriver driver
			= new SimplePOJOTestDriver(clazz);
		
		// Declares the input alphabet. The first parameter is the
		// symbolic name of the action, the second parameter is the name of
		// the method as defined in the source file, and then follow the
		// arguments, if any.
		driver.addInput("push_0", "push", 0);
		driver.addInput("push_1", "push", 1);
		driver.addInput("pop", "pop");
		
		// Store a reference to the target system alphabet
		Alphabet<AbstractMethodInput> alphabet = driver.getInputs();
		
		// A test driver implements the SUL interface, with input symbol
		// type 'AbstractMethodInput' and output symbol type 'AbstractMethodOutput'.
		// In principle, we could now learn this test driver directly
		// (using a SULOracle to convert the SUL into a membership oracle). However,
		// since the AbstractMethodInput objects contain information specific to
		// the class (i.e., the exact method references), comparing models learned
		// from different implementing classes would never succeed, as they would
		// have disjoint alphabets.
		
		// In such a case, a Mapper can help to overcome this problem. In the general
		// sense, a mapper translates abstract inputs to concrete inputs, and concrete
		// outputs to abstract outputs, such that the system itself operates on the
		// concrete level, but the learner only gets to see the abstract level.
		// Here, we will use a very simple abstraction: converting objects from/to their
		// string representation. Since we fixed the action names 'push_0' etc. above,
		// regardless of the implementation, the models returned by this method will
		// 'look' the same.
		StringMapper<AbstractMethodInput> mapper = new StringMapper<>(alphabet);
		
		// Apply the mapper to the SUL, resulting in a mapped SUL.
		SUL<String,String> mappedSUL = Mappers.apply(mapper, driver);
		
		// Define the membership oracle, which operates on the abstract (String)
		// representation, i.e., the mapped SUL.
		MembershipOracle.MealyMembershipOracle<String, String>
			oracle = new SULOracle<>(mappedSUL);
		
		// Reference to the effective oracle
		MembershipOracle.MealyMembershipOracle<String, String> effOracle
			= oracle;
		
		// Create an L* Mealy learner. Note that as the alphabet for learning, we need
		// to specify the *mapped* alphabet, i.e., the one obtained from the mapper.
		ExtensibleLStarMealy<String, String> learner
			= new ExtensibleLStarMealyBuilder<String,String>()
				.withAlphabet(mapper.getMappedInputs())
				.withOracle(effOracle)
				.create();
		
		
		// Start the learning process
		learner.startLearning();
	
		// Use random sampling to approximate equivalence queries
		EquivalenceOracle.MealyEquivalenceOracle<String, String> eqOracle
			= new RandomWordsEQOracle.MealyRandomWordsEQOracle<>(
					oracle, 5, 20, 1000, new Random());
		
		// The main learning loop
		DefaultQuery<String, Word<String>> ce;
		while ((ce = eqOracle.findCounterExample(learner.getHypothesisModel(),
				                                 mapper.getMappedInputs())) != null) {
			System.out.println("Counterexample: " + ce);
			learner.refineHypothesis(ce);
		}
		
		// Return the final hypothesis
		return learner.getHypothesisModel();
	}
	
	public static void main(String[] args) throws Exception {
		// Learn a model of the stack implementation given by class
		// BoundedArrayStack.
		CompactMealy<String,String> stack1
			= learnStack(BoundedArrayStack.class);
		// Learn a model of the stack implementation given by class
		// BoundedJavaStack.
		CompactMealy<String,String> stack2
			= learnStack(BoundedJavaStack.class);
		
		// Check if the two models are equivalent, or if there exists a separating
		// word, i.e., a word that yields different outputs.
		Word<String> sepWord
			= Automata.findSeparatingWord(stack1, stack2, stack1.getInputAlphabet());
		
		if (sepWord == null) {
			// No separating word: models are equivalent
			System.out.println("Models are equivalent");
		}
		else {
			// Found a separating word: output the counterexample
			System.out.println("Models are not equivalent: " + sepWord);
		}
		
		// Finally, show the inferred model
		Visualization.visualizeAutomaton(stack1, stack1.getInputAlphabet(), true);
	}
}