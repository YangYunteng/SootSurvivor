package liveVariableAnalysis.MyAnalysis;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;

import java.util.HashMap;

public class MyLiveAnalysis {
    private DirectedGraph<Unit> graph;
    private HashMap<Unit, FlowSet<Local>> newInSets;
    private HashMap<Unit, FlowSet<Local>> oldInSets;

    public MyLiveAnalysis(DirectedGraph<Unit> graph) {
        this.graph = graph;
        this.newInSets = new HashMap<>();
        this.oldInSets = new HashMap<>();
    }

    public void doAnalysis() {
        initBB();
        do {
            copyHashMap(newInSets, oldInSets);
            for (Unit tail : graph.getTails()) {
                genInSetAndOutSet(tail);
            }
//            System.out.println();
        } while (!change());
    }

    private void copyHashMap(HashMap<Unit, FlowSet<Local>> newInSets, HashMap<Unit, FlowSet<Local>> oldInSets) {
        for (Unit unit : graph) {
            oldInSets.put(unit, newInSets.get(unit));
        }
    }

    public boolean change() {
        for (Unit unit : graph) {
            FlowSet<Local> oldInSet = oldInSets.get(unit);
            FlowSet<Local> newInSet = newInSets.get(unit);
            if (!equals(newInSet, oldInSet))
                return false;
            System.out.println("change");
            System.out.println(unit);
        }
        return true;
    }

    //其实从理论角度，可以直接通过size大小来判断，flow set是否扩张
    private boolean equals(FlowSet<Local> newInSet, FlowSet<Local> oldInSet) {
        for (Local local : newInSet) {
            if (!oldInSet.contains(local))
                return false;
        }
        return true;
    }

    public void genInSetAndOutSet(Unit unit) {
        FlowSet<Local> outSet = new ArraySparseSet<>();
        FlowSet<Local> inSet = newInSets.get(unit);
        //merge
        for (Unit succ : graph.getSuccsOf(unit))
            outSet.union(newInSets.get(succ), outSet);

        kill(outSet, unit, inSet);
        gen(inSet, unit);

        for (Unit pred : graph.getPredsOf(unit)) {
            genInSetAndOutSet(pred);
        }
    }


    private void gen(FlowSet<Local> inSet, Unit unit) {
        for (ValueBox vb : unit.getUseBoxes()) {
            Value v = vb.getValue();
            if (v instanceof Local)
                inSet.add((Local) v);
        }
    }

    private void kill(FlowSet<Local> outSet, Unit unit, FlowSet<Local> inSet) {
        outSet.copy(inSet);
        for (ValueBox vb : unit.getDefBoxes()) {
            Value v = vb.getValue();
            if (v instanceof Local)
                inSet.remove((Local) v);
        }
    }


    public void initBB() {
        for (Unit unit : graph) {
            oldInSets.put(unit, new ArraySparseSet<>());
            newInSets.put(unit, new ArraySparseSet<>());
        }
    }

    public DirectedGraph<Unit> getGraph() {
        return graph;
    }

    public void setGraph(DirectedGraph<Unit> graph) {
        this.graph = graph;
    }

}
