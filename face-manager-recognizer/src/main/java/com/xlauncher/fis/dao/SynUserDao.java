package com.xlauncher.fis.dao;

import com.xlauncher.fis.dao.mapper.SynUserMapper;
import com.xlauncher.fis.entity.SynUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :运营云同步用户注册信息DAO层
 **/
@Service
public interface SynUserDao {

    /**
     * MQ自测
     *
     * @return
     */
    @Select("SELECT * FROM syn_user WHERE id >=#{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "userHotel", column = "user_hotel"),

    })
    List<SynUser> getListUser(@Param("id") int id);

    /**
     * 运管云同步用户信息到本地数据库
     *
     * @param synUser synUser
     * @return int
     */
    @Insert("INSERT INTO syn_user(user_name,user_age,user_sex,user_card,checkin_time,checkout_time,user_hotel,user_image)" +
            "VALUES(#{userName},#{userAge},#{userSex},#{userCard},#{checkinTime},#{checkoutTime},#{userHotel},#{userImage})")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "userHotel", column = "user_hotel"),
            @Result(property = "userImage", column = "user_image"),

    })
    int addSynUser(SynUser synUser);

    /**
     * 更新模型计算结果
     *
     * @param modelCalculation 模型计算结果
     * @param modelVersion 模型版本
     * @param userCard 用户身份证号码
     * @return int
     */
    @Update("UPDATE syn_user SET model_calculation=#{modelCalculation}, model_version=#{modelVersion} WHERE user_card=#{userCard}")
    int updateModel(@Param("modelCalculation") String modelCalculation
            , @Param("modelVersion") int modelVersion, @Param("userCard") String userCard);

    /**
     * 根据模型版本号获取用户信息
     *
     * @param modelVersion modelVersion
     * @return List
     */
    @Select("SELECT user_card FROM syn_user WHERE DATE_SUB(CURDATE(), INTERVAL 10 DAY) <= date(checkin_time)" +
            "AND model_version != #{modelVersion}")
    @Results({
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "modelVersion", column = "model_version"),
    })
    List<String> queryCardByVersion(@Param("modelVersion") int modelVersion);

    /**
     * 根据用户身份证号码查询用户信息
     *
     * @param userCard userCard
     * @return int
     */
    @Select("SELECT * FROM syn_user WHERE user_card = #{userCard}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "userHotel", column = "user_hotel"),
            @Result(property = "modelCalculation", column = "model_calculation"),
            @Result(property = "modelVersion", column = "model_version"),
    })
    SynUser getSynUser(@Param("userCard") String userCard);

    /**
     * 运管云更新用户信息到本地数据库
     *
     * @param synUser synUser
     * @return int
     */
    @UpdateProvider(type = SynUserMapper.class, method = "updateUser")
    int updateSynUser(SynUser synUser);


    /**
     * 验证是否存在该用户信息
     *
     * @param userCard userCard
     * @return int
     */
    @Select("SELECT COUNT(*) FROM syn_user WHERE user_card = #{userCard}")
    int checkUser(@Param("userCard") String userCard);

    /**
     * 查询指定酒店编号、入住天数的用户信息
     *
     * @param userHotel 酒店编号
     * @return List
     */
    @Select("SELECT user_name, user_age, user_sex, user_card, user_data, checkin_time, checkout_time, user_hotel, model_calculation, model_version" +
            " FROM syn_user WHERE DATE_SUB(CURDATE(), INTERVAL 10 DAY) <= date(checkin_time) AND user_hotel=#{userHotel}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "userHotel", column = "user_hotel"),
            @Result(property = "modelCalculation", column = "model_calculation"),
            @Result(property = "modelVersion", column = "model_version"),
    })
    List<SynUser> listSynUser(@Param("userHotel") String userHotel);


    /**
     * 查询没有离店时间、离店时间不小于当前时间的前一天
     *
     * @param hotelId 酒店编号
     * @return List
     */
    @SelectProvider(type = SynUserMapper.class, method = "queryUserListForPredict")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "userAge", column = "user_age"),
            @Result(property = "userSex", column = "user_sex"),
            @Result(property = "userCard", column = "user_card"),
            @Result(property = "checkinTime", column = "checkin_time"),
            @Result(property = "checkoutTime", column = "checkout_time"),
            @Result(property = "userHotel", column = "user_hotel"),
            @Result(property = "modelCalculation", column = "model_calculation"),
            @Result(property = "modelVersion", column = "model_version"),
    })
    List<SynUser> queryUserListForPredict(@Param("hotelId") String hotelId);
}
