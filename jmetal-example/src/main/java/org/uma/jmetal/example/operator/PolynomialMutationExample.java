package org.uma.jmetal.example.operator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.lab.visualization.plot.PlotFront;
import org.uma.jmetal.lab.visualization.plot.impl.PlotSmile;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class is intended to verify the working of the polynomial mutation operator. A figure
 * depicting the values obtained when generating 100000 points, a granularity of 200, and a number
 * of different distribution index values (5, 10, 20) can be found here:
 * <a href="https://github.com/jMetal/jMetal/blob/master/figures/polynomialMutation.png">
   Polynomial mutation</a>
 */
public class PolynomialMutationExample {
  /**
   * Program to generate data representing the distribution of points generated by a polynomial
   * mutation operator. The parameters to be introduced by the command line are:
   * - numberOfSolutions: number of solutions to generate
   * - granularity: number of subdivisions to be considered.
   * - distributionIndex: distribution index of the polynomial mutation operator
   * - outputFile: file containing the results
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) throws FileNotFoundException {
    int numberOfPoints ;
    int granularity ;
    double distributionIndex ;

    if (args.length !=3) {
      JMetalLogger.logger.info("Usage: numberOfSolutions granularity distributionIndex") ;
      JMetalLogger.logger.info("Using default parameters") ;

      numberOfPoints = 10000 ;
      granularity = 100 ;
      distributionIndex = 20.0 ;
    } else {
      numberOfPoints = Integer.parseInt(args[0]);
      granularity = Integer.parseInt(args[1]);
      distributionIndex = Double.parseDouble(args[2]);
    }

    DoubleProblem problem ;

    problem = new Sphere(1) ;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(1.0, distributionIndex) ;

    DoubleSolution solution = problem.createSolution() ;
    solution.variables().set(0, 0.0);

    List<DoubleSolution> population = new ArrayList<>(numberOfPoints) ;
    for (int i = 0 ; i < numberOfPoints ; i++) {
      DoubleSolution newSolution = (DoubleSolution) solution.copy();
      mutation.execute(newSolution) ;
      population.add(newSolution) ;
    }

    population.sort(Comparator.comparingDouble(sol -> sol.objectives()[0])) ;


    double[][] classifier = classify(population, problem, granularity);

    PlotFront plot = new PlotSmile(classifier, "") ;
    plot.plot();
  }

  private static double[][] classify(List<DoubleSolution> solutions, DoubleProblem problem, int granularity) {
    Bounds<Double> bounds = problem.getBoundsForVariables().get(0);
    double grain = (bounds.getUpperBound() - bounds.getLowerBound()) / granularity ;
    double[][] classifier = new double[granularity][] ;
    for (int i = 0 ; i < granularity; i++) {
      classifier[i] = new double[2] ;
      classifier[i][0] = bounds.getLowerBound() + i * grain ;
      classifier[i][1] = 0 ;
    }

    for (DoubleSolution solution : solutions) {
      boolean found = false ;
      int index = 0 ;
      while (!found) {
        if (solution.variables().get(0) <= classifier[index][0]) {
          classifier[index][1] ++ ;
          found = true ;
        } else {
          if (index == (granularity - 1)) {
            classifier[index][1] ++ ;
            found = true ;
          } else {
            index++;
          }
        }
      }
    }

    return classifier ;
  }
}
