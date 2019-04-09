package com.xlauncher.fis.util;

import com.xlauncher.fis.dao.FacePredictDao;
import com.xlauncher.fis.dao.SynUserDao;
import com.xlauncher.fis.entity.FacePredict;
import com.xlauncher.fis.entity.SynUser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/20 0004
 * @Desc :模型计算工具：计算识别图片特征值、比较两个特征向量的距离、预测阀值
 **/
@Component
public class ModelUtl {
    private static Logger logger = Logger.getLogger(ModelUtl.class);
    private static String UNKNOWN = "unknown";
    @Autowired
    SynUserDao synUserDao;
    @Autowired
    FacePredictDao facePredictDao;
    /**
     * 计算两个特征向量的距离
     *
     * @param embedding0
     * @param embedding1
     * @return
     */
    private static float distance(float[] embedding0, float[] embedding1){
        float dist = 0;
        for(int i=0; i<embedding1.length; i++){
            float diff = embedding1[i]-embedding0[i];
            dist += Math.pow(diff, 2);
        }
        return dist;
    }

    /**
     * 预测不同图片特征值是否小于预设阀值
     *
     * @param embeddings0 数据库图片特征值
     * @param cardList 身份证数组
     * @param embeddings1 待预测图片特征值
     * @param thresh 预设阀值
     * @return string[]
     */
    public static String[] classify(float[][] embeddings0, String[] cardList, float[][] embeddings1, double thresh){

        double[][] dist = new double[embeddings1.length][embeddings0.length];
        double[] distMin = new double[embeddings1.length];
        int[] minIndex = new int[embeddings1.length];

        for(int i=0; i<embeddings1.length; i++){
            distMin[i] = 1000000000;
            for(int j=0; j<embeddings0.length; j++){
                dist[i][j] = distance(embeddings1[i], embeddings0[j]);
                if(distMin[i]>dist[i][j]){
                    distMin[i] = dist[i][j];
                    minIndex[i] = j;
                }
            }
        }

        logger.info("[每张人脸与数据库中人脸的最小距离]" + Arrays.toString(distMin));
        String[] faceClassify = new String[embeddings1.length];

        for(int i=0; i<embeddings1.length; i++){
            BigDecimal distminBig = new BigDecimal(distMin[i]);
            BigDecimal threshBig = new BigDecimal(thresh);
            if(distminBig.compareTo(threshBig)<0){
                faceClassify[i] = cardList[minIndex[i]];
            } else {
                faceClassify[i] = UNKNOWN;
            }
        }

        return faceClassify;
    }


    /**
     * 模型计算识别分析结果
     *
     * @param hotelName
     * @param bytes
     * @param thresh
     * @return
     */
    public String[] calculationResult(String hotelName, byte[] bytes, double thresh, FacePredict facePredict) {
        logger.info("[模型计算calculationResult] hotelName:" +hotelName + ",thresh:" + thresh + ",facePredict:" + facePredict);
        String[] results = new String[0];
        // 数据库符合条件的用户信息
        List<SynUser> synUserList = synUserDao.queryUserListForPredict(hotelName);
        logger.info("[synUserList.size()] " + synUserList.size());
        // 数据库已注册用户身份证号码结果集
        String[] cards = new String[synUserList.size()];
        // 数据库已注册用户身份证图片特征结果集
        float[][] calculations = new float[synUserList.size()][];

        for (int i=0; i<synUserList.size();i++) {
            // 用户身份证号码
            cards[i] = synUserList.get(i).getUserCard();
            // 用户身份证图片特征向量
            calculations[i] = FloatUtil.explode(synUserList.get(i).getModelCalculation())[0];
        }
        logger.info("[身份证结果集cards] " + Arrays.toString(cards));
        logger.info("[特征向量集calculations.length] " + calculations.length);
        // 模型计算、结果特征值
        try {
            float[][] embeddings0 = StartListener.evaluator.evaluate(bytes, false);
            if (embeddings0 != null) {
                facePredict.setModelCalculation(FloatUtil.implode(embeddings0));
                // 结果对比（抓拍图片特征值与数据库用户特征值比较，返回其距离最小的且小于阀值）
                results = classify(calculations, cards, embeddings0, thresh);
                logger.info("[模型计算识别分析结果results.] " + Arrays.toString(results));
            }
        } catch (Exception e) {
            logger.error("[Err.模型异常]" + e.getCause() + Arrays.toString(e.getStackTrace()));
        }
        return results;
    }
}
