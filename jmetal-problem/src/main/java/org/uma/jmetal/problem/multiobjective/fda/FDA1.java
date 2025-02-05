package org.uma.jmetal.problem.multiobjective.fda;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

/** @author Cristóbal Barba <cbarba@lcc.uma.es> */
@SuppressWarnings("serial")
public class FDA1 extends FDA {

  public FDA1(Observable<Integer> observable) {
    this(100, 2);
  }

  public FDA1() {
    this(new DefaultObservable<>("FDA1 observable"));
  }

  public FDA1(Integer numberOfVariables, Integer numberOfObjectives)
      throws JMetalException {
    super();
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setName("FDA1");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    lowerLimit.add(0.0);
    upperLimit.add(1.0);
    for (int i = 1; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] f = new double[solution.objectives().length];
    f[0] = solution.variables().get(0);
    double g = this.evalG(solution);
    double h = this.evalH(f[0], g);
    f[1] = h * g;

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    return solution ;
  }

  /**
   * Returns the value of the FDA1 function G.
   *
   * @param solution Solution
   */
  private double evalG(DoubleSolution solution) {

    double gT = Math.sin(0.5 * Math.PI * time);
    double g = 0.0;
    for (int i = 1; i < solution.variables().size(); i++) {
      g += Math.pow((solution.variables().get(i) - gT), 2);
    }
    g = g + 1.0;
    return g;
  }

  /**
   * Returns the value of the ZDT1 function H.
   *
   * @param f First argument of the function H.
   * @param g Second argument of the function H.
   */
  public double evalH(double f, double g) {
    double h = 1 - Math.sqrt(f / g);
    return h;
  }
}
