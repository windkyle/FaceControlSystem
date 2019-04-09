package com.xlauncher.fis.util.model;

import org.opencv.core.Core;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;

public class Test {

    public Test(){

    }
    public static void main(String[] args) throws Exception {
//        String recogniseModelPath = args[0];
//        String faceDetectorModelPath = args[1];
//        String imgPath0 = args[2];
//        boolean loadFaceDetectModel = true;

        String recogniseModelPath = System.getProperty("user.dir") +"\\"+ "model_pb_1";
        String faceDetectorModelPath = "E:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml";
        String imgPath0 = System.getProperty("user.dir") +"\\"+ "img";
        boolean loadFaceDetectModel = true;

        Evaluator evaluator = new Evaluator(recogniseModelPath, faceDetectorModelPath);
        System.out.println("FaceClassifier create success.");

        byte[][] inputImages0 = readFiles(imgPath0);
        float[][][] embeddings = new float[inputImages0.length][][];
        for (int i=0; i<inputImages0.length; i++){
            float[][] embeddings0 = evaluator.evaluate(inputImages0[i], loadFaceDetectModel);
            System.out.println(Arrays.toString(embeddings0[0]));
            embeddings[i] = embeddings0;
        }

        float[][] dist = new float[embeddings.length][embeddings.length];
        for(int j=0; j<embeddings.length; j++){
            for(int k=0; k<embeddings.length; k++){
                dist[j][k] = distance(embeddings[j][0], embeddings[k][0]);
            }
        }

        String[] nameList0 = readfileNames(imgPath0);
        System.out.print("        ");
        System.out.println(Arrays.toString(nameList0));
        for(int m=0; m<dist.length; m++){
            System.out.print(nameList0[m]);
            System.out.print("  ");
            System.out.println(Arrays.toString(dist[m]));
        }
    }

    public static float distance( float[] emdedding0, float[] emdedding1){
        float dist = 0;
        for(int i=0; i<emdedding1.length; i++){
            float diff = emdedding1[i]-emdedding0[i];
            dist += Math.pow(diff, 2);
        }
        return dist;
    }
    public static byte[] makeImageTensor(File filename) throws IOException {
        //File filename1 = new File(filename);
        if (!filename.exists()) {
            System.out.printf("文件不存在！");
        }
        InputStream is = new FileInputStream(filename);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 12];
        int n;
        while ((n = is.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }


    public static byte[][] readFiles(String filePath) throws IOException{
        File file  = new File(filePath);

        if(!file.isDirectory()){
            System.out.println("文件夹地址不存在");
            return null;
        }
        String[] filelist = file.list();
        byte[][] inputImages = new byte[filelist.length][];
        for (int i = 0; i < filelist.length; i++) {
            System.out.println(filelist[i]);
            File readfile = new File(filePath + "/" + filelist[i]);
            byte[] imageData = makeImageTensor(readfile);
            if (null == imageData) {
                System.out.println("Local database has no image exit.");
            }
            inputImages[i] = imageData;
        }
        return inputImages;
    }

    public static String[] readfileNames(String filePath) throws IOException{
        File file  = new File(filePath);

        if(!file.isDirectory()){
            System.out.println("文件夹地址不存在");
            return null;
        }
        String[] filelist = file.list();
        String[] peopleName = new String[filelist.length];
        for (int i=0; i<filelist.length; i++){
            peopleName[i] = filelist[i].split("\\.")[0];
        }
        return peopleName;
    }

//    private static void singleSample(String imgPath0, String imgPath1, int imageSize, FaceClassifier faceclassifier,
//                                     double thresh) throws IOException{
//        byte[][] inputImages0 = readFiles(imgPath0);
//        float[][] embeddings0 = faceclassifier.computeEmbeddingSingle(inputImages0[0]);
//
//
//        if (inputImages0.length == 1){
//            embeddings0 = faceclassifier.computeEmbeddingSingle(inputImages0[0]);
//        }else{
//            embeddings0 = computeEmbeddings(inputImages0, imageSize);
//        }
//
//        byte[][] inputImages1 = readFiles(imgPath1);
//        float[][] embeddings1;
//
//
//        String[] nameList0 = readfileNames(imgPath0);
//        System.out.print("数据库人脸标签：");
//        System.out.println(Arrays.toString(nameList0));
//        String[] nameList1 = readfileNames(imgPath1);
//        System.out.print("待预测人脸标签：");
//        System.out.println(Arrays.toString(nameList1));
//        String[] classifyResults = classify(embeddings0, nameList0, embeddings1,thresh);
//        System.out.print("预测结果为：");
//        System.out.println(Arrays.toString(classifyResults));
//    }
//
//    private static void samples(String imgPath0, String imgPath1, int imageSize, FaceClassifier faceclassifier,
//                                     double thresh) throws IOException{
//        byte[][] inputImages0 = readFiles(imgPath0);
//        float[][] embeddings0;
//        if (inputImages0.length == 1){
//            embeddings0 = faceclassifier.computeEmbeddingSingle(inputImages0[0]);
//        }else{
//            embeddings0 = computeEmbeddings(inputImages0, imageSize);
//        }
//
//        byte[][] inputImages1 = readFiles(imgPath1);
//        float[][] embeddings1;
//        if (inputImages1.length == 1){
//            embeddings1 = faceclassifier.computeEmbeddingSingle(inputImages1[0]);
//        }else{
//            embeddings1 = computeEmbeddings(inputImages1, imageSize);
//        }
//
//        String[] nameList0 = readfileNames(imgPath0);
//        System.out.print("数据库人脸标签：");
//        System.out.println(Arrays.toString(nameList0));
//        String[] nameList1 = readfileNames(imgPath1);
//        System.out.print("待预测人脸标签：");
//        System.out.println(Arrays.toString(nameList1));
//        String[] classifyResults = faceclassifier.classify(embeddings0, nameList0, embeddings1,thresh);
//        System.out.print("预测结果为：");
//        System.out.println(Arrays.toString(classifyResults));
//    }




//    public static float[][] computeEmbeddings(byte[][] img, int imageSize) throws IOException{
//
//        int numberFaces = (int) img.length;
//        byte[][] inputData = new byte[numberFaces][];
//
//        for (int i=0; i<numberFaces; i++){
//            BufferedImage image = readImage(img[i]);
//            byte[] inputImageData = inputImageData(image);
//            inputData[i] = inputImageData;
//        }
//        byte[][][][] inputImages = attayToMatrix(inputData, imageSize);
//
//        Tensor input = Tensor.create(inputImages, UInt8.class);
//        System.out.println(Arrays.toString(input.shape()));
//
//        float[][] embeddings = facerecogniton.recognise(input);
//        System.out.println(embeddings.length);
//        System.out.println(embeddings[0].length);
//        System.out.println(Arrays.toString(embeddings[0]));
//        System.out.println(Arrays.toString(embeddings[1]));
//        return embeddings;
//    }



//    //  embeddings0数据库图片 embeddings1待分类图片
//    public static String[] classify(float[][] embeddings0, String[] nameList, float[][] embeddings1, double thresh){
//
//        double[][] dist = new double[embeddings1.length][embeddings0.length];
//        double[] distMin = new double[embeddings1.length];
//        int[] minIndex = new int[embeddings1.length];
//
//        for(int i=0; i<embeddings1.length; i++){
//            distMin[i] = 1000000000;
//            for(int j=0; j<embeddings0.length; j++){
//                dist[i][j] = distance(embeddings1[i], embeddings0[j]);
//                if(distMin[i]>dist[i][j]){
//                    distMin[i] = dist[i][j];
//                    minIndex[i] = j;
//                }
//            }
//        }
//        System.out.print("每张人脸与数据库中人脸的最小距离:");
//        System.out.println(Arrays.toString(distMin));
//        String[] faceClassify = new String[embeddings1.length];
//
//        for(int i=0; i<embeddings1.length; i++){
//            BigDecimal distminBig = new BigDecimal(distMin[i]);
//            BigDecimal threshBig = new BigDecimal(thresh);
//            if(distminBig.compareTo(threshBig)<0){
//                faceClassify[i] = nameList[minIndex[i]];
//            } else{
//                faceClassify[i] = "unknown";
//            }
//        }
//
//        return faceClassify;
//    }

    private static byte[][][][] attayToMatrix(byte[][] byteArray, int imgWeight){
        int number = byteArray.length;
        byte[][][] dim = new byte[number][40000][3];

        byte[][][][] imageMatrix = new byte[number][imgWeight][imgWeight][3];
        for(int i=0; i<number; i++){
            for(int j=1; j<=(byteArray[0].length/3); j++){
                dim[i][j-1][0] = byteArray[i][(j-1)*3];
                dim[i][j-1][1] = byteArray[i][1+(j-1)*3];
                dim[i][j-1][2] = byteArray[i][2+(j-1)*3];
            }
        }
        for(int i=0; i<number; i++){
            for(int j=1; j<=160; j++){
                for(int n=0; n<160; n++){
                    imageMatrix[i][j-1][n][0] = dim[i][(j-1)*160+n][0];
                    imageMatrix[i][j-1][n][1] = dim[i][(j-1)*160+n][1];
                    imageMatrix[i][j-1][n][2] = dim[i][(j-1)*160+n][2];
                }
            }

        }
        return imageMatrix;
    }
}
