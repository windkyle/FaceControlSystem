package com.xlauncher.fgs.entity;

import com.sun.jna.NativeLong;

/**
 * 设备配置实体类
 *
 * @author baisl
 * @Date :2019/2/18 0018
 */
public class DeviceConfig {
    /**设备ID*/
    private NativeLong id;
    /**设备IP*/
    private String deviceIP;
    /**设备端口*/
    private int devicePort;
    /**设备用户名*/
    private String deviceUserName;
    /**设备密码*/
    private String devicePassWord;
    /**通道ID*/
    private NativeLong deviceSingleID;


    public NativeLong getId() {
        return id;
    }

    public void setId(NativeLong id) {
        this.id = id;
    }

    public String getDeviceIP() {
        return deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(int devicePort) {
        this.devicePort = devicePort;
    }

    public String getDeviceUserName() {
        return deviceUserName;
    }

    public void setDeviceUserName(String deviceUserName) {
        this.deviceUserName = deviceUserName;
    }

    public String getDevicePassWord() {
        return devicePassWord;
    }

    public void setDevicePassWord(String devicePassWord) {
        this.devicePassWord = devicePassWord;
    }

    public NativeLong getDeviceSingleID() {
        return deviceSingleID;
    }

    public void setDeviceSingleID(NativeLong deviceSingleID) {
        this.deviceSingleID = deviceSingleID;
    }

    @Override
    public String toString() {
        return "DeviceConfig{" +
                "id=" + id +
                ", deviceIP='" + deviceIP + '\'' +
                ", devicePort=" + devicePort +
                ", deviceUserName='" + deviceUserName + '\'' +
                ", devicePassWord='" + devicePassWord + '\'' +
                ", deviceSingleID=" + deviceSingleID +
                '}';
    }
}
