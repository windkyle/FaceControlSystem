package com.xlauncher.fgs.util;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :
 **/
@Component
public class StartListener implements ApplicationContextAware {
    private static Logger LOGGER = Logger.getLogger(StartListener.class);
    @Autowired
    private ThreadRunUtil threadRunUtil;
    /**
     *
     * @param applicationContext applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("[StartListener setApplicationContext]");
        LOGGER.info("服务启动!");
        // 配置多个设置ip
        Map<String, String> map = ReadFileUtil.readFile();
        String deviceIp = map.get("device.ip");
        String[] deviceIps = deviceIp.split(",");
        for (String deviceIp1 : deviceIps) {
            threadRunUtil.threadTask(deviceIp1);
//            // 单个ip分别启动线程
//            ThreadUtil threadUtil = new ThreadUtil();
//            threadUtil.setDeviceAllIp(deviceIp1);
//            Thread thread = new Thread(threadUtil);
//            thread.start();
            System.out.println("---------------------------------");
        }
    }

}
