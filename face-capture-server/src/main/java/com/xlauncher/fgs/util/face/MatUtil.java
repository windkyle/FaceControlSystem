package com.xlauncher.fgs.util.face;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/14 0014
 * @Desc :
 **/
public class MatUtil {

    public static void matUtil(String fileName, float imageX, float imageY, float imageW, float imageH
            , float faceX, float faceY, float faceW, float faceH) throws IOException {
        File file = new File(fileName);
        BufferedImage bufferedImage = ImageIO.read(file);
        ByteArrayOutputStream buf = new ByteArrayOutputStream((int) file.length());
        ImageIO.write(bufferedImage, "jpg", buf);
        byte[] bytes = buf.toByteArray();
        // 加载库文件
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.load("C:\\Windows\\System32\\opencv_java345.dll");

        String dllName = System.getProperty("user.dir") + "\\" +Core.NATIVE_LIBRARY_NAME + ".dll";
        System.load(dllName);
        // 将内存中byte数组转换成Mat
        Mat img = byteToMat(bytes);
        int srcImgW = img.width();
        int srcImgH = img.height();

        // 截取人身图片
        Rect rect =  new Rect((int) (imageX * srcImgW), (int) (imageY * srcImgH)
                , (int) (imageW * srcImgW), (int) (imageH * srcImgH));
        //设置ROI
        Mat imgROI = new Mat(img, rect);
        // Mat类型保存图片
        Imgcodecs.imwrite("D:\\sdkimage\\" + DateTimeUtil.getFormatTime(System.currentTimeMillis())+"face1.jpg", imgROI);

        // 截取人脸图片
        Rect rect2 =  new Rect((int) (faceX *srcImgW) , (int) (faceY *srcImgH),
                (int) (faceW * srcImgW), (int)(faceH * srcImgH));
        Mat imgROI2 = new Mat(img, rect2);
        // Mat类型保存图片
        Imgcodecs.imwrite("D:\\sdkimage\\" + DateTimeUtil.getFormatTime(System.currentTimeMillis())+ "face3.jpg", imgROI2);

//        int ww = (int) (w * srcImgW);
//        int hh = (int) (h * srcImgH);
//        System.out.println("x-y " + x + ":" + y);
//        System.out.println("w-h " + w + ":" + h);
//        System.out.println("ww+hh " + ww + ":" + hh);
//        float rate = (float) 0.333;
//        if (y == 0) {
//            rate = (float) 0.27;
//        }
//        int x_new = (int) ((x + 0.25 * w) *srcImgW ) ;
//        if (x < 0.01){
//            x_new = 0;
//        }
//
//
//        int w_new = (int) (w * 0.45 * srcImgW);
//        if ((x+w) > 0.99){
//            w_new = (int) (w * srcImgW);
//        }
//        if(x_new == 0)
//        {
//            w_new = (int) (w * 0.575 * srcImgW);
//        }
//        if ((x_new + w_new) > srcImgW){
//            w_new = srcImgW - x_new;
//        }
//
//        int y_new = (int) ((y + 0.2 * h)*srcImgH) ;
//        if (y < 0.01){
//            y_new = 0;
//        }
//
//        int h_new = (int) (h * 0.55 *srcImgH);
//        if ((y+h) > 0.99){
//            h_new = (int) (h * srcImgH);
//        }
//        if(y_new == 0) {
//            h_new = (int) (h * 0.725  * srcImgH);
//        }
//        if ((y_new + h_new) > srcImgH){
//            h_new = srcImgH - y_new;
//        }
//        System.out.println("rate" + rate);
//        Rect rect1 =  new Rect(x_new, y_new,w_new, h_new);
////        Rect rect1 =  new Rect((int) (x *srcImgW) , (int) (y *srcImgH),
////                                (int) (w * srcImgW), (int)(h * srcImgH));
//        System.out.println("0.3* " + ww * 0.3 + " : " + hh * 0.3);
        //设置ROI
//        Mat imgROI1 = new Mat(img, rect1);
//        // Mat类型保存图片
//        Imgcodecs.imwrite("D:\\sdkimage\\" + DateTimeUtil.getFormatTime(System.currentTimeMillis())+ "face2.jpg", imgROI1);

    }

    /**
     * Mat类型转换成byte数组
     *
     * @param matrix        要转换的Mat
     * @param fileExtension 格式为 ".jpg", ".png", etc
     * @return byte[]
     */
    private static byte[] mat2Byte(Mat matrix, String fileExtension) {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(fileExtension, matrix, mob);
        return mob.toArray();
    }

    /**
     * byte数组转换成Mat类型
     *
     * @param image byte数组
     * @return Mat类型
     */
    private static Mat byteToMat(byte[] image) {
        BufferedImage bImage;
        Mat data = null;
        try {
            bImage = ImageIO.read(new ByteArrayInputStream(image));
            byte[] bytes = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
            data = new Mat(bImage.getHeight(), bImage.getWidth(), CvType.CV_8UC3);
            data.put(0, 0, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
