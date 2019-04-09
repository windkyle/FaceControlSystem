package com.xlauncher.fgs.util;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.xlauncher.fgs.entity.DeviceConfig;
import com.xlauncher.fgs.service.HCNetSDK;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 设备取流工具
 *
 * @author baisl
 * @Date :2019/2/18 0018
 */
public final class GetStreamService {

    private static Logger LOGGER = Logger.getLogger(GetStreamService.class);
    private static HCNetSDK hcNetSDK;
    private volatile static GetStreamService getStreamService;
    private volatile static boolean sdkLoaded;
    private static Map<String, Object> getStreamMap;

    /**
     * 设备初始化状态值(初始化成功：1 ; 初始化失败：0)
     */
    private static final String INITIALIZATION = "initialization";

    /**
     * 设备注册状态值(注册成功：>=0 ; 注册失败：<0)
     */
    private static final String REGISTERS = "registers";


    /**
     * 单例模式加锁操作，并保证SDK只加载一次，否则返回null
     *
     * @param deviceConfig deviceConfig
     * @param channelNum channelNum
     * @return GetStreamService
     */
    public static GetStreamService getInstance(DeviceConfig deviceConfig, int channelNum) {
        synchronized (GetStreamService.class) {
            LOGGER.info("[getInstance new GetStreamService]");
            LOGGER.info("1___getStreamMap." + getStreamMap);
            getStreamService = new GetStreamService(deviceConfig, channelNum);
            if (!sdkLoaded) {
                boolean load = getStreamService.load();
                LOGGER.info("[getInstance sdkLoaded] " + load);
                if (!load) {
                    getStreamService = null;
                }
            }
        }
        return getStreamService;
    }

    /**
     * 判断是否初始化成功
     *
     * @return boolean
     */
    private boolean load(){

        if (getStreamMap.get(INITIALIZATION).equals(0)) {
            LOGGER.error("[SDK初始化失败!]");
            sdkLoaded = false;
        } else {
            LOGGER.info("[SDK初始化成功!]");
            sdkLoaded = true;
        }
        return sdkLoaded;
    }

    /**
     * 设备进行初始化
     *
     * @param deviceConfig deviceConfig
     * @param channelNum channelNum
     */
    public GetStreamService(DeviceConfig deviceConfig, int channelNum){
        LOGGER.info("[___________new GetStreamService___________]");
        getStreamMap = new Hashtable<>(1);
        // 调用海康SDK
        try {
            hcNetSDK = HCNetSDK.INSTANCE;
        } catch (Exception e) {
            LOGGER.error("[初始化海康SDK Err!]." + e);
        }
        LOGGER.info("[成功初始化海康SDK!]");
        // 通过通道号获取通道ID
        NativeLong id = new NativeLong(channelNum);
        deviceConfig.setDeviceSingleID(id);
        LOGGER.info("通过通道号获取通道ID:" + id);
        if (!hcNetSDK.NET_DVR_Init()){
            getStreamMap.put("initialization", 0);
            LOGGER.error("[SDK初始化失败] 直接返回结果" + hcNetSDK.NET_DVR_Init() + getStreamMap);
        }
        // 设置连接时间和重连次数
        hcNetSDK.NET_DVR_SetConnectTime(3000,1);
        hcNetSDK.NET_DVR_SetReconnect(10000,true);

        // 设备信息，登录参数，包括设备地址、登录用户、密码等
        HCNetSDK.NET_DVR_DEVICEINFO_V30 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();

        /**
         * 设备注册
         * 设备注册后会生成一个ID，并将这个ID赋予DeviceConfig对象中的设备ID属性
         */
        NativeLong deviceID = hcNetSDK.NET_DVR_Login_V30(deviceConfig.getDeviceIP(),(short)deviceConfig.getDevicePort(),deviceConfig.getDeviceUserName(),deviceConfig.getDevicePassWord(),deviceInfo);
        deviceConfig.setId(deviceID);
        if (deviceConfig.getId().intValue() < 0){
            getStreamMap.put("registers", deviceConfig.getId().intValue());
            getStreamMap.put("NET_DVR_GetLastError", hcNetSDK.NET_DVR_GetLastError());
            getStreamMap.put("initialization", 0);
            LOGGER.error("[设备注册失败] 返回结果: " + getStreamMap);

        } else {
            getStreamMap.put("registers", deviceConfig.getId().intValue());
            getStreamMap.put("initialization", 1);
            LOGGER.info("[设备注册成功] Device ID :" + deviceConfig.getId().intValue());
        }
    }

    /**
     * 设备登录注册
     *
     * @param deviceConfig deviceConfig
     * @return Map
     */
    public static Map<String, Object> login(DeviceConfig deviceConfig) {
        Map<String, Object> loginMap = new HashMap<>(1);
        // 设备信息
        HCNetSDK.NET_DVR_DEVICEINFO_V30 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        LOGGER.info("3___getStreamMap." + getStreamMap);
        /**
         * 设备注册
         * 设备注册后会生成一个ID，并将这个ID赋予DeviceConfig对象中的设备ID属性
         */
        NativeLong deviceID = hcNetSDK.NET_DVR_Login_V30(deviceConfig.getDeviceIP(),(short)deviceConfig.getDevicePort(),deviceConfig.getDeviceUserName(),deviceConfig.getDevicePassWord(),deviceInfo);
        deviceConfig.setId(deviceID);
        if (deviceConfig.getId().intValue() < 0){
            getStreamMap.put("registers", deviceConfig.getId().intValue());
            getStreamMap.put("NET_DVR_GetLastError", hcNetSDK.NET_DVR_GetLastError());
            getStreamMap.put("initialization", 0);
            LOGGER.error("[设备注册 - 失败] 返回结果: " + getStreamMap);
            return loginMap;
        } else {
            getStreamMap.put("registers", deviceConfig.getId().intValue());
            getStreamMap.put("initialization", 1);
            LOGGER.info("[设备注册 - 成功] Device ID: " + deviceConfig.getId().intValue());
        }
        loginMap.put("registers", deviceConfig.getId().intValue());
        return loginMap;
    }

    /**
     * 注册设备并获取设备状态和视频流
     *
     * @param deviceConfig deviceConfig
     * @param channelNum channelNum
     * @return Map
     */
    static void getStream(DeviceConfig deviceConfig, int channelNum) throws InterruptedException {
        System.out.println("2.注册设备并获取设备状态和视频流");
        int init = 0;
        int registers = 0;
        try {
            if (!sdkLoaded) {
                LOGGER.info("sdkLoaded");
                getInstance(deviceConfig, channelNum);
            }
            init = (int) getStreamMap.get(INITIALIZATION);
            registers = (int) getStreamMap.get(REGISTERS);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("[getStream - Err.]" + e);
        }
        if (init == 1 && registers >=0) {
            LOGGER.info("注册设备成功！Device ID :" + deviceConfig.getId().intValue());

            HCNetSDK.FMSGCallBack_V31 fmsfcallbackV31 = (lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser) -> {
                LOGGER.info("执行回调函数!");
                System.out.println("执行回调函数!");
                switch (lCommand.intValue()) {
                    case HCNetSDK.COMM_UPLOAD_FACESNAP_RESULT:
                        HCNetSDK.NET_VCA_FACESNAP_RESULT netVcaFacesnapResult = new HCNetSDK.NET_VCA_FACESNAP_RESULT();
                        netVcaFacesnapResult.write();
                        Pointer pFaceSnapInfo = netVcaFacesnapResult.getPointer();
                        pFaceSnapInfo.write(0, pAlarmInfo.getByteArray(0, netVcaFacesnapResult.size()), 0, netVcaFacesnapResult.size());
                        netVcaFacesnapResult.read();
                        // 抓拍图片坐标
                        float imageX = netVcaFacesnapResult.struRect.fX;
                        float imageY = netVcaFacesnapResult.struRect.fY;
                        float imageWidth = netVcaFacesnapResult.struRect.fWidth;
                        float imageHeight = netVcaFacesnapResult.struRect.fHeight;

                        // 人脸图片坐标
                        float faceX = netVcaFacesnapResult.struTargetInfo.struRect.fX;
                        float faceY = netVcaFacesnapResult.struTargetInfo.struRect.fY;
                        float faceWidth = netVcaFacesnapResult.struTargetInfo.struRect.fWidth;
                        float faceHeight = netVcaFacesnapResult.struTargetInfo.struRect.fHeight;

                        if (netVcaFacesnapResult.dwBackgroundPicLen >0 && netVcaFacesnapResult.pBuffer2 != Pointer.NULL) {
                            byte[] bytes = netVcaFacesnapResult.pBuffer2.getByteArray(0, netVcaFacesnapResult.dwBackgroundPicLen);
                            getStreamMap.put("bytes", bytes);
                            getStreamMap.put("imageX", imageX);
                            getStreamMap.put("imageY", imageY);
                            getStreamMap.put("imageWidth", imageWidth);
                            getStreamMap.put("imageHeight", imageHeight);
                            getStreamMap.put("faceX", faceX);
                            getStreamMap.put("faceY", faceY);
                            getStreamMap.put("faceWidth", faceWidth);
                            getStreamMap.put("faceHeight", faceHeight);
                            LOGGER.info("[Device gets pictures successfully ! ! !]");
                            LOGGER.info("[错误编码] " + hcNetSDK.NET_DVR_GetLastError());
                            LOGGER.info("[返回图片数据大小] " + bytes.length);
                            PushStreamUtil.pushStream(getStreamMap);
                        }
                        System.out.println("人脸抓拍报警!");
                        System.out.println("等待中...");
                        break;

                    default:
                        System.out.println("其他报警, 报警类型：" + lCommand);
                        break;
                }

                return true;
            };

            // 设置报警回调函数
            hcNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fmsfcallbackV31, Pointer.NULL);
            // 启用布防，人脸抓拍报警，上传COMM_UPLOAD_FACESNAP_RESULT类型报警信息
            HCNetSDK.NET_DVR_SETUPALARM_PARAM netDvrSetupalarmParam  = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            //byFaceAlarmDetection赋值为0即选择设备上传的报警信息类型为人脸抓拍类型
            netDvrSetupalarmParam.dwSize = netDvrSetupalarmParam.size();
            netDvrSetupalarmParam.byFaceAlarmDetection = 0;
            netDvrSetupalarmParam.write();

            //其他报警布防参数不需要设置，不支持
            NativeLong iHandle = hcNetSDK.NET_DVR_SetupAlarmChan_V41(deviceConfig.getId(), netDvrSetupalarmParam);
            if (iHandle.intValue() < 0) {
                System.out.println("NativeLong iHandle is less than 0!");
                hcNetSDK.NET_DVR_Logout(deviceConfig.getId());
                hcNetSDK.NET_DVR_Cleanup();
            }

            System.out.println("等待中...");

            while (true) {
                Thread.sleep(3000);
            }

        } else if (init == 0) {
            // 调用SDK、进行初始化失败
            LOGGER.error("[SDK初始化失败!].直接返回结果：hcNetSDK.NET_DVR_Init=" + hcNetSDK.NET_DVR_Init() + ", " + getStreamMap);
        } else {
            // 重新登录设备
            Map<String, Object> loginMap = login(deviceConfig);
            int initLogin = 0;
            try {
                initLogin = (int) loginMap.get(INITIALIZATION);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("[getDeviceStatusAndStream - Err]." + e);
            }
            if (initLogin == 1) {
                // 登录成功
                LOGGER.info("[设备登录注册成功!]");
                getStream(deviceConfig, channelNum);
            } else {
                // 登录失败
                LOGGER.error("[设备注册失败!]返回结果: " + getStreamMap);
            }
        }
    }
}
