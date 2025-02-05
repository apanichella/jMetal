package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.replacement;

import java.util.List;
import org.uma.jmetal.solution.Solution;

@FunctionalInterface
public interface Replacement<S extends Solution<?>> {
  enum RemovalPolicy {sequential, oneShot}
  List<S> replace(List<S> currentList, List<S> offspringList) ;
}
