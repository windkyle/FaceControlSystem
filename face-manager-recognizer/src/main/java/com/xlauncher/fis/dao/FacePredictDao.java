package com.xlauncher.fis.dao;

import com.xlauncher.fis.dao.mapper.FacePredictMapper;
import com.xlauncher.fis.entity.FacePredict;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :人脸预测识别DAO层
 **/
@Service
public interface FacePredictDao {

    /**
     * 将人脸预测识别结果存入数据库
     *
     * @param facePredict facePredict
     * @return int
     */
    @Insert("INSERT INTO face_predict(predict_time,predict_hotel,user_name,user_age,user_sex,user_card,is_shelter" +
            ",is_abnormal,predict_image,model_calculation,face_photo)" +
            "VALUES(#{predictTime},#{predictHotel},#{userName},#{userAge},#{userSex},#{userCard},#{isShelter}" +
            ",#{isAbnormal},#{predictImage},#{modelCalculation},#{facePhoto})")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "predictTime", column = "predict_time"),
            @Result(property = "predictHotel", column = "predict_hotel"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "isShelter", column = "is_shelter"),
            @Result(property = "isAbnormal", column = "is_abnormal"),
            @Result(property = "predictImage", column = "predict_image"),
            @Result(property = "facePhoto", column = "face_photo"),
            @Result(property = "modelCalculation", column = "model_calculation"),

    })
    int addFacePredict(FacePredict facePredict);


    /**
     *  查询预测识别结果
     *
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @param page 页码数
     * @return List
     */
    @SelectProvider(type = FacePredictMapper.class, method = "listFacePredict")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "predictTime", column = "predict_time"),
            @Result(property = "predictHotel", column = "predict_hotel"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "isShelter", column = "is_shelter"),
            @Result(property = "isAbnormal", column = "is_abnormal"),
            @Result(property = "predictImage", column = "predict_image"),

    })
    List<FacePredict> listFacePredict(@Param("hotelId") String hotelId, @Param("queryStartTime") String queryStartTime
            , @Param("queryEndTime") String queryEndTime, @Param("isAbnormal") int isAbnormal, @Param("page") int page);

    /**
     * COUNT数
     *
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @return List
     */
    @SelectProvider(type = FacePredictMapper.class, method = "countListFacePredict")
    int countListFacePredict(@Param("hotelId") String hotelId, @Param("queryStartTime") String queryStartTime
            , @Param("queryEndTime") String queryEndTime, @Param("isAbnormal") int isAbnormal);

    /**
     * 查询预测分析结果
     *
     * @param id id
     * @return FacePredict
     */
    @Select("SELECT face_predict.id, face_predict.predict_time, face_predict.predict_hotel,face_predict.user_name, face_predict.user_age" +
            ", face_predict.user_sex, face_predict.user_card, face_predict.is_shelter, face_predict.is_abnormal" +
            ", face_predict.predict_image, face_predict.face_photo, face_predict.model_calculation" +
            " FROM face_predict WHERE face_predict.id=#{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "predictTime", column = "predict_time"),
            @Result(property = "predictHotel", column = "predict_hotel"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "isShelter", column = "is_shelter"),
            @Result(property = "isAbnormal", column = "is_abnormal"),
            @Result(property = "predictImage", column = "predict_image"),
            @Result(property = "facePhoto", column = "face_photo"),
            @Result(property = "modelCalculation", column = "model_calculation"),

    })
    FacePredict getFacePredict(@Param("id") int id);

    /**
     * 自测
     *
     * @return
     */
    @Select("SELECT * FROM face_predict")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "predictTime", column = "predict_time"),
            @Result(property = "predictHotel", column = "predict_hotel"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "isShelter", column = "is_shelter"),
            @Result(property = "isAbnormal", column = "is_abnormal"),
            @Result(property = "predictImage", column = "predict_image"),

    })
    List<FacePredict> listImageFacePredict();
}
