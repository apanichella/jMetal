package org.uma.jmetal.algorithm.multiobjective.abyss;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.localsearch.LocalSearchOperator;
import org.uma.jmetal.operator.localsearch.impl.BasicLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** Created by ajnebro on 11/6/15. */
public class ABYSSTest {
  DoubleProblem problem;
  LocalSearchOperator<DoubleSolution> localSearch;
  MutationOperator<DoubleSolution> mutation;
  Archive<DoubleSolution> archive;

  @Before
  public void setup() {
    problem = new MockProblem();
    archive = new CrowdingDistanceArchive<>(10);
    mutation = new PolynomialMutation(1.0, 20.0);
    localSearch = new BasicLocalSearch<>(2, mutation, new DominanceWithConstraintsComparator<>(), problem);
  }

  @Test
  public void shouldIsStoppingConditionReachedReturnTrueIfTheConditionFulfills() {
    ABYSS abyss;
    int maxEvaluations = 100;
    abyss = new ABYSS(problem, maxEvaluations, 0, 0, 0, 0, null, null, null, 0);

    ReflectionTestUtils.setField(abyss, "evaluations", 101);

    assertTrue(abyss.isStoppingConditionReached());
  }

  @Test
  public void shouldIsStoppingConditionReachedReturnFalseIfTheConditionDoesNotFulfill() {
    int maxEvaluations = 100;
    ABYSS abyss = new ABYSS(problem, maxEvaluations, 0, 0, 0, 0, null, null, null, 0);

    ReflectionTestUtils.setField(abyss, "evaluations", 1);

    assertFalse(abyss.isStoppingConditionReached());
  }

  @Test
  public void shouldInitializationPhaseLeadToAPopulationFilledWithEvaluatedSolutions() {
    int populationSize = 20;
    int numberOfSubRanges = 4;
    DoubleProblem problem = new MockProblem();

    ABYSS abyss =
        new ABYSS(problem, 0, populationSize, 0, 0, 0, null, localSearch, null, numberOfSubRanges);

    abyss.initializationPhase();
    assertEquals(populationSize, abyss.getPopulation().size());
  }

  @Test
  public void shouldReferenceSetUpdateCreateTheTwoRefSetsAfterBeingInvokedTheFirstTime() {
    int populationSize = 20;
    int numberOfSubRanges = 4;
    int referenceSet1Size = 6;
    int referenceSet2Size = 4;

    DoubleProblem problem = new MockProblem();

    ABYSS abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            null,
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();

    assertEquals(referenceSet1Size, abyss.referenceSet1.size());
    assertEquals(referenceSet2Size, abyss.referenceSet2.size());
    assertEquals(
        populationSize - referenceSet1Size - referenceSet2Size, abyss.getPopulation().size());
  }

  @Test
  public void
      shouldReferenceSetUpdateCreateAReducedSizeReferenceSet2IfThePopulationIsNotBigEnough() {
    int populationSize = 10;
    int numberOfSubRanges = 4;
    int referenceSet1Size = 8;
    int referenceSet2Size = 4;

    DoubleProblem problem = new MockProblem();

    ABYSS abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            null,
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();

    assertEquals(referenceSet1Size, abyss.referenceSet1.size());
    assertEquals(populationSize - referenceSet1Size, abyss.referenceSet2.size());
    assertEquals(0, abyss.getPopulation().size());
  }

  /* TODO
    @Test
    public void shouldSubsetGenerationGenerateAllPairWiseCombinationsOfTheRefSets() {
      int populationSize = 10;
      int numberOfSubRanges = 4;
      int referenceSet1Size = 4;
      int referenceSet2Size = 4;
      DoubleProblem problem = new MockProblem();
      abyss = new ABYSS(problem, 0, populationSize, referenceSet1Size, referenceSet2Size, 0, null,
          localSearch, null, numberOfSubRanges);
      abyss.initializationPhase();
      abyss.referenceSetUpdate();
      List<List<DoubleSolution>> list = abyss.subsetGeneration();
      int expectedCombinations = 0;
      for (int i = 0; i < abyss.referenceSet1.size(); i++) {
        for (int j = i + 1; j < abyss.referenceSet1.size(); j++) {
          expectedCombinations++;
        }
      }
      for (int i = 0; i < abyss.referenceSet2.size(); i++) {
        for (int j = i + 1; j < abyss.referenceSet2.size(); j++) {
          expectedCombinations++;
        }
      }
      assertEquals(expectedCombinations, list.size()) ;
      for (List<DoubleSolution> pair : list) {
        assertEquals(2, pair.size()) ;
      }
    }
  */
  @Test
  public void shouldSubsetGenerationProduceAnEmptyListIfAllTheSolutionsAreMarked() {
    int populationSize = 10;
    int numberOfSubRanges = 4;
    int referenceSet1Size = 4;
    int referenceSet2Size = 4;

    DoubleProblem problem = new MockProblem();

    ABYSS abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            null,
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();

    for (DoubleSolution solution : abyss.referenceSet1) {
      solution.attributes().put(ABYSS.SOLUTION_IS_MARKED, true);
    }

    for (DoubleSolution solution : abyss.referenceSet2) {
      solution.attributes().put(ABYSS.SOLUTION_IS_MARKED, true);
    }
    List<List<DoubleSolution>> list = abyss.subsetGeneration();

    assertEquals(0, list.size());
  }

  @Test
  public void shouldSolutionCombinationProduceTheRightNumberOfSolutions() {
    int populationSize = 10;
    int numberOfSubRanges = 4;
    int referenceSet1Size = 4;
    int referenceSet2Size = 4;

    DoubleProblem problem = new MockProblem();

    ABYSS abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            0,
            null,
            localSearch,
            new SBXCrossover(1.0, 20.0),
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();
    List<List<DoubleSolution>> list = abyss.subsetGeneration();
    List<DoubleSolution> combinedSolutions = abyss.solutionCombination(list);

    int expectedValue = combinedSolutions.size() / 2;

    assertEquals(expectedValue, list.size());
  }

  @Test
  public void shouldRestartCreateANewPopulationWithTheRefSet1Solutions() {
    int populationSize = 10;
    int numberOfSubRanges = 4;
    int referenceSet1Size = 4;
    int referenceSet2Size = 4;

    DoubleProblem problem = new MockProblem();

    ABYSS abyss =
        new ABYSS(
            problem,
            0,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            10,
            new CrowdingDistanceArchive<DoubleSolution>(10),
            localSearch,
            new SBXCrossover(1.0, 20.0),
            numberOfSubRanges);

    abyss.initializationPhase();
    abyss.referenceSetUpdate();
    List<List<DoubleSolution>> list = abyss.subsetGeneration();
    List<DoubleSolution> combinedSolutions = abyss.solutionCombination(list);
    for (DoubleSolution solution : combinedSolutions) {
      DoubleSolution improvedSolution = abyss.improvement(solution);
      abyss.referenceSetUpdate(improvedSolution);
    }

    abyss.restart();
    assertEquals(populationSize, abyss.getPopulation().size());
  }

  /** Mock problem */
  @SuppressWarnings("serial")
  private class MockProblem extends AbstractDoubleProblem {
    private JMetalRandom randomGenerator = JMetalRandom.getInstance();

    public MockProblem() {
      setNumberOfVariables(3);
      setNumberOfObjectives(2);
      setNumberOfConstraints(0);
      setName("Fonseca");

      List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
      List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

      for (int i = 0; i < getNumberOfVariables(); i++) {
        lowerLimit.add(-4.0);
        upperLimit.add(4.0);
      }

      setVariableBounds(lowerLimit, upperLimit);
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      solution.objectives()[0] = randomGenerator.nextDouble();
      solution.objectives()[1] = randomGenerator.nextDouble();

      return solution ;
    }

    @Override
    @Deprecated
    public Double getLowerBound(int index) {
      return super.getBoundsForVariables().get(index).getUpperBound();
    }

    @Override
    @Deprecated
    public Double getUpperBound(int index) {
      return super.getBoundsForVariables().get(index).getUpperBound();
    }
  }
}
