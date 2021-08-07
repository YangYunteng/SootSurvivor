package analysis_framework.flowsets;

import com.sun.corba.se.spi.ior.ObjectKey;
import soot.EquivTo;
import soot.toolkits.scalar.AbstractFlowSet;
import soot.toolkits.scalar.FlowSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ValueArraySparseSet extends AbstractFlowSet {
    protected static int DEFAULT_SIZE = 8;
    protected int numElements;
    protected int maxElements;
    protected Object[] elements;

    public ValueArraySparseSet() {
        maxElements = DEFAULT_SIZE;
        elements = new Object[DEFAULT_SIZE];
        numElements = 0;
    }

    public ValueArraySparseSet(int size) {
        maxElements = size;
        numElements = 0;
        elements = new Object[size];
    }

    protected ValueArraySparseSet(ValueArraySparseSet other) {
        numElements = other.numElements;
        maxElements = other.maxElements;
        elements = (Object[]) (other.elements.clone());
    }

    protected boolean sameType(Object flowSet) {
        return (flowSet instanceof ValueArraySparseSet);
    }

    @Override
    public AbstractFlowSet clone() {
        return new ValueArraySparseSet(this);
    }

    public AbstractFlowSet emptySet() {
        return new ValueArraySparseSet();
    }

    public void clear() {
        numElements = 0;
    }

    @Override
    public int size() {
        return numElements;
    }

    @Override
    public boolean isEmpty() {
        return numElements == 0;
    }

    @Override
    public Iterator iterator() {
        return Arrays.asList(elements).iterator();
    }

    @Override
    public List toList() {
        Object[] copiedElements = new Object[numElements];
        System.arraycopy(elements, 0, copiedElements, 0, numElements);
        return Arrays.asList(copiedElements);
    }

    @Override
    public void add(Object o) {
        if (!contains(o)) {
            if (numElements == maxElements)
                doubleCapacity();
            elements[numElements++] = o;
        }
    }

    protected void doubleCapacity() {
        int newSize = maxElements * 2;
        Object[] newElements = new Object[newSize];
        System.arraycopy(elements, 0, newElements, 0, numElements);
        elements = newElements;
        maxElements = newSize;
    }

    @Override
    public void remove(Object o) {
        int i = 0;
        while (i < numElements) {
            //因为不需要考虑顺序问题，只需要将最后一个元素填补上来即可
            if (elements[i].equals(o)) {
                elements[i] = elements[--numElements];
                return;
            } else
                i++;
        }
    }

    public void union(FlowSet otherFlow, FlowSet destFlow) {
        if (sameType(otherFlow) && sameType(destFlow)) {
            ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
            ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;
            if (dest == other) {
                for (int i = 0; i < this.numElements; i++) {
                    dest.add(this.elements[i]);
                }
            } else {
                if (this != dest)
                    copy(dest);
                for (int i = 0; i < other.numElements; i++) {
                    dest.add(other.elements[i]);
                }
            }
        } else {
            super.union(otherFlow, destFlow);
        }
    }

    public void intersection(FlowSet otherFlow, FlowSet destFlow) {
        if (sameType(otherFlow) && sameType(destFlow)) {
            ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
            ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;
            ValueArraySparseSet workingSet;
            //如果出现相同，则代表着如果两者出现交集，一定是在这两者之间
            //并且如果你疑惑是否进行去重操作的话，function add()中进行了contain()检查
            if (other == dest || dest == this) {
                workingSet = new ValueArraySparseSet();
            } else {
                workingSet = dest;
                workingSet.clear();
            }

            for (int i = 0; i < this.numElements; i++) {
                if (other.contains(this.elements[i]))
                    workingSet.add(this.elements[i]);
            }
            if (workingSet != dest)
                workingSet.copy(dest);
        }
    }

    public void difference(FlowSet otherFlow, FlowSet destFlow) {
        if (sameType(otherFlow) && sameType(destFlow)) {
            ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;
            ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;
            ValueArraySparseSet workingSet;
            if (dest == other || dest == this)
                workingSet = new ValueArraySparseSet();
            else {
                workingSet = dest;
                workingSet.clear();
            }

            for (int i = 0; i < numElements; i++) {
                if (!other.contains(this.elements[i]))
                    workingSet.add(this.elements[i]);
            }
            //最后将不是同一变量的workingSet copy到dest
            if (workingSet != dest)
                workingSet.copy(dest);
        }
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < numElements; i++) {
            if (elements[i] instanceof EquivTo && ((EquivTo) elements[i]).equivTo(o))
                return true;
            else if (elements[i].equals(o))
                return true;
        }
        return false;
    }

    public boolean equals(Object otherFlow) {
        if (sameType(otherFlow)) {
            ValueArraySparseSet other = (ValueArraySparseSet) otherFlow;

            if (other.numElements != this.numElements)
                return false;
            int size = this.numElements;
            for (int i = 0; i < size; i++)
                if (!other.contains(this.elements[i]))
                    return false;
            return true;
        } else
            return super.equals(otherFlow);
    }

    @Override
    public void copy(FlowSet destFlow) {
        if (sameType(destFlow)) {
            ValueArraySparseSet dest = (ValueArraySparseSet) destFlow;
            while (dest.maxElements < this.maxElements)
                dest.doubleCapacity();
            dest.numElements = this.numElements;
            System.arraycopy(this.elements, 0, dest.elements, 0, numElements);
        } else {
            super.copy(destFlow);
        }
    }

}
