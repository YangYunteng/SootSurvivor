package analysis_framework.transformations;

import analysis_framework.analyses.SimpleVeryBusyExpressions;
import analysis_framework.analyses.VeryBusyExpressions;
import soot.*;
import soot.tagkit.ColorTag;
import soot.tagkit.StringTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import sun.java2d.pipe.SpanShapeRenderer;

import java.util.Iterator;
import java.util.Map;

public class VeryBusyExpsTagger extends BodyTransformer {
    public static final String PHASE_NAME = "vbetagger";
    public static final String TAG_TYPE = "Busy Expressions";


    /**
     * 根据Soot的代码风格，采用的是单例模式的写法（singleton）
     */
    private static VeryBusyExpsTagger instance = new VeryBusyExpsTagger();

    private VeryBusyExpsTagger() {
    }

    public static VeryBusyExpsTagger v() {
        return instance;
    }

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
        VeryBusyExpressions vbe = new SimpleVeryBusyExpressions(new ExceptionalUnitGraph(body));

        for (Unit unit : body.getUnits()) {
            for (Object v : vbe.getBusyExpressionsAfter(unit)) {
                unit.addTag(new StringTag("Busy expression: " + (Value) v, TAG_TYPE));
                for (ValueBox use : unit.getUseBoxes()) {
                    if (use.getValue().equivTo(v))
                        use.addTag(new ColorTag(ColorTag.RED, TAG_TYPE));
                }
            }
        }
    }
}
