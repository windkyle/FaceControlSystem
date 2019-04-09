package com.xlauncher.fis.util;

import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

/**
 * 读取配置文件
 *
 * @author 白帅雷
 * @date 2019-02-19
 */
@Component
public class PropertiesUtil {

    /**
     * 读写properties文件的对象
     */
    private Properties properties;

    /**
     * 添加一个记录器
     */
    private static Logger logger = Logger.getLogger(PropertiesUtil.class);

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 初始化构造函数
     */
    public PropertiesUtil() {
        properties = new Properties();
        this.fileName = checkIfExist();
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
            properties.load(br);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断model.properties在target/classes下是否存在,如果不存在则创建文件
     *
     * @return 返回文件名（包含路径）
     */
    private String checkIfExist() {
        String fileName = PropertiesUtil.class.getClassLoader().getResource("").getPath() + "service.properties";
        logger.info("[配置文件service.properties]: " + fileName);
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("Err.checkIfExist!" + e);
            }
        }
        return fileName;
    }

    /**
     * 读取配置文件内容
     *
     * @param key key
     * @return String
     */
    public String getValue(String key) {
        return properties.getProperty(key);
    }

    /**
     * 得到资源路径
     *
     * @param key 模型
     * @return String
     */
    public String getPath(String key) {
        return PropertiesUtil.class.getClassLoader().getResource("").getPath() + key;
    }

    /**
     * 写入服务配置信息
     *
     * @param map map
     */
    public Map<String, Object> setService(Map<String, Object> map) {
        Map<String, Object> map1 = new HashMap<>(1);
        String ipService = (String) map.get("ip");
        if (ipService != null) {
            setIpService(ipService);
        } else {
            map1.put("code", 401);
            map1.put("result", "NO");
        }

        String portService = (String) map.get("port");
        if (portService != null) {
            setPortService(portService);
        } else {
            map1.put("code", 401);
            map1.put("result", "NO");
        }

        String periodService = (String) map.get("period");
        if (periodService != null) {
            setPeriodService(periodService);
        } else {
            map1.put("code", 401);
            map1.put("result", "NO");
        }

        String timeService = (String) map.get("time");
        if (timeService != null) {
            setTimeService(timeService);
            map1.put("code", 200);
            map1.put("result", "YES");
        } else {
            map1.put("code", 401);
            map1.put("result", "NO");
        }
        setRabbitService(map);

        return map1;
    }

    /**
     * 写入ip服务配置信息（运营云服务地址）
     */
    private void setIpService(String ipService) {
        logger.info("[写入ip服务配置信息（运营云服务地址）]" + ipService);
        properties.setProperty("cloud.ip", ipService);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
            properties.store(bw , "cloud.ip" + ipService);
            bw.close();
        } catch (IOException e) {
            logger.error("[写入ip服务配置信息异常!]" + e);
        }
    }

    /**
     * 写入port服务配置信息（运营云服务端口号）
     */
    private void setPortService(String portService) {
        logger.info("[写入port服务配置信息（运营云服务端口号）]" + portService);
        properties.setProperty("cloud.port", portService);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
            properties.store(bw , "cloud.port" + portService);
            bw.close();
        } catch (IOException e) {
            logger.error("[写入port服务配置信息异常!]" + e);
        }
    }

    /**
     * 写入time服务配置信息（运营云同步时间单位（单位：分钟）内用户信息）
     */
    private void setTimeService(String timeService) {
        logger.info("[写入time服务配置信息（运营云同步时间单位（单位：分钟）内用户信息）]" + timeService);
        properties.setProperty("unit.time", timeService);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
            properties.store(bw , "unit.time" + timeService);
            bw.close();
        } catch (IOException e) {
            logger.error("[写入time服务配置信息异常!]" + e);
        }
    }

    /**
     * 写入period服务配置信息（运营云同步时间间隔（单位：分钟））
     */
    private void setPeriodService(String periodService) {
        logger.info("写入period服务配置信息（运营云同步时间间隔（单位：分钟））" + periodService);
        properties.setProperty("period.time", periodService);
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
            properties.store(bw , "period.time" + periodService);
            bw.close();
        } catch (IOException e) {
            logger.error("[写入period服务配置信息异常!]" + e);
        }
    }

    /**
     * 写入RabbitMQ服务配置
     */
    private void setRabbitService(Map<String, Object> map) {
        logger.info("[写入RabbitMQ服务配置]" + map);
        properties.setProperty("rabbitMQ.ip", String.valueOf(map.get("mqIp")));
        properties.setProperty("rabbitMQ.port", String.valueOf(map.get("mqPort")));
        properties.setProperty("rabbitMQ.userName", String.valueOf(map.get("mqUserName")));
        properties.setProperty("rabbitMQ.password", String.valueOf(map.get("mqPassword")));
        properties.setProperty("rabbitMQ.fisQueue", String.valueOf(map.get("mqQueue")));
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8"));
            properties.store(bw , "RabbitMQ.Service");
            bw.close();
        } catch (IOException e) {
            logger.error("[写入RabbitMQ服务配置异常!]" + e);
        }
    }

}
