package org.uma.jmetal.problem.multiobjective.re;

import static org.uma.jmetal.problem.multiobjective.re.Util.getClosestValue;

import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE25. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE25 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 6;
  private static final double[] diameterFeasibleIntergers = {
    0.009, 0.0095, 0.0104, 0.0118, 0.0128, 0.0132, 0.014, 0.015, 0.0162, 0.0173, 0.018, 0.02, 0.023,
    0.025, 0.028, 0.032, 0.035, 0.041, 0.047, 0.054, 0.063, 0.072, 0.08, 0.092, 0.105, 0.12, 0.135,
    0.148, 0.162, 0.177, 0.192, 0.207, 0.225, 0.244, 0.263, 0.283, 0.307, 0.331, 0.362, 0.394,
    0.4375, 0.5
  };

  /** Constructor */
  public RE25() {
    setNumberOfVariables(3);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("RE25");

    List<Double> lowerLimit = List.of(1.0, 0.6, 0.09);
    List<Double> upperLimit = List.of(70.0, 3.0, 0.5);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = Math.rint(solution.variables().get(0));
    double x2 = solution.variables().get(1);
    double x3 = getClosestValue(diameterFeasibleIntergers, solution.variables().get(2));

    double[] g = new double[numberOfOriginalConstraints];

    double cf = ((4.0 * (x2 / x3) - 1) / (4.0 * (x2 / x3) - 4)) + (0.615 * x3 / x2);
    double fMax = 1000.0;
    double S = 189000.0;
    double G = 11.5 * 1e+6;
    double K = (G * x3 * x3 * x3 * x3) / (8 * x1 * x2 * x2 * x2);
    double lMax = 14.0;
    double lf = (fMax / K) + 1.05 * (x1 + 2) * x3;
    double Fp = 300.0;
    double sigmaP = Fp / K;
    double sigmaPM = 6;
    double sigmaW = 1.25;

    g[0] = -((8 * cf * fMax * x2) / (Math.PI * x3 * x3 * x3)) + S;
    g[1] = -lf + lMax;
    g[2] = -3 + (x2 / x3);
    g[3] = -sigmaP + sigmaPM;
    g[4] = -sigmaP - ((fMax - Fp) / K) - 1.05 * (x1 + 2) * x3 + lf;
    g[5] = sigmaW - ((fMax - Fp) / K);

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

    solution.objectives()[0] = (Math.PI * Math.PI * x2 * x3 * x3 * (x1 + 2)) / 4.0;
    solution.objectives()[1] = g[0] + g[1] + g[2] + g[3] + g[4] + g[5];

    return solution;
  }
}
