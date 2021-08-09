package liveVariableAnalysis.analysis;

import liveVariableAnalysis.flowsets.LiveVariablesFlowSet;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

public class LiveVariablesAnalysis extends BackwardFlowAnalysis<Unit, LiveVariablesFlowSet> {
    private final LiveVariablesFlowSet emptySet;

    LiveVariablesAnalysis(DirectedGraph<Unit> graph) {
        super(graph);
        emptySet = new LiveVariablesFlowSet();
        doAnalysis();
    }

    @Override
    protected LiveVariablesFlowSet entryInitialFlow() {
        return (LiveVariablesFlowSet) emptySet.clone();
    }

    @Override
    protected LiveVariablesFlowSet newInitialFlow() {
        return (LiveVariablesFlowSet) emptySet.clone();
    }

    @Override
    protected void flowThrough(LiveVariablesFlowSet inFlowSet, Unit unit, LiveVariablesFlowSet outFlowSet) {
        kill(inFlowSet, unit, outFlowSet);
        gen(outFlowSet, unit);
    }

    private void gen(LiveVariablesFlowSet outFlowSet, Unit unit) {
        LiveVariablesFlowSet gens = new LiveVariablesFlowSet();
        for (ValueBox defBox : unit.getDefBoxes()) {
            Value def = defBox.getValue();
            if (def instanceof Local)
                gens.add((Local) def);
        }
        outFlowSet.union(gens, outFlowSet);
    }

    private void kill(LiveVariablesFlowSet inFlowSet, Unit unit, LiveVariablesFlowSet outFlowSet) {
        LiveVariablesFlowSet kills = new LiveVariablesFlowSet();
        for (ValueBox useBox : unit.getUseBoxes()) {
            Value use = useBox.getValue();
            if (use instanceof Local)
                kills.add((Local) use);
        }
        inFlowSet.difference(kills, outFlowSet);
    }


    @Override
    protected void merge(LiveVariablesFlowSet in1, LiveVariablesFlowSet in2, LiveVariablesFlowSet out) {
        in1.union(in2, out);
    }

    @Override
    protected void copy(LiveVariablesFlowSet in, LiveVariablesFlowSet out) {
        in.copy(out);
    }
}
