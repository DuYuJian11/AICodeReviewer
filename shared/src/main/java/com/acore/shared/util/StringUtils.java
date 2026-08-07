package com.acore.shared.util;

import lombok.experimental.UtilityClass;

/**
 * 字符串工具类
 *
 * @author acore
 */
@UtilityClass
public class StringUtils {

    /**
     * 判断字符串是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空白
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 截断字符串到指定长度
     *
     * @param str    原始字符串
     * @param maxLen 最大长度
     * @return 截断后的字符串（超出部分替换为 ...）
     */
    public static String truncate(String str, int maxLen) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLen) {
            return str;
        }
        return str.substring(0, maxLen) + "...";
    }
}