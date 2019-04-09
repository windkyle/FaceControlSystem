package com.xlauncher.fgs.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/28 0028
 * @Desc :
 **/
@Component
public class ThreadRunUtil {

    /**
     * 执行线程任务
     *
     * @param deviceIp1 设备IP
     */
    @Async
    void threadTask(String deviceIp1) {
        ThreadUtil threadUtil = new ThreadUtil();
        threadUtil.setDeviceAllIp(deviceIp1);
        System.out.println("ThreadName."+ Thread.currentThread().getName());
    }
}
