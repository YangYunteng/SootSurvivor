package liveVariableAnalysis.flowsets;

import soot.EquivTo;
import soot.Local;
import soot.Value;
import soot.toolkits.scalar.AbstractFlowSet;
import soot.toolkits.scalar.FlowSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

//此类我个人适用于所有可以抽象为bit vector的数据流分析
public class LiveVariablesFlowSet extends AbstractFlowSet<Local> {
    protected static int DEFAULT_SIZE = 8;
    protected int numElements;
    protected int maxElements;
    Local[] liveVariables;

    public LiveVariablesFlowSet() {
        this.numElements = 0;
        this.maxElements = DEFAULT_SIZE;
        this.liveVariables = new Local[this.maxElements];
    }

    public LiveVariablesFlowSet(LiveVariablesFlowSet other) {
        this.numElements = other.numElements;
        this.maxElements = other.maxElements;
        this.liveVariables = (Local[]) (other.liveVariables.clone());
    }

    protected boolean sameType(Object flowSet) {
        return (flowSet instanceof LiveVariablesFlowSet);
    }

    @Override
    public AbstractFlowSet<Local> clone() {
        return new LiveVariablesFlowSet(this);
    }

    public AbstractFlowSet<Local> emptySet() {
        return new LiveVariablesFlowSet();
    }

    @Override
    public boolean isEmpty() {
        return this.numElements == 0;
    }

    public void clear() {
        this.numElements = 0;
        this.liveVariables = new Local[maxElements];
    }

    @Override
    public int size() {
        return this.numElements;
    }

    @Override
    public Iterator<Local> iterator() {
        return Arrays.asList(liveVariables).iterator();
    }

    @Override
    public List<Local> toList() {
        Local[] copied = new Local[numElements];
        System.arraycopy(liveVariables, 0, copied, 0, numElements);
        return Arrays.asList(copied);
    }

    @Override
    public void add(Local local) {
        if (!contains(local)) {
            if (this.maxElements == this.numElements)
                doubleCapacity();
            this.liveVariables[numElements++] = local;
        }
    }

    @Override
    public boolean contains(Local local) {
        for (Local live : liveVariables) {
            //此处不需要进行local instanceof EquivTo, 因为Local extends Value, Value implements EquivTo
            if (local != null && live != null && ((EquivTo) local).equivTo(live))
                return true;
            else if (local != null && local.equals(live))
                return true;
        }
        return false;
    }

    public int where(Local local) {
        if (!contains(local))
            return -1;
        int index = -1;
        for (int i = 0; i < numElements; i++) {
            Local live = liveVariables[i];
            if (local != null && live != null && ((EquivTo) local).equivTo(live))
                index = i;
            else if (local != null && local.equals(live))
                index = i;
        }
        return index;
    }

    public void doubleCapacity() {
        this.maxElements = this.maxElements * 2;
        Local[] temp = new Local[this.maxElements];
        System.arraycopy(this.liveVariables, 0, temp, 0, numElements);
        this.liveVariables = temp;
    }

    @Override
    public void remove(Local local) {
        int index = where(local);
        if (index == -1)
            return;
        this.liveVariables[index] = this.liveVariables[--numElements];
    }

    @Override
    public void union(FlowSet<Local> otherFlowSet, FlowSet<Local> destFlowSet) {
        if (sameType(otherFlowSet) && sameType(destFlowSet)) {
            LiveVariablesFlowSet other = (LiveVariablesFlowSet) otherFlowSet;
            LiveVariablesFlowSet dest = (LiveVariablesFlowSet) destFlowSet;
            if (dest.equals(other)) {
                for (Local local : liveVariables)
                    dest.add(local);
            } else {
                if (this != dest)
                    for (Local local : this.liveVariables)
                        dest.add(local);
                for (Local local : dest.liveVariables)
                    dest.add(local);
            }
        } else
            super.union(otherFlowSet, destFlowSet);
    }

    @Override
    public void intersection(FlowSet<Local> otherFlowSet, FlowSet<Local> destFlowSet) {
        if (sameType(otherFlowSet) && sameType(destFlowSet)) {
            LiveVariablesFlowSet other = (LiveVariablesFlowSet) otherFlowSet;
            LiveVariablesFlowSet dest = (LiveVariablesFlowSet) destFlowSet;
            dest.clear();
            for (int i = 0; i < this.numElements; i++) {
                if (other.contains(this.liveVariables[i]))
                    dest.add(this.liveVariables[i]);
            }
        } else
            super.intersection(otherFlowSet, destFlowSet);
    }

    @Override
    public void difference(FlowSet<Local> otherFlowSet, FlowSet<Local> destFlowSet) {
        if (sameType(otherFlowSet) && sameType(destFlowSet)) {
            LiveVariablesFlowSet other = (LiveVariablesFlowSet) otherFlowSet;
            LiveVariablesFlowSet dest = (LiveVariablesFlowSet) destFlowSet;
            dest.clear();
            for (int i = 0; i < this.numElements; i++) {
                if (!other.contains(this.liveVariables[i]))
                    dest.add(this.liveVariables[i]);
            }
        } else
            super.difference(otherFlowSet, destFlowSet);
    }

    @Override
    public void copy(FlowSet<Local> destFlowSet) {
        if (sameType(destFlowSet)) {
            LiveVariablesFlowSet dest = (LiveVariablesFlowSet) destFlowSet;
            while (dest.maxElements < this.maxElements)
                dest.doubleCapacity();
            dest.numElements = this.numElements;
            System.arraycopy(this.liveVariables, 0, dest.liveVariables, 0, dest.numElements);
        } else {
            super.copy(destFlowSet);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (sameType(o)) {
            LiveVariablesFlowSet other = (LiveVariablesFlowSet) o;
            if (other.numElements != this.numElements)
                return false;
            int size = this.numElements;
            for (Local local : this.liveVariables) {
                if (!other.contains(local))
                    return false;
            }
            return true;
        } else
            return super.equals(o);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), numElements, maxElements);
        result = 31 * result + Arrays.hashCode(liveVariables);
        return result;
    }
}
