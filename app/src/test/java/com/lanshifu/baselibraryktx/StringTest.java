package com.lanshifu.baselibraryktx;

/**
 * @author lanxiaobin
 * @date 2020/10/18
 */
public class StringTest {

    public static void main(String[] agrs) {
        testFindString("good5google","google");
    }


    /**
     * 母串中找到子串
     * @param fromStr
     * @param findStr
     */
    private static void testFindString(String fromStr,String findStr) {

        for (int i = 0; i < fromStr.length(); i++) {
            char start = fromStr.charAt(i);
            if (start == findStr.charAt(0)){

                int sameCount = 0;
                //第一个一样，后面继续判断
                for (int j =0;j<findStr.length();j++){
                    if (fromStr.charAt( i + j) != findStr.charAt(j)){
                        break;
                    }
                    sameCount++;
                }

                if (sameCount == findStr.length()){
                    System.out.println("找到了，下标是："+i);
                }
            }

        }

    }

}
