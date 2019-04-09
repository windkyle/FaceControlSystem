package com.xlauncher.fis.service;

import com.xlauncher.fis.entity.SynUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :运营云同步用户服务
 **/
@Service
public interface SynUserService {

    /**
     * 运管云同步用户信息到本地数据库(方式一：全量：接口+接口)
     *
     * @param map map
     * @return int
     */
    void addSynUser(Map map);

    /**
     * 运管云同步用户信息到本地数据库(方式二：增量：MQ+接口)
     *
     * @param map map
     * @return int
     */
    void addSynUserByMQ(Map map);

    /**
     * 更新模型计算结果
     *
     * @param modelCalculation 模型计算结果
     * @param modelVersion 模型版本
     * @param userCard 用户身份证号码
     * @return int
     */
    int updateModel(String modelCalculation, int modelVersion, String userCard);

    /**
     * 查询指定酒店编号、入住天数的用户信息
     *
     * @param userHotel 酒店编号
     * @return List
     */
    List<SynUser> listSynUser(@Param("userHotel") String userHotel);
}
