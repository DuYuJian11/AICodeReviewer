package com.acore.shared.util;

import lombok.experimental.UtilityClass;

/**
 * 脱敏工具类
 *
 * @author acore
 */
@UtilityClass
public class DesensitizedUtils {

    /**
     * 手机号脱敏 — 保留前 3 后 4
     */
    public static String mobile(String mobile) {
        if (mobile == null || mobile.length() != 11) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * 邮箱脱敏
     */
    public static String email(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+)", "$1***$3$4");
    }

    /**
     * 姓名脱敏
     */
    public static String name(String name) {
        if (name == null) {
            return null;
        }
        if (name.length() == 1) {
            return name;
        }
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }
}