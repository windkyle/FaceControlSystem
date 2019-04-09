package com.xlauncher.fis.util.model;
import org.apache.log4j.Logger;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.framework.ConfigProto;
import org.tensorflow.framework.GPUOptions;
import org.tensorflow.types.UInt8;
import java.io.*;
import java.util.List;

/**
 * FaceRecogniton：加载人脸识别模型
 * @author liangjia
 * @date 2019-03-04
 */
public class FaceRecognition {
    private static SavedModelBundle recogniseModel;
    private static Logger logger = Logger.getLogger(FaceRecognition.class);
    private  String recogniseModelPath;

    public FaceRecognition(String recogniseModelPath){
        this.recogniseModelPath = recogniseModelPath;
    }

    /**
     * 加载人脸识别模型
     * @return boolean
     */
    public boolean loadModel(){
        try {

            recogniseModel = SavedModelBundle.load(recogniseModelPath, "serve");
        }
        catch (Exception e)
        {
            logger.error("加载人脸识别模型失败！");
            System.out.println("加载人脸识别模型失败！");
            return false;
        }

        if (null == recogniseModel){
            logger.error("加载人脸识别模型失败，模型为空！");
            System.out.println("加载人脸识别模型失败，模型为空！");
            return false;
        }
        return true;
    }
    //加载完模型关闭，释放内存
    public void closeModel(){
        if (null != recogniseModel) {
            recogniseModel.close();
        }
    }

    /**
     *根据加载的人脸识别模型，输入一张人脸图片得到该人脸的特征。
     * @param inputImageData Tensor<UInt8>
     * @return float[][]
     * @throws IOException
     */
    float[][] recognise(Tensor<UInt8> inputImageData) throws IOException{

        List<Tensor<?>> recogniseOutputs;
        ConfigProto config = ConfigProto.newBuilder()
                .setGpuOptions(GPUOptions.newBuilder()
                        .setAllowGrowth(true)
                        .setPerProcessGpuMemoryFraction(0.1)
                        .build()
                ).build();
        try {
            recogniseOutputs = recogniseModel
                    .session()
                    .runner()
                    .setOptions(config.toByteArray())
                    .feed("input", inputImageData)
                    .fetch("embeddings")
                    .run();
        }
        catch (Exception e){
            logger.error(e);
            System.out.println(e);
            return null;
        }

        if (recogniseOutputs.size() == 0){
            logger.info("No model output was obtained!");
            System.out.println("No model output was obtained!");
            return null;
        }

        Tensor<Float> embeddingsT = recogniseOutputs.get(0).expect(Float.class);
        int numberFaces = (int) embeddingsT.shape()[0];
        float[][] embeddings = embeddingsT.copyTo(new float[numberFaces][512]);

        if (numberFaces == 0){
            logger.info("There is no face recognised！");
            System.out.println("There is no face recognised！");
            return  null;
        }
        return embeddings;
    }
}


