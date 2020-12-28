package com.lanshifu.baselibraryktx.slidebar;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gjz on 9/3/16.
 *
 * https://www.cnblogs.com/scetopcsa/p/4724308.html
 */
public class Contact {
    private String index;
    private String name;


    public Contact(String index, String name) {
        this.index = PinyinUtil.INSTANCE.getFristPinyin(name);
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public static List<Contact> getEnglishContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("A", "爱的Abbey"));
        contacts.add(new Contact("A", "爱的Abbey"));
        contacts.add(new Contact("A", "啊啊"));
        contacts.add(new Contact("A", "哎哎哎"));
        contacts.add(new Contact("A", "矮冬瓜"));
        contacts.add(new Contact("B", "Betty"));
        contacts.add(new Contact("B", "笨方法"));
        contacts.add(new Contact("B", "半年内"));
        contacts.add(new Contact("C", "查订单"));
        contacts.add(new Contact("C", "查订单2"));
        contacts.add(new Contact("C", "重新"));
        contacts.add(new Contact("C", "出个"));
        contacts.add(new Contact("C", "传的"));
        contacts.add(new Contact("D", "David"));
        contacts.add(new Contact("D", "Daniel"));
        contacts.add(new Contact("E", "嗯嗯"));
        contacts.add(new Contact("E", "天堂"));
        contacts.add(new Contact("E", "Eva"));
        contacts.add(new Contact("F", "Frances"));
        contacts.add(new Contact("F", "Frank"));
        contacts.add(new Contact("I", "Ivy"));
        contacts.add(new Contact("J", "解决"));
        contacts.add(new Contact("J", "John"));
        contacts.add(new Contact("J", "Jessica"));
        contacts.add(new Contact("K", "Karen"));
        contacts.add(new Contact("K", "Karl"));
        contacts.add(new Contact("K", "Kim"));
        contacts.add(new Contact("L", "Leon"));
        contacts.add(new Contact("L", "Lisa"));
        contacts.add(new Contact("P", "Paul"));
        contacts.add(new Contact("P", "Peter"));
        contacts.add(new Contact("S", "Sarah"));
        contacts.add(new Contact("S", "Steven"));
        contacts.add(new Contact("R", "Robert"));
        contacts.add(new Contact("R", "Ryan"));
        contacts.add(new Contact("T", "Tom"));
        contacts.add(new Contact("T", "Tony"));
        contacts.add(new Contact("W", "Wendy"));
        contacts.add(new Contact("W", "Will"));
        contacts.add(new Contact("W", "William"));
        contacts.add(new Contact("Z", "Zoe"));
        contacts.add(new Contact("Z", "123"));

//        contacts.sort(new Comparator<Contact>() {
//            @Override
//            public int compare(Contact lhs, Contact rhs) {
//                // 获取ascii值
//                int lhs_ascii = lhs.index.toUpperCase().charAt(0);
//                int rhs_ascii = rhs.index.toUpperCase().charAt(0);
//                // 判断若不是字母，则排在字母之后
//                if (lhs_ascii < 65 || lhs_ascii > 90)
//                    return 1;
//                else if (rhs_ascii < 65 || rhs_ascii > 90)
//                    return -1;
//                else
//                    return lhs.index.compareTo(rhs.index);
//            }
//        });

        return contacts;
    }

    private int sort(Contact lhs, Contact rhs) {
        // 获取ascii值
        int lhs_ascii = lhs.index.toUpperCase().charAt(0);
        int rhs_ascii = rhs.index.toUpperCase().charAt(0);
        // 判断若不是字母，则排在字母之后
        if (lhs_ascii < 65 || lhs_ascii > 90)
            return 1;
        else if (rhs_ascii < 65 || rhs_ascii > 90)
            return -1;
        else
            return lhs.index.compareTo(rhs.index);
    }

    public static List<Contact> getChineseContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("B", "白虎"));
        contacts.add(new Contact("C", "常羲"));
        contacts.add(new Contact("C", "嫦娥"));
        contacts.add(new Contact("E", "二郎神"));
        contacts.add(new Contact("F", "伏羲"));
        contacts.add(new Contact("G", "观世音"));
        contacts.add(new Contact("J", "精卫"));
        contacts.add(new Contact("K", "夸父"));
        contacts.add(new Contact("N", "女娲"));
        contacts.add(new Contact("N", "哪吒"));
        contacts.add(new Contact("P", "盘古"));
        contacts.add(new Contact("Q", "青龙"));
        contacts.add(new Contact("R", "如来"));
        contacts.add(new Contact("S", "孙悟空"));
        contacts.add(new Contact("S", "沙僧"));
        contacts.add(new Contact("S", "顺风耳"));
        contacts.add(new Contact("T", "太白金星"));
        contacts.add(new Contact("T", "太上老君"));
        contacts.add(new Contact("X", "羲和"));
        contacts.add(new Contact("X", "玄武"));
        contacts.add(new Contact("Z", "猪八戒"));
        contacts.add(new Contact("Z", "朱雀"));
        contacts.add(new Contact("Z", "祝融"));
        return contacts;
    }

    public static List<Contact> getJapaneseContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("あ", "江户川コナン"));
        contacts.add(new Contact("あ", "油女シノ"));
        contacts.add(new Contact("あ", "犬夜叉"));
        contacts.add(new Contact("か", "旗木カカシ"));
        contacts.add(new Contact("か", "神楽"));
        contacts.add(new Contact("か", "黒崎一護"));
        contacts.add(new Contact("さ", "桜木花道"));
        contacts.add(new Contact("さ", "坂田銀時"));
        contacts.add(new Contact("さ", "殺生丸"));
        contacts.add(new Contact("な", "奈良シカマル"));
        contacts.add(new Contact("は", "旗木カカシ"));
        contacts.add(new Contact("は", "日向ネジ"));
        contacts.add(new Contact("や", "越前リョーマ"));
        contacts.add(new Contact("や", "野比のび太"));
        contacts.add(new Contact("や", "野原しんのすけ"));
        contacts.add(new Contact("ら", "流川楓"));
        return contacts;
    }

}
