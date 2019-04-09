package com.xlauncher.fis.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/7 0007
 * @Desc :根据身份证的号码算出当前身份证持有者的性别和年龄
 **/
public class UserCardUtil {
    private static final int EIGHTEEN = 18;
    private static final int FIFTEEN = 15;

    /**
     * 根据身份证的号码算出当前身份证持有者的年龄
     *
     * @param card
     * @return
     */
    public static int getUserAge(String card) {
        int age = 0;
        if (card.length() == EIGHTEEN) {
            // 得到年份
            String year = card.substring(6).substring(0, 4);
            // 得到月份
            String yue = card.substring(10).substring(0, 2);
            // 得到当前的系统时间
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // 当前年份
            String fYear = format.format(date).substring(0, 4);
            // 月份
            String fYue = format.format(date).substring(5, 7);
            // 当前月份大于用户出身的月份表示已过生
            if (Integer.parseInt(yue) <= Integer.parseInt(fYue)) {
                age = Integer.parseInt(fYear) - Integer.parseInt(year) + 1;
            } else {
                // 当前用户还没过生
                age = Integer.parseInt(fYear) - Integer.parseInt(year);
            }

        } else if (card.length() == FIFTEEN) {
            // 年份
            String uYear = "19" + card.substring(6, 8);
            // 月份
            String uYue = card.substring(8, 10);
            // 得到当前的系统时间
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // 当前年份
            String fYear = format.format(date).substring(0, 4);
            // 月份
            String fYue = format.format(date).substring(5, 7);
            // 当前月份大于用户出身的月份表示已过生
            if (Integer.parseInt(uYue) <= Integer.parseInt(fYue)) {
                age = Integer.parseInt(fYear) - Integer.parseInt(uYear) + 1;
            } else {
                // 当前用户还没过生
                age = Integer.parseInt(fYear) - Integer.parseInt(uYear);
            }
        }
        return age;
    }

    /**
     * 根据身份证的号码算出当前身份证持有者的性别
     *
     * @param card
     * @return
     */
    public static int getUserSex(String card) {
        int sex = 0;
        if (card.length() == EIGHTEEN) {
            // 判断性别
            if (Integer.parseInt(card.substring(16).substring(0, 1)) % 2 == 0) {
                sex = 0;
            } else {
                sex = 1;
            }
        } else if (card.length() == FIFTEEN) {
            // 用户的性别
            String uSex = card.substring(14, 15);
            if (Integer.parseInt(uSex) % 2 == 0) {
                sex = 0;
            } else {
                sex = 1;
            }
        }
        return sex;
    }

}
