package com.xlauncher.fis.util;

import com.rabbitmq.client.*;
import com.xlauncher.fis.entity.RabbitMq;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/12/19 0019
 * @Desc :RabbitMQ 创建工厂、创建通道
 **/
@Component
public class RabbitMqUtil {
    @Autowired
    private RabbitMq rabbitMq;
    private Connection connection;
    private Channel channel;
    private static Logger logger = Logger.getLogger(RabbitMqUtil.class);


    /**
     * 创建RabbitMQ连接工厂、创建连接、创建通道
     *
     * @return Channel
     */
    public Channel createMQChannel() {
        // 创建线程池 CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadPoolExecutor.CallerRunsPolicy());

        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // 设置RabbitMQ属性
        factory.setHost(rabbitMq.getMqIp());
        factory.setPort(Integer.parseInt(rabbitMq.getMqPort()));
        factory.setUsername(rabbitMq.getMqUserName());
        factory.setPassword(rabbitMq.getMqPassword());
        factory.setVirtualHost("/");
        logger.info("[RabbitMQ]创建连接, ip:" + rabbitMq.getMqIp() + ", username:"
                + rabbitMq.getMqUserName() + ", password:" + rabbitMq.getMqPassword());
        // 创建一个连接
        connection = null;
        channel = null;
        try {
            connection = factory.newConnection(executor);
            // 创建一个通道
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            logger.error("[RabbitMQ] 创建连接工厂、创建连接异常!" + e);
        }
        return channel;
    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
            logger.error("[RabbitMQ] 释放资源错误!" + e);
        }
    }
}
