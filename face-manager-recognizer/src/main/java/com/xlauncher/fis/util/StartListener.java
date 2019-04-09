package com.xlauncher.fis.util;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.*;
import com.xlauncher.fis.dao.UserDao;
import com.xlauncher.fis.entity.RabbitMq;
import com.xlauncher.fis.entity.User;
import com.xlauncher.fis.service.FacePredictService;
import com.xlauncher.fis.util.model.Evaluator;
import com.xlauncher.fis.util.model.FaceDetector;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/2/19 0019
 * @Desc :
 **/
@Component
public class StartListener implements ApplicationContextAware {
    private static Channel channel;
    private static Logger logger = Logger.getLogger(StartListener.class);
    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private RabbitMq rabbitMq;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ThreadSynUtil threadSynUtil;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Autowired
    private ThreadModelUtil threadModelUtil;
    private ThreadArrayList threadArrayList = new ThreadArrayList();
    private Thread threadRecompute = new Thread(threadArrayList);

    public static Evaluator evaluator;
    private static final String LINUX = "linux";
    private static final String WIN = "win";
    private static final String LINUX_FILE = "/libopencv_java345.so";
    private static final String WIN_FILE = "\\opencv_java345.dll";
    private volatile static boolean openCVLoaded = false;
    /**
     * RabbitMQ 消费者
     *
     * @param applicationContext applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("[StartListener setApplicationContext]");

        logger.info("[RabbitMQ create channel !]");
        channel = rabbitMqUtil.createMQChannel();
        // queueDeclare第一个参数表示队列名称、第二个参数为是否持久化（true表示是，队列将在服务器重启时生存）、
        // 第三个参数为是否独占队列（创建者可以使用的私有队列了，断开后自动删除）、第四个参数为当前所有消费者客户端
        // 连接断开时是否自动删除队列，第五个参数为队列的其他参数。
        try {
            channel.queueDeclare(rabbitMq.getFisQueue(),false,false,false,null);
        } catch (IOException e) {
            logger.error("[RabbitMQ Err] 声明队列消息错误!" + e);
        }

        logger.info("[Waiting for the!]");
        final int[] count = {0};
        //DefaultConsumer类实现了Consumer接口，通过传入一个频道，
        // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
        Consumer consumer =  new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                    count[0]++;
                    logger.info("[接收人脸抓拍报警!] " + count[0]);
                    String message = new String(body, "UTF-8");
                    Map bodyMap = JSONObject.parseObject(message,Map.class);

                    String hotelId = (String) bodyMap.get("HOTEL_ID");
                    String hotelName = (String) bodyMap.get("HOTEL_NAME");
                    String imageDate = (String) bodyMap.get("IMAGE_DATA");
                    String imageSource = (String) bodyMap.get("IMAGE_SOURCE");
                    String imageTime = (String) bodyMap.get("IMAGE_TIME");
                    logger.info("[接收到消息!] hotelId." + hotelId + ", hotelName." + hotelName + ",imageTime." + imageTime);

                    Map<String, String> map = new HashMap<>(1);
                    map.put("hotelId", hotelId);
                    map.put("hotelName", hotelName);
                    map.put("bodyImage", imageDate);
                    map.put("sourceImage", imageSource);
                    map.put("imageTime", imageTime);
                    map.put("compareTime", DateTimeUtil.getFormatTime(System.currentTimeMillis()));
                    logger.info("------------>1");
                    threadModelUtil.doAddFacePredict(map);
                    logger.info("------------>2");
                    logger.info("Thread-id [" + Thread.currentThread().getId() + "]");
                }
        };

        try {
            // true自动回复队列应答, false手动确认 -- RabbitMQ中的消息确认机制
            channel.basicConsume(rabbitMq.getFisQueue(),true, consumer);
        } catch (IOException e) {
            logger.error("[RabbitMQ basicConsume] 消息确认出错!" + e);
        }
        // 初始化用户
        initUser();
        cvLoad();
        // 实例化模型
        getInstance();
    }

    /**
     * 初始化用户
     */
    private void initUser() {
        logger.info("[初始化用户!]");
        User user = userDao.getUserByAccount("admin");
        if (user != null) {
            logger.info("已存在admin用户：" + user);
        } else {
            User initUser = new User();
            initUser.setUserAccount("admin");
            initUser.setUserPassword(MD5Util.getResult("111111"));
            int count = userDao.initUser(initUser);
            logger.info("初始化admin用户成功：" + count + initUser);
        }
        threadSynUtil.timeTask();
    }

    private void cvLoad(){
        if (!openCVLoaded) {
            // 载入openCV的库
            logger.info("[user.dir：] " + System.getProperty("user.dir"));
            // 获得用户目录，用于加载依赖文件
            String openCVName = System.getProperty("user.dir");
            String os = System.getProperties().getProperty("os.name");
            os = os.toUpperCase();
            // 判断操作系统
            if (os.toLowerCase().startsWith(LINUX)) {
                openCVName += LINUX_FILE;
            } else if(os.toLowerCase().startsWith(WIN)) {
                openCVName += WIN_FILE;
            }
            logger.info("[openCV库文件路径：] " + openCVName);
            System.load(openCVName);
            openCVLoaded = true;
        }
    }


    /**
     * 实例化加载模型
     */
    private void getInstance(){
        logger.info("[实例化加载模型!]");
        // 模型路径
        String pathModel = propertiesUtil.getPath("model_pb_1");
        String pathXml = propertiesUtil.getPath("haarcascade_frontalcatface.xml");
        logger.info("1.model_pb_1 path：" + pathModel);
        logger.info("2.haarcascade_frontalcatface.xml path: " + pathXml);
        // 实例化模型
        evaluator = new Evaluator(pathModel, pathXml);
        logger.info("[FaceClassifier CREATE SUCCESS!]" + evaluator);
        threadRecompute.start();
    }
}