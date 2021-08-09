package analysis_framework.annotations;

import analysis_framework.transformations.VeryBusyExpsTagger;
import soot.PackManager;
import soot.Transform;
import sun.applet.Main;

public class TagBusyExpressions {
    public static void main(String[] args) {
        PackManager.v().getPack("jtp").add(
                new Transform("jtp." + VeryBusyExpsTagger.PHASE_NAME, VeryBusyExpsTagger.v()));
        Main.main(args);
    }
}
