package com.xlauncher.fis.service.impl;


import com.xlauncher.fis.dao.UserDao;
import com.xlauncher.fis.entity.User;
import com.xlauncher.fis.service.UserService;
import com.xlauncher.fis.util.Jwt;
import com.xlauncher.fis.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 用户登录
     *
     * @param userLogin   用户登录信息
     * @param responseMap responseMap
     * @return Map
     */
    @Override
    public Map<String, Object> login(Map<String, Object> userLogin, Map<String, Object> responseMap) {
        logger.info("[用户登录]" + userLogin);
        User user = userDao.getUserByAccount( (String) userLogin.get(ACCOUNT));
        if(user != null && MD5Util.getResult((String) userLogin.get(PASSWORD)).equals(user.getUserPassword())) {
            String token = Jwt.sign(responseMap, 24 * 60 * 60 * 1000L);
            if (token != null) {
                user.setToken(token);
            }
            responseMap.put("token", token);
            responseMap.put("code","200");
        } else {
            responseMap.put("Err","Input is not correct! Please log in again!!!!!");
            responseMap.put("code","404");
        }
        return responseMap;
    }

    /**
     *  用户退出
     *
     * @param token 令牌
     */
    @Override
    public void logout(String token) {
        logger.info("[用户退出]");
        userDao.deleteToken(token);
    }

    /**
     * 修改密码
     *
     * @param updateUser updateUser
     * @return Map
     */
    @Override
    public Map<String, Object> updatePassword(Map<String, Object> updateUser) {
        Map<String, Object> map =  new HashMap<>(1);
        String account = (String) updateUser.get(ACCOUNT);
        String password = (String) updateUser.get(PASSWORD);
        String newPassword = (String) updateUser.get("newPassword");
        User user = userDao.getUserByAccount(account);
        if(user != null && MD5Util.getResult(password).equals(user.getUserPassword())) {
            int result = userDao.updatePassword(MD5Util.getResult(newPassword), account);
            map.put("result", result);
            map.put("code", 200);
        } else {
            map.put("code", 401);
            map.put("msg","Input is not correct!");
        }
        return map;
    }

}
