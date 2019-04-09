package com.xlauncher.fis.web;

import com.xlauncher.fis.entity.FacePredict;
import com.xlauncher.fis.service.FacePredictService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :人脸预测识别
 **/
@RestController
public class FacePredictController {

    @Autowired
    private FacePredictService facePredictService;
    private static Logger logger = Logger.getLogger(FacePredictController.class);
    private final static int len = 10;

    /**
     * 查询预测识别结果(分页)
     *
     * @param blocName 集团名称
     * @param hotelName 酒店名称
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @param page 页码数
     * @param token token令牌
     * @return Map
     */
    @ApiOperation(value = "查询预测识别结果(分页)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "blocName", value = "集团名称", dataType = "String"),
            @ApiImplicitParam(name = "hotelName", value = "酒店名称", dataType = "String"),
            @ApiImplicitParam(name = "hotelId", value = "酒店编号", dataType = "String"),
            @ApiImplicitParam(name = "queryStartTime", value = "查询条件开始时间", dataType = "String"),
            @ApiImplicitParam(name = "queryEndTime", value = "查询条件结束时间", dataType = "String"),
            @ApiImplicitParam(name = "isAbnormal", value = "是否登记", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码数", dataType = "int")
    })
    @GetMapping(value = "/predict/list")
    public Map<String, Object> getListFacePredict(@RequestParam("blocName") String blocName
            , @RequestParam("hotelName") String hotelName, @RequestParam("hotelId") String hotelId
            , @RequestParam("queryStartTime") String queryStartTime, @RequestParam("queryEndTime") String queryEndTime
            , @RequestParam("isAbnormal") int isAbnormal, @RequestParam("page") int page, @RequestHeader("token") String token) {
        logger.info("THIS IS GET FACE PREDICT LIST!");
        Map<String, Object> map = new HashMap<>(1);
        if (queryStartTime.length() == len) {
            queryStartTime += " 00:00:01";
        }
        if (queryEndTime.length() == len) {
            queryEndTime += " 23:59:59";
        }
        List<FacePredict> facePredictList = facePredictService.listFacePredict(blocName, hotelName, hotelId, queryStartTime, queryEndTime, isAbnormal, (page - 1) * 6);
        map.put("data", facePredictList);
        return map;
    }

    /**
     * count
     *
     * @param blocName 集团名称
     * @param hotelName 酒店名称
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @return Map
     */
    @ApiOperation(value = "查询预测识别结果COUNT数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "blocName", value = "集团名称", dataType = "String"),
            @ApiImplicitParam(name = "hotelName", value = "酒店名称", dataType = "String"),
            @ApiImplicitParam(name = "hotelId", value = "酒店编号", dataType = "String"),
            @ApiImplicitParam(name = "queryStartTime", value = "查询条件开始时间", dataType = "String"),
            @ApiImplicitParam(name = "queryEndTime", value = "查询条件结束时间", dataType = "String"),
            @ApiImplicitParam(name = "isAbnormal", value = "是否异常", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码数", dataType = "int")
    })
    @GetMapping(value = "/predict/count")
    public Map<String, Object> getListFacePredictCount(@RequestParam("blocName") String blocName
            , @RequestParam("hotelName") String hotelName, @RequestParam("hotelId") String hotelId
            , @RequestParam("queryStartTime") String queryStartTime, @RequestParam("queryEndTime") String queryEndTime
            , @RequestParam("isAbnormal") int isAbnormal) {
        logger.info("THIS IS GET FACE PREDICT COUNT!");
        Map<String, Object> map = new HashMap<>(1);
        if (queryStartTime.length() == len) {
            queryStartTime += " 00:00:01";
        }
        if (queryEndTime.length() == len) {
            queryEndTime += " 23:59:59";
        }
        int count = facePredictService.countListFacePredict(blocName, hotelName, hotelId, queryStartTime, queryEndTime, isAbnormal);
        map.put("data", count);
        return map;
    }

    /**
     * 查询具体的预测识别结果
     *
     * @return Map
     */
    @ApiOperation(value = "查询具体的预测识别结果")
    @ApiImplicitParam(name = "id", value = "编号ID", dataType = "int")
    @GetMapping(value = "/predict/{id}")
    public Map<String, Object> getFacePredict(@PathVariable int id, @RequestHeader("token") String token) {
        logger.info("THIS IS GET FACE PREDICT!");
        return facePredictService.getFacePredict(id);
    }

    /**
     * 根据ID查看预测识别图片
     *
     * @param id ID
     * @param httpServletResponse 返回给前端的数据载体
     * @return 获取图片状态的map和HttpServletResponse
     */
    @GetMapping(value = "/predict/image/{id}")
    public String getImgById(@PathVariable int id, HttpServletResponse httpServletResponse) {
        httpServletResponse.setContentType("image/jpg");
        httpServletResponse.setBufferSize(1024 * 24);
        try {
            OutputStream outputStream = httpServletResponse.getOutputStream();
            byte[] imgData = this.facePredictService.getImgData(id);
            if (imgData != null) {
                outputStream.write(imgData);
                outputStream.flush();
                outputStream.close();
            }
            return null;
        } catch (IOException e) {
            logger.error("获取图片失败！" + e.getMessage());
        }
        return "SUCCESS";
    }
}
