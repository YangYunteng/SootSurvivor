package liveVariableAnalysis;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.util.Arrays;

public class Utils {
    public static void setUpSoot(String sourceDir) {
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDir);
        Options.v().set_output_format(Options.output_format_jimple);
        String[] proDirs = {sourceDir};
        Options.v().set_process_dir(Arrays.asList(proDirs));
        Options.v().set_whole_program(true);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
    }
}
