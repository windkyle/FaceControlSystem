package com.xlauncher.fis.web;

import com.xlauncher.fis.service.PropertiesService;
import com.xlauncher.fis.service.SynUserService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/20 0020
 * @Desc :服务配置
 **/
@RestController
public class ServiceController {
    private static Logger logger = Logger.getLogger(ServiceController.class);
    @Autowired
    private PropertiesService propertiesService;
    @Autowired
    SynUserService synUserService;

    /**
     * 查询服务配置信息
     *
     * @return Map
     */
    @ApiOperation(value = "查询服务配置信息")
    @GetMapping(value = "/service")
    public Map<String, Object> getService(@RequestHeader("token") String token) {
        logger.info("THIS IS GET SERVICE CONFIGURATION!");
        return propertiesService.getService();
    }

    /**
     * 添加（更新）服务配置信息
     */
    @ApiOperation(value = "添加（更新）服务配置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map", value = "服务配置信息", dataType = "map")
    })
    @PostMapping(value = "/service")
    public Map<String, Object> addService(@RequestBody Map<String, Object> map, @RequestHeader("token") String token) {
        logger.info("THIS IS ADD SERVICE CONFIGURATION!");
        return propertiesService.addService(map);
    }

    @GetMapping(value = "/")
    public String getIndex() {
        return "Hello World! This is Fis!";
    }

}
