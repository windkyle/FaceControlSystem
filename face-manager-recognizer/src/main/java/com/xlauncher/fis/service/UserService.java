package com.xlauncher.fis.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2018/10/17 0017
 * @Desc :
 **/
@Service
public interface UserService {
    /**
     * 用户登录
     *
     * @param userLogin 用户登录信息
     * @param responseMap responseMap
     * @return 模糊查询结果
     */
    Map<String,Object> login(Map<String, Object> userLogin, Map<String, Object> responseMap);

    /**
     * 用户退出
     *
     * @param token  token
     */
    void logout(String token);

    /**
     * 修改密码
     *
     * @param updateUser updateUser
     * @return Map
     */
    Map<String, Object> updatePassword(Map<String, Object> updateUser);
}
