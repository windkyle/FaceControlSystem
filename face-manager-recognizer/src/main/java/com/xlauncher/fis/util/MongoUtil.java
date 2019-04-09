package com.xlauncher.fis.util;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/4/4 0004
 * @Desc :MongoDB数据库访问工具操作类
 **/
@Component
public class MongoUtil {
    private static final String DB_NAME = "face_predict";
    private static final String PREDICT_IMAGE = "predict_image_";
    private static final String FACE_PHOTO = "face_photo_";
    private static Logger logger = Logger.getLogger(MongoUtil.class);
    private static MongoClient mongoClient;
    private static DB db;

    /**
     * 创建连接
     *
     * @return
     */
    private static MongoClient getMongoClient() {
        // 连接MongoDB，默认端口27017
        mongoClient = new MongoClient("127.0.0.1");
        // 连接数据库和集合，集合名可不填，GridFS会默认创建两个集合（fs.files(存储属性数据)、fs.chunks(存储图片数据)）
        db = mongoClient.getDB(DB_NAME);
        return mongoClient;
    }

    /**
     * 存储预测资源图片
     */
    public static void saveImage(int code, byte[] bytes, String predictTime) {
        mongoClient = getMongoClient();
        // 设置存储文件名
        String saveFileName = null;
        if (code == 0) {
            try {
                saveFileName = PREDICT_IMAGE + String.valueOf(DateTimeUtil.dateToStamp(predictTime));
                logger.info("-MongoDB-存储图片! saveFileName:" + saveFileName);
            } catch (ParseException e) {
                logger.error("数据格式错误!" + e);
            }
        }
        if (code == 1) {
            try {
                saveFileName = FACE_PHOTO + String.valueOf(DateTimeUtil.dateToStamp(predictTime));
                logger.info("-MongoDB-存储图片! saveFileName:" + saveFileName);
            } catch (ParseException e) {
                logger.error("数据格式错误!" + e);
            }
        }
        try {
            // 利用GridFS来管理数据库
            GridFS gridFS = new GridFS(db);
            // 将文件输入流封装到GridFS的文件流
            GridFSInputFile gridFSInputFile;
            gridFSInputFile = gridFS.createFile(bytes);
            // 封装属性
            gridFSInputFile.put("time", System.currentTimeMillis());
            gridFSInputFile.put("filename", saveFileName);
            gridFSInputFile.setContentType("jpg");
            gridFSInputFile.save();
        } catch (Exception e) {
            logger.error("数据流错误!" + e);
        } finally {
            mongoClient.close();
        }
    }

    /**
     * 查询资源图片
     *
     * @return
     */
    public static byte[] findPredictImage(String predictTime) {
        byte[] bytes = null;
        mongoClient = getMongoClient();
        GridFS gridFS = new GridFS(db);
        String findFileName = null;
        try {
            findFileName = PREDICT_IMAGE + String.valueOf(DateTimeUtil.dateToStamp(predictTime));
            logger.info("MongoDB查询图片! findFileName:" + findFileName);
        } catch (ParseException e) {
            logger.error("数据格式错误!" + e);
        }
        GridFSDBFile gridFSDBFile = gridFS.findOne(findFileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            gridFSDBFile.writeTo(outputStream);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            logger.error("Err.数据流错误!" + e);
        }
        return bytes;
    }
}
