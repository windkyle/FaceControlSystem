package com.xlauncher.fis.entity;


/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :人脸预测识别实体类
 **/
public class FacePredict {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 人脸预测识别时间
     */
    private String predictTime;

    /**
     * 酒店编号
     */
    private String predictHotel;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户年龄
     */
    private Integer userAge;

    /**
     * 用户性别
     */
    private Integer userSex;

    /**
     * 用户身份证号码
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
     * 是否遮挡
     */
    private Integer isShelter;

    /**
     * 是否异常
     */
    private Integer isAbnormal;

    /**
     * 预测识别抓取的图片数据
     */
    private byte[] predictImage;

    /**
     * 预测识别抓取的人脸图片数据
     */
    private byte[] facePhoto;

    /**
     * 人脸识别分析模型计算结果(float[]转换成字符串存储)
     */
    private String modelCalculation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPredictTime() {
        return predictTime;
    }

    public void setPredictTime(String predictTime) {
        this.predictTime = predictTime;
    }

    public String getPredictHotel() {
        return predictHotel;
    }

    public void setPredictHotel(String predictHotel) {
        this.predictHotel = predictHotel;
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

    public Integer getIsShelter() {
        return isShelter;
    }

    public void setIsShelter(Integer isShelter) {
        this.isShelter = isShelter;
    }

    public Integer getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(Integer isAbnormal) {
        this.isAbnormal = isAbnormal;
    }

    public byte[] getPredictImage() {
        if (predictImage != null) {
            return predictImage.clone();
        }
        return null;
    }

    public void setPredictImage(byte[] predictImage) {
        if (predictImage != null) {
            this.predictImage = predictImage.clone();
        } else {
            this.predictImage = null;
        }
    }

    public byte[] getFacePhoto() {
        if (facePhoto != null) {
            return facePhoto.clone();
        }
        return null;
    }

    public void setFacePhoto(byte[] facePhoto) {
        if (facePhoto != null) {
            this.facePhoto = facePhoto.clone();
        } else {
            this.facePhoto = null;
        }
    }

    public String getModelCalculation() {
        return modelCalculation;
    }

    public void setModelCalculation(String modelCalculation) {
        this.modelCalculation = modelCalculation;
    }

    @Override
    public String toString() {

        return "FacePredict{" +
                "id=" + id +
                ", predictTime='" + predictTime + '\'' +
                ", predictHotel='" + predictHotel + '\'' +
                ", userName='" + userName + '\'' +
                ", userAge=" + userAge +
                ", userSex=" + userSex +
                ", userCard='" + userCard + '\'' +
                ", isShelter=" + isShelter +
                ", checkinTime=" + checkinTime +
                ", checkoutTime=" + checkoutTime +
                ", isAbnormal=" + isAbnormal +
                ", predictImage=" + (predictImage != null ? predictImage.length : 0) +
                ", facePhoto=" + (facePhoto != null ? facePhoto.length : 0) +
                ", modelCalculation=" + (modelCalculation != null ? modelCalculation.length() : 0) +
                '}';
    }

}
