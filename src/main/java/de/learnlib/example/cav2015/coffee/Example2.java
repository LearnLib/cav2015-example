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
package de.learnlib.example.cav2015.coffee;

import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.visualization.Visualization;
import net.automatalib.words.Alphabet;
import de.learnlib.algorithms.lstargeneric.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.lstargeneric.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.api.EquivalenceOracle.MealyEquivalenceOracle;
import de.learnlib.api.MembershipOracle;
import de.learnlib.eqtests.basic.SimulatorEQOracle;
import de.learnlib.examples.mealy.ExampleCoffeeMachine;
import de.learnlib.examples.mealy.ExampleCoffeeMachine.Input;
import de.learnlib.experiments.Experiment;
import de.learnlib.experiments.Experiment.MealyExperiment;
import de.learnlib.oracles.SimulatorOracle;
import de.learnlib.statistics.SimpleProfiler;

/**
 * This example illustrates an alternative way to learn a model of the
 * coffee machine (cf. {@link Example1}). Unlike in the previous example,
 * this time we do not need to write the main learning loop by hand.
 * Instead, we use the {@link Experiment} class, which takes care of this
 * for us.
 * 
 * @author Malte Isberner
 */
public class Example2 {

	public static void main(String[] args) {
		// Import the target system, and store a reference to its
		// alphabet.
		CompactMealy<Input, String> target =
				ExampleCoffeeMachine.constructMachine();
		Alphabet<Input> alphabet = target.getInputAlphabet();
				
		// Create a simulator membership oracle for the target system
		MembershipOracle.MealyMembershipOracle<Input, String> oracle
			= new SimulatorOracle.MealySimulatorOracle<>(target);
				
		// Create a reference to the effectively used oracle
		MembershipOracle.MealyMembershipOracle<Input, String> effOracle
			= oracle;
		
		// Create the learning algorithm (L*)
		ExtensibleLStarMealy<Input, String> learner
			= new ExtensibleLStarMealyBuilder<Input,String>()
				.withAlphabet(alphabet)
				.withOracle(effOracle)
				.create();
			
		// Create the equivalence oracle. This time we use simulated (i.e.,
		// perfect) equivalence queries, since we have a model of the target
		// system available (this is not the general case, otherwise learning
		// would be pointless).
		MealyEquivalenceOracle<Input, String> eqOracle
			= new SimulatorEQOracle.MealySimulatorEQOracle<>(target);
		
		// Define a learning experiment consisting of
		// - a learning algorithm
		// - an equivalence oracle
		// - a learning alphabet
		MealyExperiment<Input, String> experiment
			= new MealyExperiment<>(learner, eqOracle, alphabet);
		
		// We enable profiling, so we get some more detailed runtime statistics
		// on how long which phase of the algorithm took.
		experiment.setProfile(true);
		
		// Run the experiment, and store the final hypothesis.
		// The question marks below indicate that we do not care about the
		// state and transitions of the Mealy machine we are using;
		// thus MealyMachine<?,I,?,O> is the usual form to refer to a generic
		// Mealy machine.
		MealyMachine<?,Input,?,String> finalHyp
			= experiment.run();
		
		// Print the number of rounds that were required
		System.out.println("Finished learning after " + experiment.getRounds().getCount() + " rounds");
		
		// Print the profiling statistics gathered during learning
		System.out.println(SimpleProfiler.getResults());
		
		// Finally, visualize our hypothesis
		Visualization.visualizeAutomaton(finalHyp, alphabet, true);
	}

}
