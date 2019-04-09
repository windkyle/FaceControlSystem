package com.xlauncher.fis.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class BlocHotelDaoTest {
    @Autowired
    BlocHotelDao blocHotelDao;
    @Test
    public void addBlocHotel() throws Exception {
    }

    @Test
    public void queryBlocName() throws Exception {
    }

    @Test
    public void queryHotelName() throws Exception {
        System.out.println("queryHotelName:" + blocHotelDao.queryBlocName());
    }

    @Test
    public void queryHotelIdByName() throws Exception {
        System.out.println("queryHotelIdByName:" + blocHotelDao.queryHotelIdByName("", ""));
    }

}