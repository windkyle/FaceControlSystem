package com.xlauncher.fgs.util;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/12/19 0019
 * @Desc :RabbitMQ 创建工厂、创建通道
 **/
public class RabbitMqUtil {
    private static Logger logger = Logger.getLogger(RabbitMqUtil.class);
    private static ConnectionFactory factory;

    /**
     * RabbitMQ 连接
     */
    private static Connection connection;

    /**
     * RabbitMQ 通道
     */
    private static Channel channel;

    /**
     * 创建RabbitMQ连接工厂、创建连接、创建通道
     *
     * @return Channel
     */
    public static Channel createMQChannel() {
        Map<String, String> map = ReadFileUtil.readFile();
        // 创建连接工厂
        factory = new ConnectionFactory();
        // 设置RabbitMQ属性
        factory.setHost(map.get("rabbitMQ.ip"));
        factory.setPort(Integer.parseInt(map.get("rabbitMQ.port")));
        factory.setUsername(map.get("rabbitMQ.userName"));
        factory.setPassword(map.get("rabbitMQ.password"));
        factory.setVirtualHost("/");

        try {
            // 创建一个通道
            connection = factory.newConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                channel.close();
                connection.close();
            } catch (IOException e1) {
                logger.error("[RabbitMQ Err]" + e1);
            } catch (TimeoutException e1) {
                e1.printStackTrace();
            }
            logger.error("[RabbitMQ 创建连接工厂、创建连接异常!]" + e);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * 释放资源
     */
    public static void release() {
        try {
            if (channel != null) {
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            logger.error("[RabbitMQ release]关闭错误!" + e);
        }
    }

}
