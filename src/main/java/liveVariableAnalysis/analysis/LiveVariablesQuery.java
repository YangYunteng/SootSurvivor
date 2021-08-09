package liveVariableAnalysis.analysis;

import liveVariableAnalysis.flowsets.LiveVariablesFlowSet;
import soot.Local;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.DirectedGraph;

import java.util.HashMap;
import java.util.List;

public class LiveVariablesQuery implements LiveVariables<Local> {
    HashMap<Unit, LiveVariablesFlowSet> liveVariablesBeforeUnit;
    HashMap<Unit, LiveVariablesFlowSet> liveVariablesAfterUnit;

    public LiveVariablesQuery(DirectedGraph<Unit> graph) {
        LiveVariablesAnalysis analysis = new LiveVariablesAnalysis(graph);
        liveVariablesBeforeUnit = new HashMap(graph.size() * 2 + 1, 0.7f);
        liveVariablesAfterUnit = new HashMap(graph.size() * 2 + 1, 0.7f);
        for (Unit unit : graph) {
            LiveVariablesFlowSet before = analysis.getFlowBefore(unit);
            for (Local local : before.toList())
                System.out.print(local + "\t");
            System.out.println();
            System.out.println((Stmt) unit);
            LiveVariablesFlowSet after = analysis.getFlowAfter(unit);
            for (Local local : after.toList())
                System.out.print(local + "\t");
            System.out.println();
            liveVariablesBeforeUnit.put(unit, before);
            liveVariablesAfterUnit.put(unit, after);
        }
    }

    @Override
    public List<Local> getLiveVariablesBefore(Unit unit) {
        if (liveVariablesBeforeUnit.containsKey(unit))
            return liveVariablesBeforeUnit.get(unit).toList();
        return null;
    }

    @Override
    public List<Local> getLiveVariablesAfter(Unit unit) {
        if (liveVariablesAfterUnit.containsKey(unit))
            return liveVariablesAfterUnit.get(unit).toList();
        return null;
    }
}
