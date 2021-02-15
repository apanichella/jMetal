package org.uma.jmetal.problem.multiobjective.lz09;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LZ09F2
 */
@SuppressWarnings("serial")
public class LZ09F2 extends AbstractDoubleProblem {

  private LZ09 lz09;

  /**
   * Creates a default LZ09F2 problem (30 variables and 3 objectives)
   */
  public LZ09F2() {
    this(21, 1, 22);
  }

  /**
   * Creates a LZ09F2 problem instance
   */
  public LZ09F2(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    setNumberOfVariables(30);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F2");

    lz09 = new LZ09(getNumberOfVariables(),
            objectives().length,
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
    List<Double> y = new ArrayList<Double>(objectives().length);

    for (int i = 0; i < getNumberOfVariables(); i++) {
      x.add(solution.getVariable(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (int i = 0; i < objectives().length; i++) {
      solution.setObjective(i, y.get(i));
    }
    return solution ;
  }
}

