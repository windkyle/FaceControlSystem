package com.xlauncher.fgs.util;

import sun.misc.BASE64Encoder;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/8 0008
 * @Desc :对图片byte[]数组进行处理
 **/
public class ImageUtil {

    /**
     * 将Map转换成String
     *
     * @param map
     * @return
     */
    public static String getMapToString(Map<String, Object> map) {
        Set<String> keySet = map.keySet();
        // 将set集合转换成数组
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        // 给数组进行排序（升序）
        Arrays.sort(keyArray);
        // 因为String拼接效率会很低，所以转用StringBuilder。
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<keyArray.length;i++) {
            // 参数值为空，则不参与签名
            if (map.get(keyArray[i]).toString().trim().length() > 0) {
                stringBuilder.append(keyArray[i]).append("=").append(map.get(keyArray[i]).toString().trim());
            }
            if(i != keyArray.length-1){
                stringBuilder.append("&");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 图片byte[]数组转换base64String
     *
     * @param bytes
     * @return
     */
    public static String base64ToString(byte[] bytes) {
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        // 返回Base64编码过的字节数组字符串
        return encoder.encode(bytes);
    }
}
