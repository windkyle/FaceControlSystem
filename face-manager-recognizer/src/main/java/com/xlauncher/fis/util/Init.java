package com.xlauncher.fis.util;

import java.util.UUID;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/9 0009
 * @Desc :随机生成32位编号
 **/
public class Init {

    /**
     * 随机生成32位编号
     *
     * @return String
     */
    public static String initialise(){
        String id = UUID.randomUUID().toString();
        id = id.replace("-","");
        return id;
    }

}
