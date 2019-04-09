package com.xlauncher.fgs.util.face;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.xlauncher.fgs.service.HCNetSDK;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/18 0018
 * @Desc :
 **/
public class GetStreamUtil {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("------------");
        getPicture("8.11.0.10", (short) 8000, "admin", "1qaz2wsx");
        System.out.println("------------");
    }


    private static void getPicture(String ip, short port, String userName, String password) throws InterruptedException {

        // 初始化
        HCNetSDK hcNetSDK = HCNetSDK.INSTANCE;
        if (!hcNetSDK.NET_DVR_Init()) {
            System.out.println("初始化失败!");
            return;
        }

        //登录参数，包括设备地址、登录用户、密码等
        HCNetSDK.NET_DVR_DEVICEINFO_V30 deviceinfoV30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
        //设备信息, 输出参数
        NativeLong id = hcNetSDK.NET_DVR_Login_V30(ip, port, userName, password, deviceinfoV30);

        System.out.println("NativeLong id." + id);
        if (id.intValue() < 0) {
            System.out.println("设备注册失败!" + hcNetSDK.NET_DVR_GetLastError());
            hcNetSDK.NET_DVR_Logout(id);
            hcNetSDK.NET_DVR_Cleanup();
            return;
        }


        HCNetSDK.FMSGCallBack_V31 fmsfcallbackV31 = (lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser) -> {
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

                        try {
                            // byte数组保存图片
                            String fileName = "D:\\sdkimage\\" + DateTimeUtil.getFormatTime(System.currentTimeMillis()) +"face.jpg";
                            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileName));
                            imageOutput.write(bytes, 0, bytes.length);
                            imageOutput.close();
                            //
                            MatUtil.matUtil(fileName, imageX, imageY, imageWidth, imageHeight, faceX, faceY, faceWidth, faceHeight);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

//                        HCNetSDK.NET_DVR_JPEGPARA netDvrJpegpara = new HCNetSDK.NET_DVR_JPEGPARA();
//                        netDvrJpegpara.wPicQuality = 2;
//                        netDvrJpegpara.wPicSize = 2;

//                        boolean pic = hcNetSDK.NET_DVR_CaptureJPEGPicture(id, nativeLong, netDvrJpegpara, fileName);
//                        if (pic) {
//                            System.out.println("保存图片成功!");
//                        } else {
//                            System.out.println("保存图片失败!");
//                            break;
//                        }
                    }
                    System.out.println("人脸抓拍报警!");
                    break;

                default:
                    System.out.println("其他报警, 报警类型：" + lCommand);
                    break;
            }

            return true;
        };

        //设置报警回调函数
        hcNetSDK.NET_DVR_SetDVRMessageCallBack_V31(fmsfcallbackV31, Pointer.NULL);

        //启用布防，人脸抓拍报警，上传COMM_UPLOAD_FACESNAP_RESULT类型报警信息
        HCNetSDK.NET_DVR_SETUPALARM_PARAM netDvrSetupalarmParam = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
        //byFaceAlarmDetection赋值为0即选择设备上传的报警信息类型为人脸抓拍类型
        netDvrSetupalarmParam.dwSize = netDvrSetupalarmParam.size();
        netDvrSetupalarmParam.byFaceAlarmDetection = 1;
        netDvrSetupalarmParam.write();
        //其他报警布防参数不需要设置，不支持
        NativeLong iHandle = hcNetSDK.NET_DVR_SetupAlarmChan_V41(id, netDvrSetupalarmParam);

        System.out.println("NativeLong iHandle." + iHandle);
        if (iHandle.intValue() < 0) {
            System.out.println("NativeLong iHandle is less than 0!");
            hcNetSDK.NET_DVR_Logout(id);
            hcNetSDK.NET_DVR_Cleanup();
            return;
        }

        //等待过程中，如果设备上传报警信息，在报警回调函数里面接收和处理报警信息
        System.out.println("等待过程中...");
        while(true) {
            Thread.sleep(2000);
        }

//        //撤销布防上传通道
//        if (!hcNetSDK.NET_DVR_CloseAlarmChan_V30(iHandle)) {
//            hcNetSDK.NET_DVR_Logout(id);
//            hcNetSDK.NET_DVR_Cleanup();
//            return;
//        }
//        System.out.println("注销用户，释放资源!");
//        //注销用户
//        hcNetSDK.NET_DVR_Logout(id);
//        //释放SDK资源
//        hcNetSDK.NET_DVR_Cleanup();


//
//        HCNetSDK.NET_DVR_WORKSTATE_V30  dvrWorkstateV30 = new HCNetSDK.NET_DVR_WORKSTATE_V30();
//        if (!hcNetSDK.NET_DVR_GetDVRWorkState_V30(id, dvrWorkstateV30)) {
//            System.out.println("返回设备状态失败!");
//        }
//
//        HCNetSDK.NET_DVR_JPEGPARA netDvrJpegpara = new HCNetSDK.NET_DVR_JPEGPARA();
//        netDvrJpegpara.wPicQuality = 2;
//        netDvrJpegpara.wPicSize = 2;
//
//        IntByReference intByReference = new IntByReference();
//
//        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
//        Date date = new Date();
//        int random = new Random().nextInt();
//        String fileName = "D:\\sdkimage\\face" + sdf.format(date) + "T" + random + ".jpg";
//        hcNetSDK.NET_DVR_CaptureJPEGPicture(id, nativeLong, netDvrJpegpara, fileName);


//        boolean result = hcNetSDK.NET_DVR_CaptureJPEGPicture_NEW(id,nativeLong,netDvrJpegpara, String.valueOf(buffer),1024*1024,intByReference);
//
//        if (result) {
//            System.out.println("pic." + buffer.array().length);
//            System.out.println("抓取图片成功!" + intByReference.getValue());
//
//            // 加载库文件
//            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//
//            // 将内存中byte数组转换成Mat
//            MatUtil img = byteToMat(buffer.array());
//
//            Rect rect =  new Rect(980, 250, 300, 400);
//
//            //设置ROI
//            MatUtil imgROI = new MatUtil(img, rect);
//
//            // 将截取的Mat类型转换成byte数组
//            byte[] jpg = mat2Byte(imgROI, ".jpg");
//
//            System.out.println("jpg." + jpg.length);
//
//            try {
//                // byte数组保存图片
//                FileImageOutputStream imageOutput = new FileImageOutputStream(new File("D:\\sdkimage\\image2.jpg"));
//                imageOutput.write(jpg, 0, jpg.length);
//                imageOutput.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            // Mat类型保存图片
//            Imgcodecs.imwrite("D:\\sdkimage\\image1.jpg", imgROI);
//
//        } else {
//            System.out.println("抓取图片失败!" + hcNetSDK.NET_DVR_GetLastError());
//        }
//        hcNetSDK.NET_DVR_Logout(id);
//        hcNetSDK.NET_DVR_Cleanup();
    }


}
