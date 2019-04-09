package com.xlauncher.fgs.util;

import org.apache.log4j.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/20 0020
 * @Desc :数据类型转换工具类（Mat类型与byte类型）
 **/
public class MatByteUtil {
    private static Logger logger = Logger.getLogger(MatByteUtil.class);

    /**
     * Mat类型转换成byte数组
     *
     * @param matrix        要转换的Mat
     * @param fileExtension 格式为 ".jpg", ".png", etc
     * @return byte[]
     */
    public static byte[] mat2Byte(Mat matrix, String fileExtension) {
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
    public static Mat byteToMat(byte[] image) {
        BufferedImage bImage;
        Mat data = null;
        try {
            bImage = ImageIO.read(new ByteArrayInputStream(image));
            byte[] bytes = ((DataBufferByte) bImage.getRaster().getDataBuffer()).getData();
            data = new Mat(bImage.getHeight(), bImage.getWidth(), CvType.CV_8UC3);
            data.put(0, 0, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("[byteToMat] Err." + e);
        }
        return data;
    }
}
