package com.xlauncher.fis.dao.mapper;

import com.xlauncher.fis.entity.SynUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.Objects;


/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :运营云同步用户注册信息DAO层动态生成SQL
 **/
public class SynUserMapper {
    private static final String TABLE_NAME = "syn_user";

    /**
     * 运管云更新用户信息到本地数据库
     *
     * @param synUser synUser
     * @return int
     */
    public static String updateUser(SynUser synUser) {
        return new SQL() {
            {
                UPDATE(TABLE_NAME);
                if (synUser.getCheckinTime() != null ) {
                    SET("checkin_time=#{checkinTime}");
                }
                if (synUser.getCheckoutTime() != null ) {
                    SET("checkout_time=#{checkoutTime}");
                }
                if (synUser.getUserHotel() != null & !Objects.equals(synUser.getUserHotel(), "")) {
                    SET("user_hotel=#{userHotel}");
                }
                WHERE("user_card=#{userCard}");
            }
        }.toString();

    }

    /**
     * 查询没有离店时间、离店时间不小于当前时间的前一天
     *
     * @param hotelId 酒店编号
     * @return List
     */
    public static String queryUserListForPredict(@Param("hotelId") String hotelId){
        String sql;
        if (hotelId !=null & !Objects.equals(hotelId, "")) {
            sql = "SELECT * FROM (SELECT * FROM syn_user WHERE model_calculation IS NOT NULL AND user_hotel=#{hotelId}) " +
                    "AS result WHERE DATE_SUB(CURDATE(), INTERVAL 1 DAY) <= date(checkout_time) OR checkout_time IS NULL";
        } else {
            sql = "SELECT * FROM syn_user WHERE model_calculation IS NOT NULL AND DATE_SUB(CURDATE(), INTERVAL 1 DAY) <= date(checkout_time) OR checkout_time IS NULL";
        }
        return sql;
    }
}
