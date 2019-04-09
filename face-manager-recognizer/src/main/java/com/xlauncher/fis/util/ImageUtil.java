package com.xlauncher.fis.util;

import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/8 0008
 * @Desc :对图片byte[]数组进行处理
 **/
public class ImageUtil {
    private static final String SEPARATOR_A = "&";
    private static final String SEPARATOR_B = "=";
    private static Logger logger = Logger.getLogger(ImageUtil.class);

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

    /**
     * base64String 转换成byte[]数组
     *
     * @param string
     * @return
     */
    public static byte[] stringToByte(String string){
        byte[] bytes = new byte[0];
        // 对字节数组Base64解码
        BASE64Decoder decoder = new BASE64Decoder();
        // 返回Base64解码过的字节数组
        try {
            bytes = decoder.decodeBuffer(string);
        } catch (IOException e) {
            logger.error("Err. BASE64字符串转字节数组异常!" + e);
        }
        return bytes;
    }

    /**
     * 将String转换成Map
     *
     * @param string
     * @return
     */
    public static Map<String, String> getStringToMap(String string) {
        if (string == null || "".equals(string)) {
            return null;
        }
        String[] strings = string.split(SEPARATOR_A);
        int mapLength = strings.length;
        if ((strings.length % 2) != 0) {
            mapLength = mapLength + 1;
        }

        Map<String, String> map = new HashMap<>(mapLength);
        for (String string1 : strings) {
            String[] strArray = string1.split(SEPARATOR_B);
            map.put(strArray[0], strArray[1]);
        }
        return map;
    }
}
