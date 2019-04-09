package com.xlauncher.fis.service.impl;

import com.xlauncher.fis.service.PropertiesService;
import com.xlauncher.fis.util.PropertiesUtil;
import com.xlauncher.fis.util.ThreadSynUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/20 0020
 * @Desc :运营云服务、RabbitMQ消息队列服务配置
 **/
@Service
public class PropertiesServiceImpl implements PropertiesService{

    @Autowired
    private PropertiesUtil propertiesUtil;
    @Autowired
    private ThreadSynUtil threadSynUtil;
    private static Logger logger = Logger.getLogger(PropertiesServiceImpl.class);

    /**
     * 查询服务配置信息
     *
     * @return Map
     */
    @Override
    public Map<String, Object> getService() {
        logger.info("[getService] 查询服务配置信息");
        Map<String, Object> map = new HashMap<>(1);
        String ip = propertiesUtil.getValue("cloud.ip");
        String port = propertiesUtil.getValue("cloud.port");
        String time = propertiesUtil.getValue("unit.time");
        String period = propertiesUtil.getValue("period.time");
        // 运营云服务
        map.put("ip", ip);
        map.put("port", port);
        map.put("time", time);
        map.put("period", period);

        // RabbitMQ消息队列服务
        map.put("mqIp", propertiesUtil.getValue("rabbitMQ.ip"));
        map.put("mqPort", propertiesUtil.getValue("rabbitMQ.port"));
        map.put("mqUserName", propertiesUtil.getValue("rabbitMQ.userName"));
        map.put("mqPassword", propertiesUtil.getValue("rabbitMQ.password"));
        map.put("mqQueue", propertiesUtil.getValue("rabbitMQ.fisQueue"));
        return map;
    }

    /**
     * 添加（更新）服务配置信息
     *
     * @param map map
     * @return Map
     */
    @Override
    public Map<String, Object> addService(Map<String, Object> map) {
        logger.info("[addService] 添加（更新）服务配置信息! map." + map);
        Map<String, Object> map1 = propertiesUtil.setService(map);
        logger.info("[ThreadSynUtil] 运营云同步用户信息!");
        threadSynUtil.run();
        return map1;

    }
}
