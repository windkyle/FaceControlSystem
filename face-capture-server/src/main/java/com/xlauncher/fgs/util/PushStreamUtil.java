package com.xlauncher.fgs.util;

import com.alibaba.fastjson.JSONObject;
import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/14 0014
 * @Desc :
 **/
public class PushStreamUtil {
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(PushStreamUtil.class);
    private static final String LINUX = "linux";
    private static final String WIN = "win";
    private static final String LINUX_FILE = "/libopencv_java345.so";
    private static final String WIN_FILE = "\\opencv_java345.dll";
    /**
     * 将opencv动态库所在路径加载到环境变量
     *
     * @param path
     */
    private void addToPath(String path) {
        try {
            // 获取系统path变量对象
            Field field = ClassLoader.class.getDeclaredField(path);
            // 设置此变量对象可访问
            field.setAccessible(true);
            // 获取此变量对象的值
            String[] paths = (String[]) field.get(null);
            // 创建字符串数组，在原来的数组长度上增加一个，用于存放增加的目录
            String[] tmp = new String[paths.length+1];
            // 将原来的paths变量复制到tmp中
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            // 将增加的目录存入新的变量数组中
            tmp[paths.length] = path;
            // 将增加目录后的数组赋给paths变量对象
            field.set(null, tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        // 获取存放dll库文件的绝对路径
        String path = System.getProperty("user.dir");
        System.out.println("path." + path);
        // 将此目录添加到环境变量中
        addToPath(path);
        // 加载相应的dll文件，注意要将'\'替换成'/'
        System.load(path.replaceAll("\\\\","/") + "/opencv_java345.dll");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("**********");
    }

    /**
     * 推送图片
     *
     * @param map
     */
    public static void pushStream(Map<String,  Object> map) {
        // 使用RabbitMQ消息队列发送消息
        byte[] bytes = (byte[]) map.get("bytes");
        if (bytes.length != 0) {
            try {
                // 加载库文件
                String openCVName = System.getProperty("user.dir");
                System.out.println("**user.dir." + openCVName);
                String os = System.getProperties().getProperty("os.name");
                os = os.toUpperCase();
                // 判断操作系统
                if (os.toLowerCase().startsWith(LINUX)) {
                    openCVName += LINUX_FILE;
                } else if(os.toLowerCase().startsWith(WIN)) {
                    openCVName += WIN_FILE;
                }
                System.out.println("**openCVName." + openCVName);
                System.load(openCVName);
                System.out.println("3.推送图片流");
                // 人身坐标
                float imageX = (float) map.get("imageX");
                float imageY = (float) map.get("imageY");
                float imageW = (float) map.get("imageWidth");
                float imageH = (float) map.get("imageHeight");
                // 人脸坐标
                float faceX = (float) map.get("faceX");
                float faceY = (float) map.get("faceY");
                float faceW = (float) map.get("faceWidth");
                float faceH = (float) map.get("faceHeight");

                System.out.println("image: x." + imageX + ",y." + imageY + ",w." + imageW + ",h." + imageH);
                System.out.println("face: x." + faceX + ",y." + faceY + ",w." + faceW + ",h." + faceH);

                // 将byte数组转换成Mat
                Mat matBytes = MatByteUtil.byteToMat(bytes);
                // 图片大小
                int srcImgW = matBytes.width();
                int srcImgH = matBytes.height();

                // 截取人身
                Rect rectSource =  new Rect((int) (imageX * srcImgW), (int) (imageY * srcImgH),
                                    (int) (imageW * srcImgW), (int) (imageH * srcImgH));
                //设置ROI
                Mat imgROISource = new Mat(matBytes, rectSource);
                // 将截取的Mat类型转换成byte数组
                byte[] imageSource = MatByteUtil.mat2Byte(imgROISource, ".jpg");

                // 截取人脸
                Rect rectData =  new Rect((int) (faceX * srcImgW), (int) (faceY * srcImgH),
                        (int) (faceW * srcImgW), (int) (faceH * srcImgH));
                Mat imgROIData = new Mat(matBytes, rectData);
                byte[] imageData = MatByteUtil.mat2Byte(imgROIData, ".jpg");

                // 设置JVM编码JAVA_TOOL_OPTIONS-Dfile.encoding=UTF-8
                Map<String, Object> map1 = new HashMap<>(1);
                String hotelId = ReadFileUtil.readFile().get("hotelId");
                String hotelName = ReadFileUtil.readFile().get("hotelName");
                String imageTime = DateTimeUtil.getFormatTime(System.currentTimeMillis());
                map1.put("HOTEL_ID", hotelId);
                map1.put("HOTEL_NAME", hotelName);
                map1.put("IMAGE_SOURCE", ImageUtil.base64ToString(imageSource));
                map1.put("IMAGE_DATA", ImageUtil.base64ToString(imageData));
                map1.put("IMAGE_TIME", imageTime);

                String requestInfo = JSONObject.toJSONString(map1);
                System.out.println("发送数据:" + hotelId + ", " + hotelName + ", " + imageTime);

                com.rabbitmq.client.Channel channelMQ = RabbitMqUtil.createMQChannel();
                // MQ队列名称
                String queue = ReadFileUtil.readFile().get("rabbitMQ.queue");

                if (channelMQ != null) {
                    System.out.println("4.RabbitMQ Channel创建成功!");
                    // 声明一个队列
                    // queueDeclare第一个参数表示队列名称、第二个参数为是否持久化（true表示是，队列将在服务器重启时生存）、
                    // 第三个参数为是否独占队列（创建者可以使用的私有队列了，断开后自动删除）、第四个参数为当前所有消费者客户端
                    // 连接断开时是否自动删除队列，第五个参数为队列的其他参数。

                    channelMQ.queueDeclare(queue, false, false, false, null);
                    // 发送消息到队列中
                    // basicPublish第一个参数为交换机名称、第二个参数为队列映射的路由key、
                    // 第三个参数为消息的其他属性、第四个参数为发送消息的主体
                    channelMQ.basicPublish("", queue, null, requestInfo.getBytes());
                } else {
                    System.out.println("4.RabbitMQ Channel创建失败!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("RabbitMQ.Err 创建通道和连接:" + e);
            } finally {
                LOGGER.info("[RabbitMQ.release 释放资源]");
                System.out.println("5.释放资源");
                RabbitMqUtil.release();
            }
        } else {
            LOGGER.error("获取视频图片流为空!");
        }
    }
}
