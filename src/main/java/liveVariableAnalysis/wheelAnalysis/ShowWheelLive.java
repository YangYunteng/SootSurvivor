package liveVariableAnalysis.wheelAnalysis;

import soot.Local;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.FlowSet;

import java.util.HashMap;

public class ShowWheelLive {
    HashMap<Unit, FlowSet<Local>> unitToLiveBefore;
    HashMap<Unit, FlowSet<Local>> unitToLiveAfter;

    public ShowWheelLive(DirectedGraph<Unit> graph) {
        unitToLiveAfter = new HashMap<>();
        unitToLiveBefore = new HashMap<>();
        WheelLiveAnalysis wheelLiveAnalysis = new WheelLiveAnalysis(graph);
        for (Unit unit : graph) {
            FlowSet<Local> before = wheelLiveAnalysis.getFlowBefore(unit);
            unitToLiveBefore.put(unit, before);
            FlowSet<Local> after = wheelLiveAnalysis.getFlowAfter(unit);
            unitToLiveAfter.put(unit, after);
            System.out.print("Before: ");
            for (Local local : before)
                System.out.print(local + "\t");
            System.out.println();

            System.out.println("Stmt: " + unit);

//            System.out.print("After: ");
//            for (Local local : after)
//                System.out.print(local + "\t");
//            System.out.println();
        }
    }
}
