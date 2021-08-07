package analysis_framework.analyses;

import soot.Unit;

import java.util.List;


public interface VeryBusyExpressions {
    /**
     * Returns the list of expressions that are very busy before the specified Unit.
     *
     * @param s the Unit that defines this query
     * @return a list of expressions that are busy before the specified unit in the method
     */
    List getBusyExpressionsBefore(Unit s);

    /**
     * Description Returns the list of expressions that are very busy after the specified Unit
     * @param s the Unit that defines rhis query
     * @return java.util.List a list of expressions that are busy before the specified unit in the method
     * Author YangYunteng
     * Date 2021/8/3
     * Time 14:58
     */
    List getBusyExpressionsAfter(Unit s);
}
