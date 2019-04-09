package com.xlauncher.fis.service.impl;

import com.xlauncher.fis.dao.BlocHotelDao;
import com.xlauncher.fis.entity.BlocHotel;
import com.xlauncher.fis.service.BlocHotelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :集团酒店对应关系实现类
 **/
@Service
public class BlocHotelServiceImpl implements BlocHotelService {
    @Autowired
    private BlocHotelDao blocHotelDao;
    private static Logger logger = Logger.getLogger(BlocHotelServiceImpl.class);


    /**
     * 同步集团酒店对应关系
     *
     * @param blocHotel blocHotel
     * @return int
     */
    @Override
    public int synBlocHotel(BlocHotel blocHotel) {
        logger.info("同步酒店!" + blocHotel);
        if (blocHotel != null) {
            if (ifExist(blocHotel.getHotelName())) {
                // 已存在
                return 0;
            } else {
                // 不存在
                return blocHotelDao.addBlocHotel(blocHotel);
            }
        }
        return 0;
    }

    /**
     * 同步集团酒店对应关系
     *
     * @param map map
     */
    @Override
    public void addBlocHotel(Map map) {
        logger.info("[同步运营云集团酒店信息] " + map);
        BlocHotel blocHotel = new BlocHotel();
        map.get("");



        blocHotelDao.addBlocHotel(blocHotel);
    }

    /**
     * 提供集团酒店对应关系
     *
     * @return map
     */
    @Override
    public Map<String, Object> queryBloc() {
        Map<String, Object> map = new HashMap<>(1);
        // 查询集团名称
        List<BlocHotel> listBlocName = blocHotelDao.queryBlocName();
        List<String> stringListBlocName = new ArrayList<>(1);
        if (listBlocName != null) {
            map.put("code", 200);
            listBlocName.forEach(blocHotel -> {
                stringListBlocName.add(blocHotel.getBlocName());
            });
            map.put("blocName", stringListBlocName);
        } else {
            map.put("code", 404);
            map.put("msg", "暂无集团信息!");
        }
        return map;
    }

    /**
     * 查询集团信息
     *
     * @param blocName
     * @return
     */
    @Override
    public Map<String, Object> queryHotel(String blocName) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("code", 200);
        // 查询酒店名称
        List<BlocHotel> listHotelName = blocHotelDao.queryHotelName(blocName);
        map.put("hotelName", listHotelName);
        return map;
    }

    /**
     * 验证是否已存在
     *
     * @param hotelName
     * @return
     */
    private boolean ifExist(String hotelName) {
        int count = blocHotelDao.countBlocHotel(hotelName);
        return count != 0;
    }
}
