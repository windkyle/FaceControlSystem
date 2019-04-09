package com.xlauncher.fis.dao;

import com.xlauncher.fis.entity.SynUser;
import com.xlauncher.fis.util.TemplateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/21 0021
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest

public class SynUserDaoTest {
    @Autowired
    SynUserDao synUserDao;
    @Autowired
    TemplateUtil templateUtil;
    @Test
    public void addSynUser() throws Exception {
        SynUser synUser = new SynUser();
        synUser.setUserName("Test:" + new Random().nextInt());
        synUser.setUserAge(21);
        synUser.setUserSex(1);
        synUser.setUserCard("410123456789123652");
        synUser.setUserHotel("b62f299cc5ae4b889edaddf615d55879");

        File file = new File("D:\\sdkimage\\image1.jpg");
        BufferedImage bufferedImage = ImageIO.read(file);
        ByteArrayOutputStream buf = new ByteArrayOutputStream((int) file.length());
        ImageIO.write(bufferedImage, "jpg", buf);
        buf.toByteArray();
        synUserDao.addSynUser(synUser);
    }
    @Test
    public void checkUser() {
        System.out.println(synUserDao.checkUser("410123456789123652"));
    }
    @Test
    public void updateUser() {

    }
    @Test
    public void updateModel() {
        synUserDao.updateModel("1024",1, "411123456789123652");
    }

    @Test
    public void listSynUser() throws Exception {
        List<SynUser> list = synUserDao.getListUser(274);
        list.forEach(synUser -> {
            System.out.println("---------->");
            templateUtil.getImageByIdCard(synUser.getUserCard());
        });
    }

    @Test
    public void queryUserListForPredict() {
        System.out.println("+-+" + synUserDao.queryUserListForPredict(""));
    }
}