package com.xlauncher.fis.dao;

import com.xlauncher.fis.entity.FacePredict;
import com.xlauncher.fis.util.DateTimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/20 0020
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class FacePredictDaoTest {
    @Autowired
    FacePredictDao facePredictDao;
    @Test
    public void addFacePredict() throws Exception {
        FacePredict facePredict = new FacePredict();
        facePredict.setPredictTime(DateTimeUtil.getFormatTime(System.currentTimeMillis()));
        facePredict.setIsAbnormal(1);
        facePredict.setIsShelter(0);
        facePredict.setUserSex(0);
        facePredict.setUserAge(21);
        facePredict.setUserCard("410000000000000001");
        facePredict.setUserName("test" + new Random().nextInt());
        File file = new File("D:\\sdkimage\\image1.jpg");
        BufferedImage bufferedImage = ImageIO.read(file);
        ByteArrayOutputStream buf = new ByteArrayOutputStream((int) file.length());
        ImageIO.write(bufferedImage, "jpg", buf);
        facePredict.setPredictImage(buf.toByteArray());

        facePredictDao.addFacePredict(facePredict);
    }

    @Test
    public void listFacePredict() throws Exception {
        List<FacePredict> list = facePredictDao.listImageFacePredict();
        list.forEach(facePredict -> {
            byte[] image = facePredict.getPredictImage();
            // byte数组保存图片
            String fileName = "D:\\images\\" + System.currentTimeMillis() +".jpg";
            try {
                FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileName));
                imageOutput.write(image, 0, image.length);
                imageOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    @Test
    public void countListFacePredict() throws Exception {
    }

    @Test
    public void getFacePredict() throws Exception {
        FacePredict facePredict = facePredictDao.getFacePredict(9);
        System.out.println(facePredict);
        if (facePredict.getIsAbnormal() != 1) {
            String userCard = facePredict.getUserCard();
            String card1 = userCard.substring(0,10);
            String card2 = userCard.substring(14, userCard.length());
            String starCard = card1 + "****" + card2;
            facePredict.setUserCard(starCard);
            System.out.println("starCard===" + starCard);
            System.out.println(facePredict);
        } else {
            System.out.println(facePredict);
        }

    }

}