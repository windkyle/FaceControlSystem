package com.xlauncher.fgs.util;

import com.xlauncher.fgs.entity.DeviceConfig;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/15 0015
 * @Desc :
 **/
@Component
public class ThreadUtil implements Runnable{

    private static Logger LOGGER = Logger.getLogger(ThreadUtil.class);
    private String deviceAllIp;

    public ThreadUtil() {
    }

    public void setDeviceAllIp(String deviceAllIp) {
        this.deviceAllIp = deviceAllIp;
    }

    /**
     * 设备注册状态值(注册成功：>=0 ; 注册失败：<0)
     */
    private static final String REGISTERS = "registers";

    /**
     * 线程任务，调用海康SDK、注册设备、登录设备、获得设备状态、抓取图片等
     */
    private void prepareThread(){
        while (!Thread.currentThread().isInterrupted()){
            Map<String, String> map = ReadFileUtil.readFile();
            // 配置设备的信息
            DeviceConfig deviceConfig = new DeviceConfig();
            System.out.println("******ip******" + deviceAllIp);
            deviceConfig.setDeviceIP(deviceAllIp);
            deviceConfig.setDevicePort(Integer.parseInt(map.get("device.port")));
            deviceConfig.setDeviceUserName(map.get("device.userName"));
            deviceConfig.setDevicePassWord(map.get("device.password"));
            LOGGER.info("[配置设备的信息]" + deviceConfig.toString());
            LOGGER.info("[GetStreamService.getInstance] 初始化SDK");
            GetStreamService getStreamService = GetStreamService.getInstance(deviceConfig, 1);
            if (getStreamService == null) {
                LOGGER.error("[GetStreamService.getInstance is null!]");
                continue;
            }

            Map<String, Object> loginMap = GetStreamService.login(deviceConfig);
            int registers = (int) loginMap.get(REGISTERS);
            if (registers >= 0) {
                // 获取图片流
                try {
                    GetStreamService.getStream(deviceConfig,1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    long mills = Long.parseLong(map.get("sleepTime"));
                    LOGGER.info("线程睡眠等待:Thread.sleep("+ mills +")");
                    Thread.sleep(mills);
                } catch (InterruptedException e) {
                    LOGGER.error("[Thread:InterruptedException]." + e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {
        System.out.println("run.");
        LOGGER.info("启动线程运行run()方法, 调用prepareThread()方法");
        this.prepareThread();
    }
}
