package org.uma.jmetal.example.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.agemoeaii.AGEMOEAIIBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.lab.visualization.plot.PlotFront;
import org.uma.jmetal.lab.visualization.plot.impl.Plot2D;
import org.uma.jmetal.lab.visualization.plot.impl.Plot3D;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.cf.*;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.maf.MaF01;
import org.uma.jmetal.problem.multiobjective.maf.MaF02;
import org.uma.jmetal.qualityindicator.QualityIndicatorUtils;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;

import java.io.IOException;
import java.util.List;

/** Class to configure and run the AGE-MOEA-II algorithm */
public class AGEMOEAIIRunner extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws JMetalException, IOException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    problem = new DTLZ1();
    String referenceParetoFront = "resources/referenceFrontsCSV/DTLZ1.3D.csv";

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 30.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm =
        new AGEMOEAIIBuilder<>(problem)
            .setCrossoverOperator(crossover)
            .setMutationOperator(mutation)
            .setMaxIterations(300)
            .setPopulationSize(200)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    List<DoubleSolution> population = algorithm.getResult();
    long computingTime = algorithmRunner.getComputingTime();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);

    if (!referenceParetoFront.equals("")) {
      QualityIndicatorUtils.printQualityIndicators(
              SolutionListUtils.getMatrixWithObjectiveValues(population),
              VectorUtils.readVectors(referenceParetoFront, ","));
    }

    if (problem.getNumberOfObjectives() == 3) {
      PlotFront plot = new Plot3D(new ArrayFront(population).getMatrix(), problem.getName() + " (AGE-MOEA-II)");
      plot.plot();
    } else if (problem.getNumberOfObjectives() == 2){
      PlotFront plot = new Plot2D(new ArrayFront(population).getMatrix(), problem.getName() + " (AGE-MOEA-II)");
      plot.plot();
    }

    //PlotFront plot = new PlotSmile(new ArrayFront(population).getMatrix(), problem.getName() + " (AGE-MOEA)") ;
    //plot.plot();
  }
}
