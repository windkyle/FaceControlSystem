package com.xlauncher.fis.service;

import com.xlauncher.fis.entity.FacePredict;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :人脸预测识别Service层
 **/
@Service
public interface FacePredictService {

    /**
     * 将人脸预测识别结果存入数据库
     *
     * @param map map
     * @return int
     */
    int addFacePredict(Map<String, String> map);

    /**
     *  查询预测识别结果
     *
     * @param blocName 集团名称
     * @param hotelName 酒店名称
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @param page 页码数
     * @return List
     */
    List<FacePredict> listFacePredict(@RequestParam("blocName") String blocName
            , @RequestParam("hotelName") String hotelName, @Param("hotelId") String hotelId
            , @Param("queryStartTime") String queryStartTime, @Param("queryEndTime") String queryEndTime
            , @Param("isAbnormal") int isAbnormal, @Param("page") int page);

    /**
     * COUNT数
     *
     * @param blocName 集团名称
     * @param hotelName 酒店名称
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @return List
     */
    int countListFacePredict(@RequestParam("blocName") String blocName
            , @RequestParam("hotelName") String hotelName, @Param("hotelId") String hotelId
            , @Param("queryStartTime") String queryStartTime, @Param("queryEndTime") String queryEndTime
            , @Param("isAbnormal") int isAbnormal);

    /**
     * 查看具体预测识别结果
     *
     * @param id id
     * @return Map
     */
    Map<String, Object> getFacePredict(int id);

    /**
     * 根据ID查看预测识别图片
     *
     * @param id id
     * @return byte
     */
    byte[] getImgData(int id);
}
