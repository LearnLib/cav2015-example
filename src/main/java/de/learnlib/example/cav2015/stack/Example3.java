package de.learnlib.example.cav2015.stack;

import java.util.Random;

import net.automatalib.automata.transout.impl.compact.CompactMealy;
import net.automatalib.util.automata.Automata;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;
import de.learnlib.algorithms.lstargeneric.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstargeneric.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.lstargeneric.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.api.EquivalenceOracle;
import de.learnlib.api.MembershipOracle;
import de.learnlib.drivers.reflect.AbstractMethodInput;
import de.learnlib.drivers.reflect.SimplePOJOTestDriver;
import de.learnlib.eqtests.basic.RandomWordsEQOracle;
import de.learnlib.mapper.Mappers;
import de.learnlib.mapper.StringMapper;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.oracles.SULOracle;


public class Example3 {
	
	public static CompactMealy<String,String>
	learnStack(Class<?> clazz) throws Exception {
		SimplePOJOTestDriver driver
			= new SimplePOJOTestDriver(clazz.getConstructor());
		
		driver.addInput("push_0", "push", 0);
		driver.addInput("push_1", "push", 1);
		driver.addInput("pop", "pop");
		
		Alphabet<AbstractMethodInput> alphabet = driver.getInputs();
		
		StringMapper<AbstractMethodInput> mapper = new StringMapper<>(alphabet);
		
		MembershipOracle.MealyMembershipOracle<String, String>
			oracle = new SULOracle<>(Mappers.apply(mapper, driver));
		
		MembershipOracle.MealyMembershipOracle<String, String> effOracle
			= oracle;
		
		ExtensibleLStarMealy<String, String> learner
			= new ExtensibleLStarMealyBuilder<String,String>()
				.withAlphabet(mapper.getMappedInputs())
				.withOracle(effOracle)
				.withCexHandler(ObservationTableCEXHandlers.RIVEST_SCHAPIRE)
				.create();
		
		learner.startLearning();
	
		
		EquivalenceOracle.MealyEquivalenceOracle<String, String> eqOracle
			= new RandomWordsEQOracle.MealyRandomWordsEQOracle<>(
					oracle, 5, 20, 1000, new Random());
		
		DefaultQuery<String, Word<String>> ce;
		
		while ((ce = eqOracle.findCounterExample(learner.getHypothesisModel(),
				                                 mapper.getMappedInputs())) != null) {
			//System.out.println("Refining using " + ce);
			learner.refineHypothesis(ce);
		}
		
		return learner.getHypothesisModel();
	}
	
	public static void main(String[] args) throws Exception {
		CompactMealy<String,String> stack1
			= learnStack(BoundedArrayStack.class);
		CompactMealy<String,String> stack2
			= learnStack(AltStack.class);
		
		Word<String> sepWord
			= Automata.findSeparatingWord(stack1, stack2, stack1.getInputAlphabet());
		
		if (sepWord == null) {
			System.out.println("Models are equivalent");
		}
		else {
			System.out.println("Models are not equivalent: " + sepWord);
		}
	}
}