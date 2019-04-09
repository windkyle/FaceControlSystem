package com.xlauncher.fis.web;

import com.xlauncher.fis.service.BlocHotelService;
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
 * @Date :2019/3/5 0005
 * @Desc :
 **/
@RestController
public class BlocHotelController {
    @Autowired
    private BlocHotelService blocHotelService;
    private static Logger logger = Logger.getLogger(BlocHotelController.class);
    /**
     * 查询集团信息
     *
     * @param token token令牌
     * @return Map
     */
    @ApiOperation(value = "查询集团信息")
    @GetMapping(value = "/bloc")
    public Map<String, Object> queryBloc(@RequestHeader("token") String token) {
        logger.info("THIS IS GET QUERY BLOC!");
        return blocHotelService.queryBloc();
    }

    /**
     * 查询酒店信息
     *
     * @param blocName 集团名称
     * @param token token令牌
     * @return Map
     */
    @ApiOperation(value = "查询酒店信息")
    @ApiImplicitParam(name = "blocName", value = "集团名称", dataType = "String")
    @GetMapping(value = "/hotel")
    public Map<String, Object> queryHotel(@RequestHeader("token") String token, @RequestParam("blocName") String blocName) {
        logger.info("THIS IS GET QUERY HOTEL!");
        return blocHotelService.queryHotel(blocName);
    }
}
