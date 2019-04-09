package com.xlauncher.fis.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/12/19 0019
 * @Desc :RabbitMQ 消息队列参数实体对象
 **/
@Configuration
@PropertySource("classpath:service.properties")
public class RabbitMq {
    /**
     * rabbitMQ
     */
    @Value("${rabbitMQ.ip}")
    private String mqIp;
    @Value("${rabbitMQ.port}")
    private String mqPort;
    @Value("${rabbitMQ.userName}")
    private String mqUserName;
    @Value("${rabbitMQ.password}")
    private String mqPassword;
    @Value("${rabbitMQ.fisQueue}")
    private String fisQueue;
    @Value("${rabbitMQ.chhQueue}")
    private String chhQueue;

    public String getMqIp() {
        return mqIp;
    }

    public void setMqIp(String mqIp) {
        this.mqIp = mqIp;
    }

    public String getMqPort() {
        return mqPort;
    }

    public void setMqPort(String mqPort) {
        this.mqPort = mqPort;
    }

    public String getMqUserName() {
        return mqUserName;
    }

    public void setMqUserName(String mqUserName) {
        this.mqUserName = mqUserName;
    }

    public String getMqPassword() {
        return mqPassword;
    }

    public void setMqPassword(String mqPassword) {
        this.mqPassword = mqPassword;
    }

    public String getFisQueue() {
        return fisQueue;
    }

    public void setFisQueue(String fisQueue) {
        this.fisQueue = fisQueue;
    }

    public String getChhQueue() {
        return chhQueue;
    }

    public void setChhQueue(String chhQueue) {
        this.chhQueue = chhQueue;
    }

    @Override
    public String toString() {
        return "RabbitMq{" +
                "mqIp='" + mqIp + '\'' +
                ", mqPort='" + mqPort + '\'' +
                ", mqUserName='" + mqUserName + '\'' +
                ", mqPassword='" + mqPassword + '\'' +
                ", fisQueue='" + fisQueue + '\'' +
                ", chhQueue='" + chhQueue + '\'' +
                '}';
    }
}
