package liveVariableAnalysis;


import liveVariableAnalysis.analysis.LiveVariablesQuery;
import soot.*;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;

import java.util.Arrays;

public class Main {
    private static final String sourceDir = "assets";
    private static final String className = "lab3";

    public static void main(String[] args) {
        setUpSoot();
        SootClass lab3Class = Scene.v().getSootClass(className);
        for (SootMethod sm : lab3Class.getMethods()) {
            System.out.println("---------Method:" + sm.getName() + "------------");

            Body body = sm.getActiveBody();
            BriefUnitGraph briefUnitGraph = new BriefUnitGraph(body);
           LiveVariablesQuery liveVariablesQuery = new LiveVariablesQuery(briefUnitGraph);
        }
    }

    public static void setUpSoot() {
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_soot_classpath(sourceDir);
        Options.v().set_output_format(Options.output_format_jimple);
        String[] proDirs = {sourceDir};
        Options.v().set_process_dir(Arrays.asList(proDirs));
        Options.v().set_whole_program(true);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
    }
}
