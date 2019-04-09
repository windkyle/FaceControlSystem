package com.xlauncher.fis.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.Objects;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/11 0011
 * @Desc :集团酒店对应关系
 **/
public class BlocHotelMapper {

    /**
     * 查询酒店名称
     *
     * @param blocName
     * @return
     */
    public static String queryHotelName(@Param("blocName") String blocName) {
        return new SQL(){
            {
                SELECT("hotel_id, hotel_name");
                FROM("bloc_hotel");
                if (blocName != null & Objects.equals(blocName,"")) {
                    WHERE("bloc_name=#{blocName}");
                } else {
                    WHERE("bloc_name IS NULL");
                }

            }
        }.toString();
    }

    /**
     * 查询酒店编号
     *
     * @param blocName
     * @param hotelName
     * @return
     */
    public static String queryHotelIdByName(@Param("blocName") String blocName, @Param("hotelName") String hotelName) {
        return new SQL(){
            {
                SELECT("hotel_id");
                FROM("bloc_hotel");
                if (blocName != null & Objects.equals(blocName,"")) {
                    WHERE("bloc_name=#{blocName}");
                } else {
                    WHERE("bloc_name IS NULL");
                }
                if (hotelName != null & Objects.equals(hotelName, "")) {
                    WHERE("hotel_name=#{hotelName}");
                }
            }
        }.toString();
    }
}
