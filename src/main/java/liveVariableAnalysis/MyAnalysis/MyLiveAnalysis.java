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
    private HashMap<Unit, FlowSet<Local>> outSets;
    private HashMap<Unit, Boolean> mark;

    public MyLiveAnalysis(DirectedGraph<Unit> graph) {
        this.graph = graph;
        this.newInSets = new HashMap<>();
        this.oldInSets = new HashMap<>();
        outSets = new HashMap<>();
        this.mark = new HashMap<>();
    }

    public void doAnalysis() {
        initBB();
        do {
            for (Unit unit : graph)
                mark.put(unit, false);
            copyHashMap(newInSets, oldInSets);
            for (Unit tail : graph.getTails()) {
                genInSetAndOutSet(tail);
            }
        } while (change());
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
                return true;
        }
        return false;
    }

    //其实从理论角度，可以直接通过size大小来判断，flow set是否扩张
    private boolean equals(FlowSet<Local> newInSet, FlowSet<Local> oldInSet) {
        if (newInSet.size() > oldInSet.size())
            return false;
        for (Local local : newInSet) {
            if (!oldInSet.contains(local))
                return false;
        }
        return true;
    }

    public void genInSetAndOutSet(Unit unit) {
        mark.put(unit, true);
        FlowSet<Local> outSet = outSets.get(unit);
        outSet.clear();
        FlowSet<Local> inSet = newInSets.get(unit);
        //merge
        for (Unit succ : graph.getSuccsOf(unit))
            outSet.union(newInSets.get(succ), outSet);

        kill(outSet, unit, inSet);
        gen(inSet, unit);

        for (Unit pred : graph.getPredsOf(unit)) {
            FlowSet<Local> tempOutSet = new ArraySparseSet<>();
            for (Unit tempSucc : graph.getSuccsOf(pred))
                tempOutSet.union(newInSets.get(tempSucc), tempOutSet);
            //存在两种情况，如果mark为false 或者 可能当前节点之前已经标记，但是后续的一些节点发生改变，可以再次执行，主要是对于branches的merge
            if (!mark.get(pred) || !equals(tempOutSet, outSets.get(pred)))
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
            outSets.put(unit, new ArraySparseSet<>());
        }
    }

    public DirectedGraph<Unit> getGraph() {
        return graph;
    }

    public void setGraph(DirectedGraph<Unit> graph) {
        this.graph = graph;
    }

    public void printLocalsBefore(Unit unit) {
        System.out.print("Before:(" + newInSets.get(unit).size() + ")");
        for (Local local : newInSets.get(unit)) {
            System.out.print(local + "\t");
        }
        System.out.println();
    }
}
