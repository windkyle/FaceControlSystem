package com.xlauncher.fis.entity;


/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :运营云同步用户注册信息实体类
 **/
public class SynUser {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 运营云同步用户姓名
     */
    private String userName;

    /**
     * 运营云同步用户年龄
     */
    private Integer userAge;

    /**
     * 运营云同步用户性别
     */
    private Integer userSex;

    /**
     * 运营云同步用户身份证号码
     */
    private String userCard;

    /**
     * 运营云同步用户入住时间
     */
    private String checkinTime;

    /**
     * 运营云同步用户离店时间
     */
    private String checkoutTime;

    /**
     * 运营云同步用户入住酒店编号
     */
    private String userHotel;

    /**
     * 用户身份证图片
     */
    private byte[] userImage;

    /**
     * 人脸识别分析模型计算结果(float[]转换成字符串存储)
     */
    private String modelCalculation;

    /**
     * 人脸识别分析模型版本
     */
    private int modelVersion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public Integer getUserSex() {
        return userSex;
    }

    public void setUserSex(Integer userSex) {
        this.userSex = userSex;
    }

    public String getUserCard() {
        return userCard;
    }

    public void setUserCard(String userCard) {
        this.userCard = userCard;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getUserHotel() {
        return userHotel;
    }

    public void setUserHotel(String userHotel) {
        this.userHotel = userHotel;
    }

    public byte[] getUserImage() {
        if (userImage != null) {
            return userImage.clone();
        }
        return null;
    }

    public void setUserImage(byte[] userImage) {
        if (userImage != null) {
            this.userImage = userImage.clone();
        } else {
            this.userImage = null;
        }
        this.userImage = userImage;
    }

    public String getModelCalculation() {
        return modelCalculation;
    }

    public void setModelCalculation(String modelCalculation) {
        this.modelCalculation = modelCalculation;
    }

    public int getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(int modelVersion) {
        this.modelVersion = modelVersion;
    }

    @Override
    public String toString() {
        return "SynUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", userAge=" + userAge +
                ", userSex=" + userSex +
                ", userCard='" + userCard + '\'' +
                ", checkinTime='" + checkinTime + '\'' +
                ", checkoutTime='" + checkoutTime + '\'' +
                ", userHotel='" + userHotel + '\'' +
                ", userImage='" + (userImage != null ? userImage.length : 0) + '\'' +
                ", modelCalculation='" + modelCalculation.length() + '\'' +
                ", modelVersion=" + modelVersion +
                '}';
    }
}
