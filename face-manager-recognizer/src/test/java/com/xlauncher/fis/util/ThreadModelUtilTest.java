package com.xlauncher.fis.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ThreadModelUtilTest {
    @Autowired
    ThreadModelUtil threadModelUtil;
    @Test
    public void doModelUtil() throws Exception {

        InputStream is = new FileInputStream("D:\\sdkimage\\bue.jpg");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 12];
        int n;
        while ((n = is.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        System.out.println("*.*.*.*.*.*.*.*");
        threadModelUtil.doModelUtil("350128199610220020", out.toByteArray());

    }

}