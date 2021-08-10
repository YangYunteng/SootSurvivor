package liveVariableAnalysis;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        ArrayList<A> lists = new ArrayList<>();
        lists.add(new A(1));
        lists.add(new A(2));
        A a = lists.get(0);
        A temp = a;
        lists.remove(temp);
        System.out.println(lists.get(0).num);
    }
}

class A {
    int num;

    A(int num) {
        this.num = num;
    }
}
