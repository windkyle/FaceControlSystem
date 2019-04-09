package com.xlauncher.fis.entity;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/1 0001
 * @Desc :用户登录实体类
 **/
public class User {
    /**
     * 用户名
     */
    private String userAccount;
    /**
     * 密码
     */
    private String userPassword;
    /**
     * token令牌
     */
    private String token;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "userAccount='" + userAccount + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
