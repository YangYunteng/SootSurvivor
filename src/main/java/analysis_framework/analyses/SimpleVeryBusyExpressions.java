package analysis_framework.analyses;

import analysis_framework.flowsets.ValueArraySparseSet;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.BinopExpr;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

import java.util.*;

public class SimpleVeryBusyExpressions implements VeryBusyExpressions {
    private Map unitToExpressionsAfter;
    private Map unitToExpressionsBefore;

    public SimpleVeryBusyExpressions(DirectedGraph graph) {
        SimpleVeryBusyAnalysis analysis = new SimpleVeryBusyAnalysis(graph);
        unitToExpressionsAfter = new HashMap(graph.size() * 2 + 1, 0.7f);
        unitToExpressionsBefore = new HashMap(graph.size() * 2 + 1, 0.7f);

        Iterator unitIt = graph.iterator();
        while (unitIt.hasNext()) {
            Unit s = (Unit) unitIt.next();
            FlowSet set = (FlowSet) analysis.getFlowBefore(s);
            unitToExpressionsBefore.put(s, Collections.unmodifiableList(set.toList()));
        }
    }

    @Override
    public List getBusyExpressionsBefore(Unit s) {
        return null;
    }

    @Override
    public List getBusyExpressionsAfter(Unit s) {
        return null;
    }
}

//backward and must analysis
class SimpleVeryBusyAnalysis extends BackwardFlowAnalysis {

    private FlowSet emptySet;

    public SimpleVeryBusyAnalysis(DirectedGraph graph) {
        super(graph);
        emptySet = new ValueArraySparseSet();
        doAnalysis();
    }

    @Override
    protected void merge(Object in1, Object in2, Object out) {
        FlowSet inSet1 = (FlowSet) in1;
        FlowSet inSet2 = (FlowSet) in2;
        FlowSet outSet = (FlowSet) out;
        inSet1.intersection(inSet2, outSet);
    }

    @Override
    protected void copy(Object src, Object dest) {
        FlowSet srcSet = (FlowSet) src;
        FlowSet destSet = (FlowSet) dest;
        srcSet.copy(destSet);
    }

    /**
     * Used to initialize the in and out sets for each node. In
     * our case we want to build up the sets as we go, so we
     * initialize with the empty set.
     * </p><p>
     * Note: If we had information about all the possible values
     * the sets could contain, we could initialize with that and
     * then remove values during the analysis.
     * @return an empty set
     */
    @Override
    protected Object newInitialFlow() {
        return emptySet.clone();
    }
    //理论上我认为这边是应当定义为全集的，但是可能代码实现和理论层面还是存在出入吧
    @Override
    protected Object entryInitialFlow(){
        return emptySet.clone();
    }

    @Override
    protected void flowThrough(Object in, Object node, Object out) {
        FlowSet inSet = (FlowSet) in,
                outSet = (FlowSet) out;
        Unit u = (Unit) node;
        kill(inSet, u, outSet);
        gen(outSet, u);
    }

    private void kill(FlowSet inSet, Unit u, FlowSet outSet) {
        FlowSet kills = (FlowSet) emptySet.clone();
        //迭代Unit的defBox
        for (ValueBox defBox : u.getDefBoxes()) {
            //检查是否是局部变量
            if (defBox.getValue() instanceof Local) {
                //对于inSet中每个二元表达式进行迭代，去除其中被修改过的变量
                for (Object expr : inSet) {
                    BinopExpr e = (BinopExpr) expr;
                    for (ValueBox useBox : e.getUseBoxes()) {
                        if (useBox.getValue() instanceof Local && useBox.getValue().equivTo(defBox.getValue()))
                            kills.add(e);
                    }
                }
            }
        }
        inSet.difference(kills, outSet);
    }

    private void gen(FlowSet outSet, Unit u) {
        for (ValueBox useBox : u.getUseBoxes()) {
            if (useBox.getValue() instanceof BinopExpr)
                outSet.add(useBox.getValue());
        }
    }

}