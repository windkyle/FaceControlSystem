package com.xlauncher.fis.util;

import com.xlauncher.fis.service.FacePredictService;
import com.xlauncher.fis.service.SynUserService;
import com.xlauncher.fis.util.model.Evaluator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :人脸识别分析模型计算线程
 **/
@Component
public class ThreadModelUtil {
    private static Logger logger = Logger.getLogger(ThreadModelUtil.class);
    @Autowired
    private SynUserService synUserService;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Autowired
    private FacePredictService facePredictService;
    /**
     * 人脸识别分析模型计算
     *
     * @param userCard 用户身份证号码
     * @param data 身份证图片
     * @throws InterruptedException
     * @throws IOException
     */
    @Async
    void doModelUtil(String userCard, byte[] data) throws InterruptedException, IOException {
        logger.info("[Async1-人脸识别分析模型计算doModelUtil()!]userCard." + userCard + ",data.length." + data.length);
        float[][] calculation = new float[0][];
        try {
            // 加载模型，计算识别
            calculation = StartListener.evaluator.evaluate(data, true);
        } catch (Exception e) {
            logger.error("Err.模型计算异常!" + e);
        }
        if (calculation != null) {
            int modelVersion = Integer.parseInt(propertiesUtil.getValue("model_version"));
            int result = synUserService.updateModel(FloatUtil.implode(calculation), modelVersion, userCard);
            logger.info("[更新人脸识别分析模型计算] result." + result);
        }
    }

    /**
     * 人脸抓拍图片识别存储
     *
     * @param map
     */
    @Async
    void doAddFacePredict(Map<String, String> map) {
        logger.info("[Async2-人脸抓拍图片识别存储doAddFacePredict()!]");
        facePredictService.addFacePredict(map);
    }
}
