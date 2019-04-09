package com.xlauncher.fis.util;

import com.google.gson.Gson;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :float[]转换字符串
 **/
public class FloatUtil {
    private static Gson gson = new Gson();

    /**
     * float[]数组转换成字符串
     *
     * @param floats
     * @return
     */
    public static String implode(float[][] floats) {
        // float[]数组转换成字符串
        return gson.toJson(floats);

    }

    /**
     * 字符串转换float[]
     *
     * @param str
     * @return
     */
    public static float[][] explode(String str) {
        // 字符串转换float[]
        return gson.fromJson(str, float[][].class);
    }
}
