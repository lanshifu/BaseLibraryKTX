package com.lanshifu.baselibraryktx.slidebar

import com.lanshifu.lib.ext.logd
import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination


/**
 * @author lanxiaobin
 * @date 2020/12/29
 */
object PinyinUtil {
    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    fun getFristPinyin(inputString: String): String {
        val format = HanyuPinyinOutputFormat()
        format.caseType = HanyuPinyinCaseType.LOWERCASE
        format.toneType = HanyuPinyinToneType.WITHOUT_TONE
        format.vCharType = HanyuPinyinVCharType.WITH_V

        val input: CharArray = inputString.trim().toCharArray()
        if (input.isEmpty()){
            return ""
        }
        var output = ""

        try {
            output =
                if (Character.toString(input[0]).matches(Regex("[\\u4E00-\\u9FA5]+"))) {
                    val temp = PinyinHelper.toHanyuPinyinStringArray(input[0], format)
                    temp[0].toUpperCase().substring(0,1)
                } else {
                    Character.toString(input[0]).toUpperCase()
                }
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
        }
        logd("getFristPinyin,inputString=$inputString,output=$output")
        return output
    }

}