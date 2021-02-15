package org.uma.jmetal.problem.multiobjective.cre;

import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CRE24Test {
  @Test
  public void shouldConstructorCreateAProblemWithTheRightProperties() {
    DoubleProblem problem = new CRE24();

    assertEquals(7, problem.getNumberOfVariables());
    assertEquals(2, problem.objectives().length);
    assertEquals(11, problem.getNumberOfConstraints());
    assertEquals("CRE24", problem.getName());
  }

  @Test
  public void shouldEvaluateWorkProperly() {
    DoubleProblem problem = new CRE24();
    DoubleSolution solution = problem.createSolution();
    problem.evaluate(solution);

    assertEquals(7, solution.getNumberOfVariables());
    assertEquals(2, solution.objectives().length);
    assertEquals(11, solution.getNumberOfConstraints());
  }
}
