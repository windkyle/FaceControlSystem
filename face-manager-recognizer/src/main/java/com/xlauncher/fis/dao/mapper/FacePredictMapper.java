package com.xlauncher.fis.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.Objects;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :人脸预测识别DAO层动态生成SQL
 **/
public class FacePredictMapper {
    /**
     * 数据库：人脸预测识别结果表face_predict
     */
    private static final String TABLE_PREDICT = "face_predict";
    /**
     * 数据库：运营云同步用户注册信息表syn_user
     */
    private static final String TABLE_USER = "syn_user";

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
    public static String listFacePredict(@Param("hotelId") String hotelId, @Param("queryStartTime") String queryStartTime
            , @Param("queryEndTime") String queryEndTime, @Param("isAbnormal") int isAbnormal, @Param("page") int page) {
        return new SQL() {
            {
                SELECT("face_predict.id, face_predict.predict_time, face_predict.predict_hotel, face_predict.is_shelter" +
                        ", face_predict.is_abnormal, face_predict.predict_image, face_predict.user_name" +
                        ", face_predict.user_age, face_predict.user_sex, face_predict.user_card");
                FROM(TABLE_PREDICT);

                if (hotelId != null & !Objects.equals(hotelId, "")) {
                    WHERE("face_predict.predict_hotel=#{hotelId}");
                }
                if (isAbnormal != -1) {
                    WHERE("face_predict.is_abnormal=#{isAbnormal}");
                }
                if (queryStartTime != null & !Objects.equals(queryStartTime, "")) {
                    WHERE("face_predict.predict_time > #{queryStartTime}");
                }
                if (queryEndTime != null & !Objects.equals(queryEndTime, "")) {
                    WHERE("face_predict.predict_time < #{queryEndTime}");
                }
                ORDER_BY("predict_time DESC limit #{page}, 6");
            }
        }.toString();
    }

    /**
     * COUNT数
     *
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @return List
     */
    public static String countListFacePredict(@Param("hotelId") String hotelId, @Param("queryStartTime") String queryStartTime
            , @Param("queryEndTime") String queryEndTime, @Param("isAbnormal") int isAbnormal) {
        return new SQL() {
            {
                SELECT("COUNT(*)");
                FROM(TABLE_PREDICT);

                if (hotelId != null & !Objects.equals(hotelId, "")) {
                    WHERE("face_predict.predict_hotel=#{hotelId}");
                }
                if (isAbnormal != -1) {
                    WHERE("face_predict.is_abnormal=#{isAbnormal}");
                }
                if (queryStartTime != null & !Objects.equals(queryStartTime, "")) {
                    WHERE("face_predict.predict_time > #{queryStartTime}");
                }
                if (queryEndTime != null & !Objects.equals(queryEndTime, "")) {
                    WHERE("face_predict.predict_time < #{queryEndTime}");
                }
            }
        }.toString();
    }
}
