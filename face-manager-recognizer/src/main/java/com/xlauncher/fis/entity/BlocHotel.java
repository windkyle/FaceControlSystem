package com.xlauncher.fis.entity;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/5 0005
 * @Desc :集团酒店对应关系
 **/
public class BlocHotel {
    /**
     * 主键
     */
    private int id;
    /**
     * 集团编号
     */
    private String blocId;
    /**
     * 集团名称
     */
    private String blocName;
    /**
     * 酒店编号
     */
    private String hotelId;
    /**
     * 酒店名称
     */
    private String hotelName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBlocId() {
        return blocId;
    }

    public void setBlocId(String blocId) {
        this.blocId = blocId;
    }

    public String getBlocName() {
        return blocName;
    }

    public void setBlocName(String blocName) {
        this.blocName = blocName;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    @Override
    public String toString() {
        return "BlocHotel{" +
                "id=" + id +
                ", blocId='" + blocId + '\'' +
                ", blocName='" + blocName + '\'' +
                ", hotelId='" + hotelId + '\'' +
                ", hotelName='" + hotelName + '\'' +
                '}';
    }
}
