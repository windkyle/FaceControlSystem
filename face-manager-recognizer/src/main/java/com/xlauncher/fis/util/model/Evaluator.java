package com.xlauncher.fis.util.model;

import org.apache.log4j.Logger;
import org.tensorflow.Tensor;
import org.tensorflow.types.UInt8;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Evaluator {
//    private volatile static Evaluator evaluator;
//    private volatile static boolean modelLoaded;
    private static Logger logger = Logger.getLogger(Evaluator.class);
    private static FaceRecognition faceRecognition = null;
    private static FaceDetector faceDetector = null;

    public Evaluator(String recogniseModelPath, String faceDetectorModelPath) {
        faceRecognition = new FaceRecognition(recogniseModelPath);
        faceDetector = new FaceDetector(faceDetectorModelPath);

        File recogniseModelFile = new File(recogniseModelPath);
        File faceDetectModelFile = new File(faceDetectorModelPath);
        if (!recogniseModelFile.exists() || !faceDetectModelFile.exists()) {
            logger.error("模型文件不存在！");
            System.out.println("模型文件不存在！");
        }
        //保证模型只加载一次
        boolean loadSucc = loadModel();
        if (!loadSucc) {
            logger.error("加载模型阶段出现异常情况！");
            System.out.println("加载模型阶段出现异常情况！");
        }
    }

    /**
     * 单例模式加锁操作，并保证模型只加载一次，否则返回null
     * @param recogniseModelPath String
     * @return evaluator
     */
//    public static Evaluator getInstance(String recogniseModelPath, String faceDetectorModelPath) {
//        if (null == evaluator) synchronized (Evaluator.class) {
//            if (null == evaluator) {
//                File recogniseModelFile = new File(recogniseModelPath);
//                File faceDetectModelFile = new File(faceDetectorModelPath);
//
//                if (!recogniseModelFile.exists() || !faceDetectModelFile.exists()) {
//                    logger.error("模型文件不存在！");
//                    System.out.println("模型文件不存在！");
//                    return null;
//                }
//
//                evaluator = new Evaluator(recogniseModelPath, faceDetectorModelPath);
//                //保证模型只加载一次
//                if (!evaluator.modelLoaded) {
//                    boolean loadSucc = evaluator.loadModel();
//                    if (!loadSucc) {
//                        logger.error("加载模型阶段出现异常情况！");
//                        System.out.println("加载模型阶段出现异常情况！");
//                        evaluator = null;
//                    }
//                }
//            }
//        }
//        return evaluator;
//    }

    /**
     * 加载模型，并判断是否加载成功
     *
     * @return boolean
     */
    private boolean loadModel() {
        if (null == faceRecognition || null == faceDetector) {
            logger.error("加载模型阶段出现异常情况！");
            System.out.println("加载模型阶段yi出现异常情况！");
            return false;
        }
        if(faceRecognition.loadModel()){
            if (faceDetector.loadModel()) {
                return true;
            }
            faceRecognition.closeModel();
        }
        return false;
    }

    //模型加载完毕关闭，释放内存
    public static void closeModel() {
        if (null != faceRecognition) {
            faceRecognition.closeModel();
        }
    }
    /**
     * 读取传入的图片byte数组
     * @param img byte[]
     * @return BufferedImage
     * @throws IOException
     */
    static BufferedImage readImage(byte[] img) throws IOException {
        InputStream is = new ByteArrayInputStream(img);

        if (null == is) {
            System.out.println("InputStream is null!");
            logger.info("InputStream is null!");
            return null;
        }

        BufferedImage bf = ImageIO.read(is);

        if (null == bf) {
            logger.info("BufferedImage is null!");
            System.out.println("BufferedImage is null!");
            return null;
        }

        if (bf.getType() != BufferedImage.TYPE_3BYTE_BGR) {
            throw new IOException(
                    String.format(
                            "Expected 3-byte BGR encoding in BufferedImage, found %d .",
                            bf.getType()));
        }
        return bf;
    }

    /**
     * 将读取的图片数据转换为模型需要的Tensor<UInt8>格式的数据
     * @param bf BufferedImage
     * @return Tensor<UInt8>
     * @throws IOException
     */
    private static Tensor<UInt8> inputImageData(BufferedImage bf) throws IOException {
        byte[] data = ((DataBufferByte) bf.getData().getDataBuffer()).getData();
        bgr2rgb(data);
        int height = bf.getHeight();
        int width = bf.getWidth();
        long[] shape = new long[]{1, height, width, 3};
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return Tensor.create(UInt8.class, shape, buffer);
    }

    /**
     * 将传入的BGR格式的图片数据转为RGB格式的
     * @param data byte[]
     */
    private static void bgr2rgb(byte[] data) {
        for (int i = 0; i < data.length; i += 3) {
            byte tmp = data[i];
            data[i] = data[i + 2];
            data[i + 2] = tmp;
        }
    }
    /**
     * 计算单张图片的特征向量
     * @param img byte[]
     * @return float[][]
     * @throws IOException
     */
    public float[][] evaluate(byte[] img, boolean loadFaceDetectModel) throws Exception{
        BufferedImage image;
        if(loadFaceDetectModel){
            byte[] faceImage = faceDetector.faceDetect(img);
            if (faceImage == null) {
                return null;
            }
            image = readImage(faceImage);
        } else {
            image = readImage(img);
        }
        Tensor<UInt8> inputImageData = inputImageData(image);

        return faceRecognition.recognise(inputImageData);
    }
}
