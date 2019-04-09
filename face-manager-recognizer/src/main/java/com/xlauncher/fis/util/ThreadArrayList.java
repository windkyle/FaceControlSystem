package com.xlauncher.fis.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;


/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/03/20 0020
 * @Desc :
 **/
@Component
public class ThreadArrayList implements Runnable{
    private static Logger logger = Logger.getLogger(ThreadArrayList.class);
    /**
     * 初次识别没有结果，将map放入列表中，根据预设时间进行再次识别线程任务
     */
    private void task() {
       System.out.println("------------------->.1");
        while(true){
            ArrayListInstance arrayListInstance = ArrayListInstance.getInstance();
            arrayListInstance.intoList(false, null);
            try {
                logger.info("Thread.sleep(30000)!");
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                logger.error("Err.ArrayListInstance ERROR!");
            }
        }
    }

    @Override
    public void run() {
        logger.info("定时执行再次识别线程任务!");
        task();
    }
}
