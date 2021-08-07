package analysis_framework.analyses;

import soot.EquivTo;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

public class FlowAnalysisTemplate extends BackwardFlowAnalysis {
    public FlowAnalysisTemplate(DirectedGraph g) {
        super(g);
        doAnalysis();
    }

    @Override
    protected Object entryInitialFlow() {
        //return new ValueArraySparseSet();
        return null;
    }

    @Override
    protected Object newInitialFlow() {
        return null;
    }

    @Override
    protected void merge(Object in1, Object in2, Object out) {
        //must analysis =>  out <- in1 union in2
//        FlowSet inSet1 = (FlowSet) in1;
//        FlowSet inSet2 = (FlowSet) in2;
//        FlowSet outSet = (FlowSet) out;
//        inSet1.union(inSet2, outSet);
        //may analysis => out <- in1 intersection in2
//        FlowSet inSet1 = (FlowSet) in1;
//        FlowSet inSet2 = (FlowSet) in2;
//        FlowSet outSet = (FlowSet) out;
//        inSet1.intersection(inSet2, outSet);
    }

    @Override
    protected void copy(Object source, Object dest) {
//        FlowSet srcSet = (FlowSet) source,
//                destSet = (FlowSet) dest;
//        srcSet.copy(destSet);
    }

    @Override
    protected void flowThrough(Object in, Object node, Object out) {
//        FlowSet inSet = (FlowSet) in,
//                outSet = (FlowSet) out;
//        Unit u = (Unit) node;
//        //此处的两个方法需要我们人为定义
//        kill(inSet, u, outSet);
//        gen(outSet, u);
    }
}

