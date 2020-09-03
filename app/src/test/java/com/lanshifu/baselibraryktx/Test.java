package com.lanshifu.baselibraryktx;

/**
 * @author lanxiaobin
 * @date 2020/8/22
 */
public class Test {
    public static int add(int a) {
        int b = 2;
        int c = 3;
        return a + b + c;
    }

    public static void methodA() {
        int a = 1;
        methodB();
        methodC();
    }

    public static void methodB() {
        int d = 0;
        methodC();
    }

    public static void methodC(){

    }

}
