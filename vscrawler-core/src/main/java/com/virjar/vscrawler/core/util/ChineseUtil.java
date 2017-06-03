package com.virjar.vscrawler.core.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.virjar.vscrawler.core.constant.CommonConstant;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * <p>
 * description:中文处理工具包
 * </p>
 * 
 * @author mario1oreo
 * @since 2017-6-3 13:02:18
 * @see
 */
public class ChineseUtil {

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     * 
     * @param chines
     *            汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    String[] strResult = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strResult != null && strResult.length > 0) {
                        pinyinName.append(strResult[0].charAt(0));
                    } else {
                        pinyinName.append(nameChar[i]);
                    }

                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        if (pinyinName.toString().contains("u:")) {
            return pinyinName.toString().replaceAll("u:", "v");
        } else {
            return pinyinName.toString();
        }
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     * 
     * @param chines
     *            汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        // long start = System.currentTimeMillis();
        StringBuffer pinyinName = new StringBuffer();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    String[] strResult = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);
                    if (strResult != null && strResult.length > 0) {
                        pinyinName.append(strResult[0]);
                    } else {
                        pinyinName.append(nameChar[i]);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        // long end = System.currentTimeMillis();
        // System.out.println("cost time : " + (end - start));
        if (pinyinName.toString().contains("u:")) {
            return pinyinName.toString().replaceAll("u:", "v");
        } else {
            return pinyinName.toString();
        }
    }

    public static boolean matches(String pattern, String source) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern argument cannot be null.");
        }
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        return m.matches();
    }

    public static String matchesChineseValue(String source) {
        return source.replaceAll("[^\u4e00-\u9fa5]", "");
    }


    /**
     *
     * <p>
     * description:JsonArrayString 中的key 由中文转英文
     * </p>
     *
     * @param jsonStr
     * @return jsonArrayString
     * @author mario1oreo
     * @since 2017-6-3 12:54:24
     * @see
     */
    public static String jsonArrayKeyC2P(String jsonStr) {
        return jsonArrayKeyC2P(jsonStr, true);
    }

    public static String jsonArrayKeyC2P(String jsonStr, boolean replace) {
        if (StringUtils.isBlank(jsonStr) || !jsonStr.startsWith("[")) {
            return "jsonString is null or blank , or jsonStr not start with '[',can not be converted!";
        }
        return jsonConvertKeyChineseToPinyin(jsonStr, CommonConstant.JSONStringType.JSON_ARRAY.getValue(), replace);
    }

    /**
     *
     * <p>
     * description:JsonObjectString 中的key 由中文转英文
     * </p>
     *
     * @param jsonStr
     * @return JsonObjectString
     * @author mario1oreo
     * @since 2017-6-3 12:54:19
     * @see
     */
    public static String jsonObjectKeyC2P(String jsonStr) {
        return jsonObjectKeyC2P(jsonStr, true);
    }

    public static String jsonObjectKeyC2P(String jsonStr, boolean replace) {
        if (StringUtils.isBlank(jsonStr) || !jsonStr.startsWith("{")) {
            return "jsonString is null or blank , or jsonStr not start with '{',can not be converted!";
        }
        return jsonConvertKeyChineseToPinyin(jsonStr, CommonConstant.JSONStringType.JSON_OBJECT.getValue(), replace);
    }

    public static String jsonConvertKeyChineseToPinyin(String sourceJsonString, String type, boolean replace) {
        if (StringUtils.isBlank(sourceJsonString)) {
            return StringUtils.EMPTY;
        }
        JSONArray result = new JSONArray();
        if (CommonConstant.JSONStringType.JSON_ARRAY.getValue().equals(type)) {
            JSONArray sourceJsonArray = JSONArray.parseArray(sourceJsonString);
            for (Object object : sourceJsonArray) {
                JSONObject jsonObj = new JSONObject();
                Set<String> keySet = ((JSONObject) object).keySet();
                for (String key : keySet) {
                    String value = ((JSONObject) object).getString(key);
                    if (replace) {
                        if (!ChineseUtil.matches("[\\u4e00-\\u9fa5]+", key)) {
                            String pinyinKey = ChineseUtil.converterToSpell(ChineseUtil.matchesChineseValue(key));
//                            System.out.println(pinyinKey+"\t"+key);
                            jsonObj.put(pinyinKey, value);
                        } else {
                            jsonObj.put(ChineseUtil.converterToSpell(key), value);
//                            System.out.println(ChineseUtil.converterToSpell(key)+"\t"+key);
                        }
                    } else {
                        jsonObj.put(ChineseUtil.converterToSpell(key), value);
//                        System.out.println(ChineseUtil.converterToSpell(key)+"\t"+key);
                    }
                }
                result.add(jsonObj);
            }
            return result.toJSONString();
        } else if (CommonConstant.JSONStringType.JSON_OBJECT.getValue().equals(type)) {
            JSONObject jsonObj = new JSONObject();
            JSONObject sourceJsonObj = JSONObject.parseObject(sourceJsonString);
            Set<String> keySet = sourceJsonObj.keySet();
            for (String key : keySet) {
                String value = sourceJsonObj.getString(key);
                if (replace) {
                    jsonObj.put(ChineseUtil.converterToSpell(ChineseUtil.matchesChineseValue(key)), value);
                } else {
                    jsonObj.put(ChineseUtil.converterToSpell(key), value);
                }
            }
            result.add(jsonObj);
            return jsonObj.toJSONString();
        } else {
            return "json type Error , can not be converted!";
        }
    }
    public static void main(String[] args) {

        String temp = "alkals哈哈哈：";
        System.out.printf(matchesChineseValue(temp));
    }
}
