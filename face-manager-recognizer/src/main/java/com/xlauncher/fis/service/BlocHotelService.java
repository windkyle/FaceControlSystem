package com.xlauncher.fis.service;

import com.xlauncher.fis.entity.BlocHotel;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :集团酒店对应关系Service
 **/
@Service
public interface BlocHotelService {

    /**
     * 同步集团酒店对应关系
     *
     * @param blocHotel blocHotel
     * @return int
     */
    int synBlocHotel(BlocHotel blocHotel);

    /**
     * 同步集团酒店对应关系
     *
     * @param map map
     * @return int
     */
    void addBlocHotel(Map map);

    /**
     * 查询集团信息
     *
     * @return
     */
    Map<String, Object> queryBloc();

    /**
     * 查询集团信息
     *
     * @param blocName
     * @return
     */
    Map<String, Object> queryHotel(String blocName);
}
