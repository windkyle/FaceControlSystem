package com.xlauncher.fis.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xlauncher.fis.dao.FacePredictDao;
import com.xlauncher.fis.dao.SynUserDao;
import com.xlauncher.fis.entity.FacePredict;
import com.xlauncher.fis.entity.SynUser;
import org.apache.log4j.Logger;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/21 0004
 * @Desc :初次识别没有结果，将map放入列表中，根据预设时间进行再次识别
 **/
public class ArrayListInstance {
    private static ArrayListInstance arrayListInstance = null;
    private ArrayList<Map<String, String>> mapArrayList = new ArrayList<>();
    private static Logger logger = Logger.getLogger(ThreadArrayList.class);

    private PropertiesUtil propertiesUtil = SpringUtil.getBean(PropertiesUtil.class);
    private FacePredictDao facePredictDao = SpringUtil.getBean(FacePredictDao.class);
    private ModelUtl modelUtl = SpringUtil.getBean(ModelUtl.class);
    private SynUserDao synUserDao = SpringUtil.getBean(SynUserDao.class);
    private static String UNKNOWN = "unknown";

    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("ArrayList-pool-%d").build();
    private ExecutorService poolExecutor = new ThreadPoolExecutor(4, 4,
            60000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(20), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    private ArrayListInstance() {}

    /**
     * 单例实现
     *
     * @return
     */
    public synchronized static ArrayListInstance getInstance() {
        if (arrayListInstance == null) {
            logger.info("[arrayListInstance = new ArrayListInstance()]");
            arrayListInstance = new ArrayListInstance();
        }
        return arrayListInstance;
    }

    /**
     * 由boolean决定是否put还是deal
     *
     * @param isPut
     * @param map
     */
    public synchronized void intoList(boolean isPut, Map<String, String> map) {
        if (isPut) {
            putT(map);
        } else {
            dealT();
        }
    }


    /**
     * 将map放入list中
     *
     * @param map
     */
    private void putT(Map<String, String> map) {
        if (map != null) {
            mapArrayList.add(map);
            logger.info("往队列添加内容, mapArrayList size:" + mapArrayList.size());
        }
    }


    /**
     * 定时重新计算
     */
    private void dealT() {
        long period = Long.parseLong(propertiesUtil.getValue("milliseconds"));
        double thresh = Double.parseDouble(propertiesUtil.getValue("model_thresh"));
        int size = mapArrayList.size();
        if (size != 0) {
            logger.info("mapArrayList size." +size);
            logger.info("-----start-----");
            for (int i= size - 1; i>=0; i--) {
                logger.info("遍历List。i:" + i);
                Map<String, String> map = mapArrayList.get(i);
                FacePredict facePredict = new FacePredict();
                // GET
                String compareTime = map.get("compareTime");
                try {
                    long compare = DateTimeUtil.dateToStamp(compareTime);
                    logger.info("compare." + compare);
                    long currentTime = System.currentTimeMillis();
                    logger.info("currentTime." + compareTime);
                    long resultTime = currentTime - compare;
                    logger.info("resultTime." + resultTime);
                    if (resultTime >= period) {
                        logger.info("resultTime >= period 执行计算存储!");
                        // 酒店名称
                        String hotelName = map.get("hotelName");
                        // 酒店抓拍图片时间
                        String imageTime = map.get("imageTime");
                        // 酒店上报的抓拍人脸图片（用于人脸分析计算）
                        byte[] bodyImage = ImageUtil.stringToByte(map.get("bodyImage"));
                        // 酒店上报的抓拍资源图片（用于存储资源图片）
                        byte[] sourceImage = ImageUtil.stringToByte(map.get("sourceImage"));
                        // 预测分析赋值
                        facePredict.setPredictTime(imageTime);
                        facePredict.setPredictHotel(hotelName);
                        facePredict.setPredictImage(sourceImage);
                        facePredict.setFacePhoto(bodyImage);
                        // 线程池
                        poolExecutor.execute(() -> {
                            logger.info("PoolExecutor Execute()");
                            int addResult;
                            String[] resultsAgain = modelUtl.calculationResult(hotelName, bodyImage, thresh, facePredict);
                            if (!Objects.equals(resultsAgain[0], UNKNOWN)) {
                                // 有识别结果
                                SynUser synUser = synUserDao.getSynUser(resultsAgain[0]);
                                logger.info("[再次识别有结果resultsAgain!] " + resultsAgain[0]);
                                facePredict.setUserCard(resultsAgain[0]);
                                facePredict.setUserAge(synUser.getUserAge());
                                facePredict.setUserName(synUser.getUserName());
                                facePredict.setUserSex(synUser.getUserSex());
                                facePredict.setIsShelter(0);
                                facePredict.setIsAbnormal(0);
                                addResult = facePredictDao.addFacePredict(facePredict);
                            } else {
                                // 没有识别结果
                                logger.info("[再次识别没有结果!]");
                                facePredict.setIsShelter(0);
                                facePredict.setIsAbnormal(1);
                                addResult = facePredictDao.addFacePredict(facePredict);
                            }
                            logger.info("线程池PoolExecutor." + Thread.currentThread().getId() + ",存储结果addResult." + addResult);
                            logger.info("[迭代器中结果存储数据库facePredict] " + facePredict);
                        });
                        logger.info("Execute End");
                        // REMOVE
                        mapArrayList.remove(mapArrayList.get(i));
                        logger.info("移除已经计算存储的iterator!");
                    } else {
                        logger.info("resultTime < period 继续等待下次执行!");
                    }
                } catch (ParseException e) {
                    logger.error("Err.迭代器异常！" + e);
                }
            }
            logger.info("-----end-----");
        } else {
            logger.info("MapArrayList Size Is 0!");
        }
    }

//    /**
//     * 定时重新计算
//     */
//    private void dealT() {
//
//        Iterator<Map<String, String>> iterator = mapArrayList.iterator();
//        long period = Long.parseLong(propertiesUtil.getValue("milliseconds"));
//        logger.info("定时重新计算___period." + period);
//        double thresh = Double.parseDouble(propertiesUtil.getValue("model_thresh"));
//        int count = 0;
//        while (iterator.hasNext()) {
//            logger.info("size," + mapArrayList.size() + ",count. " + count++);
//            Map<String, String> map = iterator.next();
//            FacePredict facePredict = new FacePredict();
//            // GET
//            String compareTime = map.get("compareTime");
//            try {
//                long compare = DateTimeUtil.dateToStamp(compareTime);
//                logger.info("compare." + compare);
//                long currentTime = System.currentTimeMillis();
//                logger.info("currentTime." + compareTime);
//                long resultTime = currentTime - compare;
//                logger.info("resultTime." + resultTime);
//                if (resultTime >= period) {
//                    logger.info("resultTime >= period 执行计算存储!");
//                    // 酒店名称
//                    String hotelName = map.get("hotelName");
//                    // 酒店抓拍图片时间
//                    String imageTime = map.get("imageTime");
//                    // 酒店上报的抓拍人脸图片（用于人脸分析计算）
//                    byte[] bodyImage = ImageUtil.stringToByte(map.get("bodyImage"));
//                    // 酒店上报的抓拍资源图片（用于存储资源图片）
//                    byte[] sourceImage = ImageUtil.stringToByte(map.get("sourceImage"));
//                    // 预测分析赋值
//                    facePredict.setPredictTime(imageTime);
//                    facePredict.setPredictHotel(hotelName);
//                    facePredict.setPredictImage(sourceImage);
//                    facePredict.setFacePhoto(bodyImage);
//
//                    poolExecutor.execute(()->{
//                        logger.info("PoolExecutor Execute()");
//                        int addResult;
//                        String[] resultsAgain = modelUtl.calculationResult(hotelName, bodyImage, thresh, facePredict);
//                        if (!Objects.equals(resultsAgain[0], UNKNOWN)) {
//                            // 有识别结果
//                            SynUser synUser = synUserDao.getSynUser(resultsAgain[0]);
//                            logger.info("[再次识别有结果resultsAgain!] " + resultsAgain[0]);
//                            facePredict.setUserCard(resultsAgain[0]);
//                            facePredict.setUserAge(synUser.getUserAge());
//                            facePredict.setUserName(synUser.getUserName());
//                            facePredict.setUserSex(synUser.getUserSex());
//                            facePredict.setIsShelter(0);
//                            facePredict.setIsAbnormal(0);
//                            addResult = facePredictDao.addFacePredict(facePredict);
//                        } else {
//                            // 没有识别结果
//                            logger.info("[再次识别没有结果!]");
//                            facePredict.setIsShelter(0);
//                            facePredict.setIsAbnormal(1);
//                            addResult = facePredictDao.addFacePredict(facePredict);
//                        }
//                        logger.info("PoolExecutor." + Thread.currentThread().getId() + ", addResult." + addResult);
//                    });
//                    logger.info("Execute End");
//                    // REMOVE
//                    iterator.remove();
//                    logger.info("移除已经计算存储的iterator!");
//                    logger.info("[迭代器中结果存储数据库facePredict] " + facePredict);
//                } else {
//                    logger.info("resultTime < period 继续等待下次执行!");
//                }
//            } catch (ParseException e) {
//                e.printStackTrace();
//                logger.error("Err.迭代器异常！" + e);
//            }
//        }
//    }

//    public synchronized Map<String, String> getT() {
//        Map<String, String> map = null;
//        Iterator<Map<String, String>> iterator = mapArrayList.iterator();
//        if (iterator.hasNext()) {
//            map = iterator.next();
//        }
//        return map;
//    }

//    public synchronized void removeT(Map<String, String> map) {
//        Iterator<Map<String, String>> iterator = mapArrayList.iterator();
//        while (iterator.hasNext()) {
//            Map<String, String> mapTmp = iterator.next();
//            if (mapTmp.equals(map)) {
//                iterator.remove();
//                return;
//            }
//        }
//    }
}
