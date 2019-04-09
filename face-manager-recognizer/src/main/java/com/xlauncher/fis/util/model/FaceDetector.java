package com.xlauncher.fis.util.model;

import org.apache.log4j.Logger;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class FaceDetector {
    private static CascadeClassifier faceDetectorModel;
    private static Logger logger = Logger.getLogger(FaceRecognition.class);
    private String faceDetectorModelPath;
    private static final String LINUX = "linux";
    private static final String WIN = "win";
    private static final String LINUX_FILE = "/libopencv_java345.so";
    private static final String WIN_FILE = "\\opencv_java345.dll";
    /**
     * 初始化加载openCV库文件，true为已加载；false为未加载
     */
    private volatile static boolean openCVLoaded = false;

    public FaceDetector(String faceDetectorModelPath){
        this.faceDetectorModelPath = faceDetectorModelPath;
    }

//    static{
//        synchronized (FaceDetector.class) {
//            if (!openCVLoaded) {
//            // 载入openCV的库
//            logger.info("[user.dir：] " + System.getProperty("user.dir"));
//            // 获得用户目录，用于加载依赖文件
//            String openCVName = System.getProperty("user.dir");
//            String os = System.getProperties().getProperty("os.name");
//            os = os.toUpperCase();
//            // 判断操作系统
//            if (os.toLowerCase().startsWith(LINUX)) {
//                openCVName += LINUX_FILE;
//            } else if(os.toLowerCase().startsWith(WIN)) {
//                openCVName += WIN_FILE;
//            }
//            logger.info("[openCV库文件路径：] " + openCVName);
//            System.load(openCVName);
//            openCVLoaded = true;
//            }
//        }
//    }

    public boolean loadModel(){
        try {
            faceDetectorModel = new CascadeClassifier(faceDetectorModelPath);
        }
        catch (Exception e)
        {
            logger.error("加载人脸检测模型失败！");
            return false;
        }
        return true;
    }

    //加载完模型关闭，释放内存
//    public void closeModel(){
//        if (null != faceDetectorModel) {
//            faceDetectorModel.close();
//        }
//    }

    /**
     * opencv实现人脸识别
     * @param image byte[]
     * @return byte[]
     * @throws Exception
     */
    public byte[] faceDetect(byte[] image) throws  Exception{

        Date date = new Date();
        long time1 = date.getTime();

        Mat imageMat = byteToMat(image);
        MatOfRect faceDetections = new MatOfRect();
//        double scale = 1.1;
//        int minNeighbors = 3;
//        int flags = 0;
//        Size minSize = new Size(40, 40);
//        faceDetectorModel.detectMultiScale(imageMat, faceDetections);
        try{
            Mat greyLena = new Mat();
            Imgproc.cvtColor(imageMat, greyLena, Imgproc.COLOR_RGB2GRAY);
            Imgproc.equalizeHist(greyLena, greyLena);
            faceDetectorModel.detectMultiScale(greyLena, faceDetections, 1.1, 3, Objdetect.CASCADE_SCALE_IMAGE, new Size(30, 30), new Size());

        }catch (Exception e){
            logger.error(e);
            return null;
        }

        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
        Rect[] rects = faceDetections.toArray();
        System.out.println(rects.length);
        Rect rectsOut;
        Mat sub;
        if(rects.length > 1){
            System.out.println("超过一个脸！");
            int maxSize = 0;
            int maxIndex = 0;
            for(int i=0; i<rects.length; i++){
                int faceWidth = rects[i].width;
                int faceHeight = rects[i].height;
                int faceSize = faceWidth * faceHeight;
                System.out.println(faceSize);
                if(faceSize > maxSize){
                    maxSize = faceSize;
                    maxIndex = i;
                }
            }
            System.out.println(maxSize);
            rectsOut = rects[maxIndex];
            sub = imageMat.submat(rectsOut);
        } else if(rects.length == 1) {
            sub = imageMat.submat(rects[0]);
        } else {
            logger.error("未检测到人脸！");
            return null;
        }

        byte[] faceImage = mat2Byte(sub, ".jpg");

        Date dateOut = new Date();
        long time = dateOut.getTime() - time1;
        System.out.println("time: " + time);

        return faceImage;
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
            logger.error("[byteToMat] Err." + e);
        }
        return data;
    }

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

//    public static void main(String[] args) throws Exception {
//        byte[] inputImage = readFiles("E:\\our_own_facenet\\img\\timg.jpg");
//        byte[] faceImage = faceDetect(inputImage);
//    }

    public static byte[] readFiles(String filePath) throws IOException{
        File readfile = new File(filePath);
        byte[] imageData = Test.makeImageTensor(readfile);
        if (null == imageData) {
            System.out.println("Local database has no image exit.");
        }
        return imageData;
    }
}
