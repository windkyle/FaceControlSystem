package com.xlauncher.fis.dao;

import com.xlauncher.fis.util.MD5Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/11 0011
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {
    @Autowired
    UserDao userDao;
    @Test
    public void initUser() throws Exception {
    }

    @Test
    public void getUserByAccount() throws Exception {
    }

    @Test
    public void updatePassword() throws Exception {
        userDao.updatePassword(MD5Util.getResult("123456"),"admin");
    }

    @Test
    public void deleteToken() throws Exception {
    }

}