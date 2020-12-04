package com.lanshifu.baselibraryktx;

/**
 * @author lanxiaobin
 * @date 2020/8/22
 */
public class Test {
    public static void methodA() {
        int a = 1;
        methodB();
        methodC();
    }

    public static void methodB() {
        int d = 0;
        methodC();
    }

    public static Obj methodC(){
        Obj obj = new Obj();
        obj.doSomeThing();
        return obj;
    }

    static class Obj{

        String doSomeThing(){
            return "";
        }
    }

}
