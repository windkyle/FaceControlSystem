package com.xlauncher.fis.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/4 0004
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThreadSynUtilTest {
    @Autowired
    ThreadSynUtil threadSynUtil;
    @Test
    public void timeTask() throws Exception {
        threadSynUtil.timeTask();
    }

    @Test
    public void run() throws Exception {
    }

}