package org.uma.jmetal.problem.multiobjective.lz09;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem LZ09F1
 */
@SuppressWarnings("serial")
public class LZ09F1 extends AbstractDoubleProblem {
  private LZ09 lz09;

  /**
   * Creates a default LZ09F1 problem (10 variables and 2 objectives)
   **/
  public LZ09F1() {
    this(21, 1, 21);
  }

  /**
   * Creates a LZ09F1 problem instance
   */
  public LZ09F1(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    setNumberOfVariables(10);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F1");

    lz09 = new LZ09(getNumberOfVariables(),
      getNumberOfObjectives(),
      ptype,
      dtype,
      ltype);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    List<Double> x = new ArrayList<Double>(getNumberOfVariables());
    List<Double> y = new ArrayList<Double>(solution.objectives().length);

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x.add(solution.variables().get(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (int i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = y.get(i);
    }
    return solution ;
  }
}
