package liveVariableAnalysis;

import liveVariableAnalysis.wheelAnalysis.ShowWheelLive;
import soot.*;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class WheelMain {
    private static final String sourceDir = "assets";
    private static final String className = "lab3";


    public static void main(String[] args) {
        Utils.setUpSoot(sourceDir);
        SootClass lab3Class = Scene.v().getSootClass(className);
        for (SootMethod sm : lab3Class.getMethods()) {
            System.out.println("---------Method:" + sm.getName() + "------------");

            Body body = sm.getActiveBody();
            BriefUnitGraph briefUnitGraph = new BriefUnitGraph(body);
            ShowWheelLive showWheelLive = new ShowWheelLive(briefUnitGraph);
        }
    }
}
