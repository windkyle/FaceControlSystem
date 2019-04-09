package com.xlauncher.fis.service.impl;

import com.xlauncher.fis.dao.SynUserDao;
import com.xlauncher.fis.entity.BlocHotel;
import com.xlauncher.fis.entity.SynUser;
import com.xlauncher.fis.service.BlocHotelService;
import com.xlauncher.fis.service.SynUserService;
import com.xlauncher.fis.util.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :运营云同步用户服务实现层
 **/
@Service
public class SynUserServiceImpl implements SynUserService {
    @Autowired
    private SynUserDao synUserDao;
    @Autowired
    private TemplateUtil templateUtil;
    @Autowired
    private BlocHotelService blocHotelService;

    private static Logger logger = Logger.getLogger(SynUserServiceImpl.class);
    private static final String COMMA = ",";

    /**
     * 运管云同步用户信息到本地数据库（如果已经存在则更新，不存在则同步）
     *
     * @param map map
     */
    @Override
    public void addSynUser(Map map) {
        logger.info("[运管云同步用户信息到本地数据库addSynUser()] ");
        SynUser synUser = new SynUser();
        List<Map<String, Object>> mapList = (List<Map<String, Object>>) map.get("data");
        if (mapList != null) {
            logger.info("[运管云同步用户信息mapList.size()] " + mapList.size());
            // 获得同步用户map
            for (int n=0; n<mapList.size(); n++) {
                // 用户入住时间
                String checkinTime = String.valueOf(mapList.get(n).get("CHECKINTIME"));
                // 用户离店时间（未退房为空）
                String checkoutTime = String.valueOf(mapList.get(n).get("CHECKOUTTIME"));
                // 初始化32位酒店编号
                String id = Init.initialise();
                // 用户入住酒店名称
                String hotelName = String.valueOf(mapList.get(n).get("HOTELNAME"));
                // 用户姓名
                String name = String.valueOf(mapList.get(n).get("NAME"));
                // 用户身份证号码
                String idCard = String.valueOf(mapList.get(n).get("IDCREAD"));

                BlocHotel blocHotel = new BlocHotel();
                blocHotel.setBlocId("0");
                blocHotel.setBlocName("无");
                blocHotel.setHotelId(id);
                blocHotel.setHotelName(hotelName);
                // 同步酒店名称
                blocHotelService.synBlocHotel(blocHotel);
                // 验证是否多个用户、身份证
                if (idCard.contains(COMMA)) {
                    String[] idCards = idCard.split(COMMA);
                    String[] names = name.split(COMMA);
                    // 遍历每一个用户、身份证
                    for (int i=0; i<idCards.length;i++) {
                        synUser.setUserName(names[i]);
                        synUser.setUserCard(idCards[i]);
                        synUser.setCheckinTime(checkinTime);
                        synUser.setCheckoutTime(checkoutTime);
                        // 备注：hotelId
                        synUser.setUserHotel(hotelName);
                        synUser.setUserSex(UserCardUtil.getUserSex(idCards[i]));
                        synUser.setUserAge(UserCardUtil.getUserAge(idCards[i]));
                        // 验证该身份证号码是否已经同步
                        boolean ifExist = checkUserIfExist(idCards[i]);
                        // 更新用户身份证号码获取用户身份证图片
                        byte[] bytes = templateUtil.queryPersonByIdCard(idCards[i]);
                        synUser.setUserImage(bytes);
                        logger.info("[遍历获取用户身份证图片]" + idCards[i] + ", num " + n);
                        if (ifExist) {
                            // 存在该用户
                            synUserDao.updateSynUser(synUser);
                        } else {
                            // 不存在该用户
                            synUserDao.addSynUser(synUser);
                        }
                    }
                } else {
                    synUser.setUserName(name);
                    synUser.setUserCard(idCard);
                    synUser.setCheckinTime(checkinTime);
                    synUser.setCheckoutTime(checkoutTime);
                    // 备注：hotelId
                    synUser.setUserHotel(hotelName);
                    synUser.setUserSex(UserCardUtil.getUserSex(idCard));
                    synUser.setUserAge(UserCardUtil.getUserAge(idCard));
                    // 验证该身份证号码是否已经同步
                    boolean ifExist = checkUserIfExist(idCard);
                    // 更新用户身份证号码获取用户身份证图片
                    byte[] bytes = templateUtil.queryPersonByIdCard(idCard);
                    synUser.setUserImage(bytes);
                    logger.info("[遍历获取用户身份证图片]" + idCard + ", num " + n);
                    if (ifExist) {
                        // 存在该用户
                        synUserDao.updateSynUser(synUser);
                    } else {
                        // 不存在该用户
                        synUserDao.addSynUser(synUser);
                    }
                }
            }
        }
    }

    /**
     * 运管云同步用户信息到本地数据库(方式二：增量：MQ+接口)
     *
     * @param map map
     */
    @Override
    public void addSynUserByMQ(Map map) {
        logger.info("[运管云同步用户信息(方式二：增量：MQ+接口)] " + map);
        SynUser synUser = new SynUser();
        // 用户姓名
        String name = String.valueOf(map.get("name"));
        // 用户身份证号码
        String idCard = String.valueOf(map.get("idCard"));
        // 用户入住时间
        String checkinTime = String.valueOf(map.get("checkinTime"));
        // 用户离店时间
        String checkoutTime = String.valueOf(map.get("checkoutTime"));
        // 用户入住酒店编号（为空）
        String hotelId = String.valueOf(map.get("hotelId"));
        // 用户入住酒店名称
        String hotelName = String.valueOf(map.get("hotelName"));

        BlocHotel blocHotel = new BlocHotel();
        blocHotel.setBlocId("0");
        blocHotel.setBlocName("无");
        // 初始化32位酒店编号
        String id;
        if (hotelId != null & !Objects.equals(hotelId, "")) {
            id = hotelId;
        } else {
            id = Init.initialise();
        }
        blocHotel.setHotelId(id);
        blocHotel.setHotelName(hotelName);
        // 同步酒店名称
        blocHotelService.synBlocHotel(blocHotel);

        // 验证是否多个用户、身份证
        if (idCard.contains(COMMA)) {
            String[] idCards = idCard.split(COMMA);
            String[] names = name.split(COMMA);
            for (int j=0;j<idCards.length;j++) {
                synUser.setUserCard(idCards[j]);
                synUser.setUserName(names[j]);
                synUser.setCheckinTime(checkinTime);
                synUser.setCheckoutTime(checkoutTime);
                // 备注：hotelId
                synUser.setUserHotel(hotelName);
                synUser.setUserSex(UserCardUtil.getUserSex(idCards[j]));
                synUser.setUserAge(UserCardUtil.getUserAge(idCards[j]));
                // 验证该身份证号码是否已经同步
                boolean ifExist = checkUserIfExist(idCards[j]);
                try {
                    Thread.sleep(3000);
                    logger.info("等待3000毫秒获取图片!");
                } catch (InterruptedException e) {
                    logger.error("Err.等待3000毫秒获取身份证图片异常!" + e);
                }
                byte[] bytes = templateUtil.queryPersonByIdCard(idCards[j]);
                synUser.setUserImage(bytes);
                if (ifExist) {
                    // 存在
                    synUserDao.updateSynUser(synUser);
                } else {
                    // 不存在
                    synUserDao.addSynUser(synUser);
                }
            }
        } else {
            synUser.setUserCard(idCard);
            synUser.setUserName(name);
            synUser.setCheckinTime(checkinTime);
            synUser.setCheckoutTime(checkoutTime);
            // 备注：hotelId
            synUser.setUserHotel(hotelName);
            synUser.setUserSex(UserCardUtil.getUserSex(idCard));
            synUser.setUserAge(UserCardUtil.getUserAge(idCard));
            // 验证该身份证号码是否已经同步
            boolean ifExist = checkUserIfExist(idCard);
            try {
                logger.info("等待3000毫秒获取图片!");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.error("Err.等待3000毫秒获取身份证图片异常!" + e);
            }
            byte[] bytes = templateUtil.queryPersonByIdCard(idCard);
            synUser.setUserImage(bytes);
            if (ifExist) {
                // 存在
                synUserDao.updateSynUser(synUser);
            } else {
                // 不存在
                synUserDao.addSynUser(synUser);
            }
        }
    }

    /**
     * 更新模型计算结果
     *
     * @param modelCalculation 模型计算结果
     * @param modelVersion     模型版本
     * @param userCard         用户身份证号码
     * @return int
     */
    @Override
    public int updateModel(String modelCalculation, int modelVersion, String userCard) {
        logger.info("[更新模型计算结果] userCard." + userCard);
        int result = synUserDao.updateModel(modelCalculation, modelVersion, userCard);
        // 模型版本号对比
        List<String> listUserCard = synUserDao.queryCardByVersion(modelVersion);
        if (listUserCard != null) {
            listUserCard.forEach(s -> {
                // 更新用户身份证号码获取用户身份证图片
                templateUtil.queryPersonByIdCard(s);
            });
        }

        return result;
    }

    /**
     * 查询指定酒店编号、入住天数的用户信息
     *
     * @param userHotel 酒店编号
     * @return List
     */
    @Override
    public List<SynUser> listSynUser(String userHotel) {
        return synUserDao.listSynUser(userHotel);
    }

    /**
     * 验证用户信息是否已经存在（存在返回true，不存在返回false）
     *
     * @param userCard 用户身份证信息
     * @return boolean
     */
    private boolean checkUserIfExist(String userCard) {
        int count = synUserDao.checkUser(userCard);
        return count != 0;
    }
}
