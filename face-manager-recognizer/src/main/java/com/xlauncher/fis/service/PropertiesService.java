package com.xlauncher.fis.service;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/20 0020
 * @Desc :服务配置
 **/
@Service
public interface PropertiesService {

    /**
     * 查询服务配置信息
     *
     * @return Map
     */
    Map<String, Object> getService();

    /**
     * 添加（更新）服务配置信息
     *
     * @param map map
     * @return Map
     */
    Map<String, Object> addService(Map<String, Object> map);
}
