package org.uma.jmetal.experimental.ensemble;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.moead.MOEADDE;
import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.experimental.componentbasedalgorithm.algorithm.multiobjective.smpso.SMPSOWithArchive;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.aggregativefunction.AggregativeFunction;
import org.uma.jmetal.util.aggregativefunction.impl.Tschebyscheff;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class Ensemble2DNSGAIISMPSOMOEAD extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    String problemName;

    problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2";
    String referenceParetoFront = "resources/referenceFrontsCSV/LZ09_F2.csv";

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    Archive<DoubleSolution> archive = new CrowdingDistanceArchive<>(100);

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(150000);

    Algorithm<List<DoubleSolution>> nsgaII =
        new NSGAII<>(
                problem,
                populationSize,
                offspringPopulationSize,
                new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
                new PolynomialMutation(mutationProbability, mutationDistributionIndex),
                termination)
            .withArchive(new NonDominatedSolutionListArchive<>());

    int swarmSize = 100;
    BoundedArchive<DoubleSolution> leadersArchive =
        new CrowdingDistanceArchive<>(swarmSize);

    var evaluation = new SequentialEvaluation<>(problem);

    Algorithm<List<DoubleSolution>> smpso =
        new SMPSOWithArchive(
            (DoubleProblem) problem,
            swarmSize,
            leadersArchive,
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            evaluation,
            termination,
            new NonDominatedSolutionListArchive<>());

    double cr = 1.0;
    double f = 0.5;

    double neighborhoodSelectionProbability = 0.9;
    int neighborhoodSize = 20;
    int maximumNumberOfReplacedSolutions = 2;

    AggregativeFunction aggregativeFunction = new Tschebyscheff();

    Algorithm<List<DoubleSolution>> moead =
        new MOEADDE(
                problem,
                300,
                cr,
                f,
                aggregativeFunction,
                neighborhoodSelectionProbability,
                maximumNumberOfReplacedSolutions,
                neighborhoodSize,
                "resources/weightVectorFiles/moead",
                termination)
            .withArchive(archive);

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(nsgaII);
    algorithmList.add(smpso);
    algorithmList.add(moead);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
        new AlgorithmEnsemble<>(algorithmList, archive);

    algorithmEnsemble.run();

    List<DoubleSolution> population = algorithmEnsemble.getResult();
    JMetalLogger.logger.info(
        "Total execution time : " + algorithmEnsemble.getTotalComputingTime() + "ms");

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    for (Algorithm<List<DoubleSolution>> algorithm : algorithmList) {
      List<DoubleSolution> result =
          population.stream()
              .filter(solution -> algorithm.getName() == solution.attributes().get("ALGORITHM_NAME"))
              .collect(Collectors.toList());

      new SolutionListOutput(result)
          .setVarFileOutputContext(
              new DefaultFileOutputContext("VAR" + algorithm.getName() + ".csv", ","))
          .setFunFileOutputContext(
              new DefaultFileOutputContext("FUN" + algorithm.getName() + ".csv", ","))
          .print();
    }

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }

}
