package com.xlauncher.fis.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/20 0020
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertiesUtilTest {
    @Autowired
    PropertiesUtil propertiesUtil;
    @Test
    public void getValue() throws Exception {
        System.out.println("getValue." + propertiesUtil.getValue("model_version"));
    }

    @Test
    public void setService() throws Exception {
        Map<String, Object> map = new HashMap<>(1);
        map.put("ip","8.11.0.11");
        map.put("port","8000");
        map.put("time", "30");
        propertiesUtil.setService(map);
    }

}