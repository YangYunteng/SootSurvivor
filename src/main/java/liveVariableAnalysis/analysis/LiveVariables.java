package liveVariableAnalysis.analysis;

import soot.Unit;

import java.util.List;

public interface LiveVariables<T> {

    List<T> getLiveVariablesBefore(Unit unit);

    List<T> getLiveVariablesAfter(Unit unit);
}
