package com.xlauncher.fis.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/21 0021
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class FacePredictServiceTest {
    @Autowired
    FacePredictService facePredictService;
    @Test
    public void addFacePredict() throws Exception {
    }

    @Test
    public void listFacePredict() throws Exception {
        System.out.println("listFacePredict()." + facePredictService.listFacePredict("","","","","", 0,1));
    }

    @Test
    public void countListFacePredict() throws Exception {
    }

    @Test
    public void getFacePredict() throws Exception {
        System.out.println(facePredictService.getFacePredict(9));
    }

    @Test
    public void getPicData() throws Exception {
    }

    @Test
    public void getImgData() throws Exception {
    }

}