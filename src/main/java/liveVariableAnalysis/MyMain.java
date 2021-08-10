package liveVariableAnalysis;

import liveVariableAnalysis.MyAnalysis.MyLiveAnalysis;
import soot.*;
import soot.toolkits.graph.BriefUnitGraph;


public class MyMain {
    private static final String sourceDir = "assets";
    private static final String className = "lab3";

    public static void main(String[] args) {
        Utils.setUpSoot(sourceDir);
        SootClass lab3Class = Scene.v().getSootClass(className);
        for (SootMethod sm : lab3Class.getMethods()) {
            System.out.println("---------Method:" + sm.getName() + "------------");

            Body body = sm.getActiveBody();
            BriefUnitGraph briefUnitGraph = new BriefUnitGraph(body);
            for(Unit unit:briefUnitGraph)
                System.out.println(unit);
            MyLiveAnalysis myLiveAnalysis = new MyLiveAnalysis(briefUnitGraph);
            myLiveAnalysis.doAnalysis();
            for(Unit unit:briefUnitGraph){
                myLiveAnalysis.printLocalsBefore(unit);
            }
           
        }
    }

}
