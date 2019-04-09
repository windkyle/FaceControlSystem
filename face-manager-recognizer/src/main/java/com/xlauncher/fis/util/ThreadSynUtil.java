package com.xlauncher.fis.util;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/20 0020
 * @Desc :
 **/
@Component
public class ThreadSynUtil implements Runnable {
    private static ScheduledExecutorService executorService;
    private static Logger logger = Logger.getLogger(ThreadSynUtil.class);
    @Autowired
    private TemplateUtil templateUtil;
    @Autowired
    private PropertiesUtil propertiesUtil;
    private int index = 0;

    /**
     * 定时任务：同步运营云用户注册信息
     */
    public void timeTask() {
        long period = Long.parseLong(propertiesUtil.getValue("period.time"));
        logger.info("[定时任务：同步运营云用户信息!]");
        Runnable runnable = () -> {
            index ++;
            logger.info("[*START轮询*]第[" + index + "]次。间隔[" + period + "]分钟");
            getSynUser();
        };
        executorService = Executors
                .newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(runnable, 0, period, TimeUnit.MINUTES);

    }

    /**
     * 运营云同步注册用户信息(方式一：全量：接口+接口)
     */
    private void getSynUser() {
        logger.info("运营云同步注册用户信息(方式一：全量：接口+接口)");
        templateUtil.queryPersonByDateTime();
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {
        logger.info("___run.timeTask");
        // 当服务配置时，调用此方法，立即关闭正在执行的定时任务
        executorService.shutdownNow();
        timeTask();
    }

}
