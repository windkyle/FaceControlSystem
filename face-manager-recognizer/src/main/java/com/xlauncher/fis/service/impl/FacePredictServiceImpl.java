package com.xlauncher.fis.service.impl;

import com.xlauncher.fis.dao.BlocHotelDao;
import com.xlauncher.fis.dao.FacePredictDao;
import com.xlauncher.fis.dao.SynUserDao;
import com.xlauncher.fis.entity.BlocHotel;
import com.xlauncher.fis.entity.FacePredict;
import com.xlauncher.fis.entity.SynUser;
import com.xlauncher.fis.service.BlocHotelService;
import com.xlauncher.fis.service.FacePredictService;
import com.xlauncher.fis.util.*;
import com.xlauncher.fis.util.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :人脸预测识别impl层
 **/
@Service
public class FacePredictServiceImpl implements FacePredictService{
    @Autowired
    private FacePredictDao facePredictDao;
    @Autowired
    private SynUserDao synUserDao;
    @Autowired
    private BlocHotelService blocHotelService;
    @Autowired
    private BlocHotelDao blocHotelDao;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Autowired
    private ModelUtl modelUtl;
    private static final int CARD_LENGTH = 18;
    private static String UNKNOWN = "unknown";
    private static Logger logger = Logger.getLogger(FacePredictServiceImpl.class);


    /**
     * 将人脸预测识别结果存入数据库
     *
     * @param map map
     * @return int
     */
    @Override
    public int addFacePredict(Map<String, String> map) {
        int result = 0;
        logger.info("[人脸预测识别并存储addFacePredict()]");
        // 酒店编号
        String hotelId = map.get("hotelId");
        // 酒店名称
        String hotelName = map.get("hotelName");
        // 酒店抓拍图片时间
        String imageTime = map.get("imageTime");

        // 酒店与集团对应关系（目前仅支持酒店）
        BlocHotel blocHotel = new BlocHotel();
        blocHotel.setBlocId("0");
        blocHotel.setBlocName("无");
        blocHotel.setHotelId(hotelId);
        blocHotel.setHotelName(hotelName);
        blocHotelService.synBlocHotel(blocHotel);

        // 酒店上报的抓拍人脸图片（用于人脸分析计算）
        byte[] bodyImage = ImageUtil.stringToByte(map.get("bodyImage"));
        // 酒店上报的抓拍资源图片（用于存储资源图片）
        byte[] sourceImage = ImageUtil.stringToByte(map.get("sourceImage"));
        logger.info("[抓拍人脸图片bodyImage.] " + bodyImage.length);
        logger.info("[抓拍资源图片sourceImage.] " + sourceImage.length);
        MongoUtil.saveImage(0, sourceImage, imageTime);
        MongoUtil.saveImage(1, bodyImage, imageTime);
        // 模型预设阀值
        double thresh = Double.parseDouble(propertiesUtil.getValue("model_thresh"));
        logger.info("[模型预设阀值thresh.] " + thresh);
        FacePredict facePredict = new FacePredict();
        // 模型计算
        String[] results = modelUtl.calculationResult(hotelName, bodyImage, thresh, facePredict);
        // 预测分析赋值
        facePredict.setPredictTime(imageTime);
        facePredict.setPredictHotel(hotelName);
        facePredict.setPredictImage(sourceImage);
        facePredict.setFacePhoto(bodyImage);
        logger.info("1____计算结束的时间!" + DateTimeUtil.getFormatTime(System.currentTimeMillis()));
        if (results.length != 0) {
            if (!Objects.equals(results[0], UNKNOWN)) {
                // 有识别结果
                SynUser synUser = synUserDao.getSynUser(results[0]);
                logger.info("[初次识别有结果results[0]] " + results[0]);
                facePredict.setUserCard(results[0]);
                facePredict.setUserAge(synUser.getUserAge());
                facePredict.setUserName(synUser.getUserName());
                facePredict.setUserSex(synUser.getUserSex());
                facePredict.setIsShelter(0);
                facePredict.setIsAbnormal(0);
                result = facePredictDao.addFacePredict(facePredict);
            } else {
                logger.info("初次识别没有结果!");
                long minutes = Long.parseLong(propertiesUtil.getValue("milliseconds"));
                logger.info("[预设时间 " + minutes + " 毫秒，一分钟60000毫秒]");
                ArrayListInstance arrayListInstance = ArrayListInstance.getInstance();
                logger.info("arrayListInstance.putT---" + map.size());
                arrayListInstance.intoList(true, map);
                logger.info("[线程等待结束执行计算模型!]");
            }
        }
        logger.info("[识别结果存储数据库facePredict] " + facePredict + ", result." + result);
        return result;
    }

    /**
     * 查询预测识别结果

     * @param blocName 集团名称
     * @param hotelName 酒店名称
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @param page 页码数
     * @return List
     */
    @Override
    public List<FacePredict> listFacePredict(String blocName, String hotelName, String hotelId, String queryStartTime, String queryEndTime, int isAbnormal, int page) {
        logger.info("[查询预测识别结果] blocName." + blocName + ", hotelName." + hotelName + ", hotelId." + hotelId
                + ", queryStartTime." + queryStartTime + ", queryEndTime." + queryEndTime + ", isAbnormal." + isAbnormal + ", page." + page);
        String cloneHotelId;
        if (!Objects.equals(hotelId, "") & hotelId != null) {
            cloneHotelId = hotelId;
        } else {
            cloneHotelId = blocHotelDao.queryHotelIdByName(blocName, hotelName);
        }

        List<FacePredict> list = facePredictDao.listFacePredict(cloneHotelId, queryStartTime, queryEndTime, isAbnormal, page);
        list.forEach(facePredict -> {
            int queryIsAbnormal = facePredict.getIsAbnormal();
            // 用户识别无异常返回用户入住信息、用户识别有异常返回null
            if (queryIsAbnormal == 0) {
                // 识别无异常
                String userCard = facePredict.getUserCard();
                // 身份证号码长度
                if (userCard.length() == CARD_LENGTH) {
                    String card1 = userCard.substring(0,10);
                    String card2 = userCard.substring(14, userCard.length());
                    String starCard = card1 + "****" + card2;
                    facePredict.setUserCard(starCard);
                }

                // 用户入住信息
                SynUser synUser = synUserDao.getSynUser(userCard);
                if (synUser != null) {
                    String checkinTime = synUser.getCheckinTime();
                    String checkoutTime = synUser.getCheckoutTime();
                    facePredict.setCheckinTime(checkinTime.substring(0, checkinTime.length()));
                    facePredict.setCheckoutTime(checkoutTime.substring(0, checkoutTime.length()));
                }
            }

            String predictTime = facePredict.getPredictTime();
            facePredict.setPredictTime(predictTime.substring(0, predictTime.length()));
        });
        return list;
    }

    /**
     * COUNT数

     * @param blocName 集团名称
     * @param hotelName 酒店名称
     * @param hotelId 酒店编号
     * @param queryStartTime 查询条件开始时间
     * @param queryEndTime 查询条件结束时间
     * @param isAbnormal 是否异常
     * @return List
     */
    @Override
    public int countListFacePredict(String blocName, String hotelName, String hotelId, String queryStartTime, String queryEndTime, int isAbnormal) {
        logger.info("查询COUNT数: blocName." + blocName + ",HotelName." + hotelName + ",HotelId." + hotelId + ",QueryStartTime." + queryStartTime + ",EndStartTime." + queryEndTime);
        int count;
        if (hotelId != null & !Objects.equals(hotelId, "")) {
            count = facePredictDao.countListFacePredict(hotelId, queryStartTime, queryEndTime, isAbnormal);
        } else {
            String queryHotelId = blocHotelDao.queryHotelIdByName(blocName, hotelName);
            count = facePredictDao.countListFacePredict(queryHotelId, queryStartTime, queryEndTime, isAbnormal);
        }
        return count;
    }

    /**
     * 查看具体预测识别结果
     *
     * @param id id
     * @return Map
     */
    @Override
    public Map<String, Object> getFacePredict(int id) {
        logger.info("[getFacePredict] 查看具体预测识别结果! id." + id);
        Map<String, Object> map = new HashMap<>(1);
        FacePredict facePredict = this.facePredictDao.getFacePredict(id);
        int isAbnormal = facePredict.getIsAbnormal();
        byte[] bytes = MongoUtil.findPredictImage(facePredict.getPredictTime());
        // 无异常
        if (isAbnormal == 0) {
            // 身份证信息进行****处理
            String userCard = facePredict.getUserCard();
            String card1 = userCard.substring(0,10);
            String card2 = userCard.substring(14, userCard.length());
            String starCard = card1 + "****" + card2;
            facePredict.setUserCard(starCard);
            // 查询用户入住信息
            SynUser synUser = synUserDao.getSynUser(userCard);
            facePredict.setCheckinTime(synUser.getCheckinTime());
            facePredict.setCheckoutTime(synUser.getCheckoutTime());
        }
        map.put("data", facePredict);
        map.put("bytes", bytes);
        return map;
    }

    /**
     * 根据ID查看预测识别图片
     *
     * @param id id
     * @return byte
     */
    @Override
    public byte[] getImgData(int id) {
        return this.facePredictDao.getFacePredict(id).getPredictImage();
    }
}
