package liveVariableAnalysis.wheelAnalysis;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

public class WheelLiveAnalysis extends BackwardFlowAnalysis<Unit, FlowSet<Local>> {
    ArraySparseSet<Local> emptySet;

    WheelLiveAnalysis(DirectedGraph<Unit> graph) {
        super(graph);
        emptySet = new ArraySparseSet<>();
        doAnalysis();
    }

    @Override
    protected void flowThrough(FlowSet<Local> inSet, Unit unit, FlowSet<Local> outSet) {
        kill(inSet, unit, outSet);
        gen(outSet, unit);
    }

    private void gen(FlowSet<Local> outSet, Unit unit) {
        for (ValueBox use : unit.getUseBoxes()) {
            Value val = use.getValue();
            if (val instanceof Local)
                outSet.add((Local) val);
        }
    }

    private void kill(FlowSet<Local> inSet, Unit unit, FlowSet<Local> outSet) {
        //将inSet 中内容copy至outSet
        inSet.copy(outSet);
        for (ValueBox def : unit.getDefBoxes()) {
            Value val = def.getValue();
            if (val instanceof Local)
                outSet.remove((Local) val);
        }
    }

    @Override
    protected FlowSet<Local> newInitialFlow() {
        return emptySet.clone();
    }

    @Override
    protected void merge(FlowSet<Local> in1, FlowSet<Local> in2, FlowSet<Local> out) {
        in1.union(in2, out);
    }

    @Override
    protected void copy(FlowSet<Local> in, FlowSet<Local> out) {
        in.copy(out);
    }
}
