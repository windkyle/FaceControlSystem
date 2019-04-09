package com.xlauncher.fis.service;

import com.xlauncher.fis.entity.SynUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SynUserServiceTest {
    @Autowired
    SynUserService synUserService;
    private SynUser synUser;

    @Before
    public void init() {
        synUser = new SynUser();
        synUser.setUserName("test:test");
        synUser.setUserSex(1);
        synUser.setUserAge(20);
        synUser.setUserCard("410295166987451255");
        synUser.setUserHotel("b62f299cc5ae4b889edaddf615d55879");
    }
    @Test
    public void addSynUser() throws Exception {
        Map map = new HashMap();
        map.put("synUser", synUser);
        synUserService.addSynUser(map);
    }

}