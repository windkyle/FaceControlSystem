package com.xlauncher.fis.dao;

import com.xlauncher.fis.dao.mapper.BlocHotelMapper;
import com.xlauncher.fis.entity.BlocHotel;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :集团酒店对应关系DAO
 **/
@Service
public interface BlocHotelDao {

    /**
     * 同步集团酒店对应关系
     *
     * @param blocHotel blocHotel
     * @return int
     */
    @Insert("INSERT INTO bloc_hotel(bloc_id, bloc_name, hotel_id, hotel_name)" +
            "VALUES(#{blocId},#{blocName},#{hotelId},#{hotelName})")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "blocId", column = "bloc_id"),
            @Result(property = "blocName", column = "bloc_name"),
            @Result(property = "hotelId", column = "hotel_id"),
            @Result(property = "hotelName", column = "hotel_name"),

    })
    int addBlocHotel(BlocHotel blocHotel);

    /**
     * 是否已存在
     *
     * @param hotelName hotelName
     * @return int
     */
    @Select("SELECT COUNT(*) FROM bloc_hotel WHERE hotel_name=#{hotelName}")
    int countBlocHotel(@Param("hotelName") String hotelName);

    /**
     * 查询集团名称
     *
     * @return
     */
    @Select("SELECT DISTINCT bloc_id, bloc_name FROM bloc_hotel")
    @Results({
            @Result(property = "blocId", column = "bloc_id"),
            @Result(property = "blocName", column = "bloc_name"),
    })
    List<BlocHotel> queryBlocName();

    /**
     * 查询酒店名称
     *
     * @param blocName
     * @return
     */
    @Select("SELECT hotel_id, hotel_name FROM bloc_hotel WHERE bloc_name=#{blocName}")
//    @SelectProvider(type = BlocHotelMapper.class, method = "queryHotelName")
    @Results({
            @Result(property = "hotelId", column = "hotel_id"),
            @Result(property = "hotelName", column = "hotel_name"),
    })
    List<BlocHotel> queryHotelName(@Param("blocName") String blocName);

    /**
     * 查询酒店编号（备注：hotel_id）
     *
     * @param blocName
     * @param hotelName
     * @return
     */
    @Select("SELECT hotel_name FROM bloc_hotel WHERE bloc_name=#{blocName} AND hotel_name=#{hotelName}")
//    @SelectProvider(type = BlocHotelMapper.class, method = "queryHotelIdByName")
    @Results({
            @Result(property = "hotelName", column = "hotel_name"),
    })
    String queryHotelIdByName(@Param("blocName") String blocName, @Param("hotelName") String hotelName);
}
