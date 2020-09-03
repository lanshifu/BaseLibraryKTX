package com.lanshifu.baselibraryktx;

/**
 * @author lanxiaobin
 * @date 2020/8/31
 *
 * /**
 * * VM agrs: -Xms20M -Xmx20M -Xmn10M -XX:+PrintGCDetails
 * * -XX:SurvivorRatio=8
 *
 */
public class MinorGCTest {
    
    private static final int _1MB = 1024 * 1024;

    public static void testAllocation() {
        byte[] a1, a2, a3, a4;
        a1 = new byte[2 * _1MB];
        a2 = new byte[2 * _1MB];
        a3 = new byte[2 * _1MB];
        a4 = new byte[1 * _1MB];
    }

    public static void main(String[] agrs) {
        testAllocation();
    }
}
