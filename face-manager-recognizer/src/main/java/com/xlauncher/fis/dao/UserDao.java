package com.xlauncher.fis.dao;

import com.xlauncher.fis.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/1 0001
 * @Desc :
 **/
@Service
public interface UserDao {

    /**
     * 初始化用户
     *
     * @param user user
     * @return int
     */
    @Insert("INSERT INTO user(account, password,token) VALUES(#{userAccount}, #{userPassword}, #{token})")
    @Results({
            @Result(column = "account", property = "userAccount"),
            @Result(column = "password", property = "userPassword"),
            @Result(column = "token", property = "token"),

    })
    int initUser(User user);

    /**
     * 根据用户名来查询用户
     *
     * @param userAccount 用户名
     * @return  User
     */
    @Select("SELECT account,password,token FROM user WHERE account=#{userAccount}")
    @Results({
            @Result(column = "account", property = "userAccount"),
            @Result(column = "password", property = "userPassword"),
            @Result(column = "token", property = "token"),
    })
    User getUserByAccount(@Param("userAccount") String userAccount);

    /**
     * 修改密码
     *
     * @param userAccount 用户名
     * @param userPassword 密码
     * @return  User
     */
    @Update("UPDATE user SET password=#{userPassword} WHERE account=#{userAccount}")
    int updatePassword(@Param("userPassword") String userPassword, @Param("userAccount") String userAccount);

    /**
     * 注销token
     *
     * @param token 用户令牌
     */
    @Update("UPDATE user SET token=NULL WHERE token=#{token}")
    void deleteToken(String token);
}
