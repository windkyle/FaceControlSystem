package com.xlauncher.fgs.util.face;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author 白帅雷
 * @date 2019-02-19
 */
public class DateTimeUtil {

    private static final String FORMAT = "yyyy-MM-dd-HH-mm-ss";

    /**
     * 获得格式化时间
     *
     * @param time time
     * @return String
     */
    public static String getFormatTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT);
        return dateFormat.format(new Date(time).getTime());
    }

}
