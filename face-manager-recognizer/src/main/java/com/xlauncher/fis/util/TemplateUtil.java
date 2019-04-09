package com.xlauncher.fis.util;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xlauncher.fis.service.BlocHotelService;
import com.xlauncher.fis.service.SynUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/4 0004
 * @Desc :运营云同步接口工具类
 **/
@Component
public class TemplateUtil {
    private static Logger logger = Logger.getLogger(TemplateUtil.class);
    private static final int CODE_OK = 200;
    private static final String DATA = "data";
    private String ip;
    private int port;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SynUserService synUserService;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Autowired
    private ThreadModelUtil threadModelUtil;
    @Autowired
    private BlocHotelService blocHotelService;


    /**
     * 按入住时间区间获取人员信息
     */
    void queryPersonByDateTime() {
        // 运营云ip、port、url
        ip = propertiesUtil.getValue("cloud.ip");
        port = Integer.parseInt(propertiesUtil.getValue("cloud.port"));
        // 运营用同步的时间区间（分钟）
        long time = Long.parseLong(propertiesUtil.getValue("unit.time"));
        // 运营云同步用户信息接口
        String cloudUrl = "http://" + ip + ":" + port + "/fdcservice/queryPerson/byDatetime";

        Map<String, String> postMap = new HashMap<>(1);
        // 按入住时间区间查询：开始时间
        String startTime = DateTimeUtil.getFormatTime(System.currentTimeMillis() - (10 * time * 60 * 1000));
        // 按入住时间区间查询：结束时间
        String endTime = DateTimeUtil.getFormatTime(System.currentTimeMillis());
        postMap.put("starttime", startTime);
        postMap.put("endtime", endTime);

        // Header设置
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(postMap, headers);
        logger.info("[运管云同步用户信息, 按入住时间区间获取人员信息] url." + cloudUrl + ", startTime." + startTime + ", endTime." + endTime);

        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(cloudUrl,httpEntity, Map.class);
            if (responseEntity.getStatusCodeValue() == CODE_OK) {
                logger.info("[开始同步用户信息]");
                synUserService.addSynUser(responseEntity.getBody());
            } else {
                logger.warn("[运营云服务异常!] StatusCodeValue." + responseEntity.getBody());
            }
        } catch (RestClientException e) {
            logger.error("[Err.按入住时间区间获取人员信息queryPersonByDateTime()异常!]" + e);
        }

    }

    /**
     * 按身份证号码获取图片信息
     */
    public byte[] queryPersonByIdCard(String idCard) {
        byte[] bytes = new byte[0];
        // 运营云ip、port、url
        ip = propertiesUtil.getValue("cloud.ip");
        port = Integer.parseInt(propertiesUtil.getValue("cloud.port"));
        // 运营云同步用户信息接口
        String cloudUrl = "http://" + ip + ":" + port + "/fdcservice/queryPerson/byIdcard";
//        String cloudUrl = "http://139.159.142.50:3344/fdcservice/queryPerson/byIdcard";

        // 传入参数
        Map<String, String> postMap = new HashMap<>(1);
        postMap.put("id", idCard);

        // Header设置
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(postMap, headers);

        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(cloudUrl,httpEntity, Map.class);
            if (responseEntity.getStatusCodeValue() == CODE_OK) {
                Map map = responseEntity.getBody();
                assert map != null;
                if (map.get(DATA) != null) {
                    try {
                        Map mapData = (Map) map.get(DATA);
                        // 获取用户身份证图片
                        String photo = (String) mapData.get("photo");
                        // BASE64解码
                        byte[] image = ImageUtil.stringToByte(photo);
                        logger.info("[获取用户身份证:" + idCard + "图片数据进行计算!] image.length()" + image.length);
                        if (image.length != 0) {
                            // 线程计算特征值
                            threadModelUtil.doModelUtil(idCard, image);
                        }
                        bytes = image;
                    } catch (InterruptedException | IOException e) {
                        logger.error("[Err.人脸识别预测线程异常]" + e);
                    }
                } else {
                    logger.warn(map.get("message"));
                }

            } else {
                logger.warn("[运营云服务异常!] StatusCodeValue." + responseEntity.getBody());
            }
        } catch (RestClientException e) {
            logger.error("[Err.按身份证号码获取图片信息queryPersonByIdCard()异常!]" + e);
        }
        return bytes;
    }

    public void getImageByIdCard(String idCard) {
        System.out.println("idCard." + idCard);
        // 运营云同步用户信息接口
        String cloudUrl = "http://139.159.142.50:3344/fdcservice/queryPerson/byIdcard";
        // 传入参数
        Map<String, String> postMap = new HashMap<>(1);
        postMap.put("id", idCard);
        // Header设置
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(postMap, headers);
        ResponseEntity<Map> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(cloudUrl,httpEntity, Map.class);
            if (responseEntity.getStatusCodeValue() == CODE_OK) {
                Map map = responseEntity.getBody();
                assert map != null;
                if (map.get(DATA) != null) {
                    try {
                        Map mapData = (Map) map.get(DATA);
                        // 获取用户身份证图片
                        String photo = (String) mapData.get("photo");
                        // BASE64解码
                        byte[] image = ImageUtil.stringToByte(photo);
                        logger.info("[获取用户身份证:" + idCard + "图片数据进行计算!] image.length()" + image.length);
                        if (image.length != 0) {
                            // byte数组保存图片
                            String fileName = "D:\\photo\\" + System.currentTimeMillis() +".jpg";
                            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileName));
                            imageOutput.write(image, 0, image.length);
                            imageOutput.close();
                        }
                    } catch (IOException e) {
                        logger.error("[Err.人脸识别预测线程异常]" + e);
                    }
                }
            }
        } catch (RestClientException e) {
            logger.error("[Err.按身份证号码获取图片信息queryPersonByIdCard()异常!]" + e);
        }
    }

    /**
     * 获取集团和酒店信息
     */
    public void queryBlocHotel() {
        // 运营云ip、port、url
        ip = propertiesUtil.getValue("cloud.ip");
        port = Integer.parseInt(propertiesUtil.getValue("cloud.port"));
        // 运营云同步用户信息接口
        String cloudUrl = "http://" + ip + ":" + port + "/fdcservice/";
        logger.info("[同步集团、酒店信息] " + cloudUrl);
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(cloudUrl, Map.class);
        if (responseEntity.getStatusCodeValue() == CODE_OK) {
            System.out.println(responseEntity.getBody());

            blocHotelService.addBlocHotel(responseEntity.getBody());
        }
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
//        pushUser("2019-03-21 16:00:30", "2019-03-23 18:00:00","白帅雷","410329199512269633");
//        System.out.println("------");
//        pushPhoto("410329199512269633", "wubin_ID");
        pushCameraPhoto("zhouguoxiao_ID");
    }

    /**
     * 模拟酒店自助机推送用户身份证图片
     *
     * @param id 用户身份证号码
     * @param photoName 图片路径（d:\\images\\test_1.jpg）
     */
    private static void pushPhoto(String id, String photoName) {
        RestTemplate restTemplate = new RestTemplate();
        // 运营云同步用户信息接口
        String cloudUrl = "http://139.159.142.50:3344/fdcservice/addCustomer";

        // 传入参数
        Map<String, String> postMap = new HashMap<>(1);
        postMap.put("id", id);
        InputStream is;
        try {
            is = new FileInputStream("D:\\朗澈科技\\白帅雷在贵阳出差\\img_crop_201\\" + photoName + ".jpg");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 12];
            int n;
            while ((n = is.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            byte[] bytes = out.toByteArray();
            postMap.put("photo", ImageUtil.base64ToString(bytes));

            // Header设置
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", "application/json;charset=UTF-8");
            HttpEntity<Object> httpEntity = new HttpEntity<Object>(postMap, headers);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(cloudUrl,httpEntity, Map.class);
            if (responseEntity.getStatusCodeValue() == CODE_OK) {
                System.out.println("    ");
                System.out.println(responseEntity.getBody());
                System.out.println("    ");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟酒店自助机用户入住
     *
     * @param checkinTime 入住时间
     * @param checkoutTime 离店时间
     * @param name 用户姓名
     * @param idCard 用户身份证号码
     * @throws IOException
     * @throws TimeoutException
     */
    private static void pushUser(String checkinTime, String checkoutTime, String name, String idCard) throws IOException, TimeoutException {
        //1.创建一个ConnectionFactory连接工厂connectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //2.通过connectionFactory设置RabbitMQ所在IP等信息
        connectionFactory.setHost("139.159.140.8");
        connectionFactory.setPort(45672);
        connectionFactory.setUsername("chh_checkin_person");
        connectionFactory.setPassword("chh_checkin_person@3008");
        connectionFactory.setVirtualHost("/chh_checkin_person");
        //3.通过connectionFactory创建一个连接connection
        Connection connection = connectionFactory.newConnection();
        //4.通过connection创建一个频道channel
        Channel channel = connection.createChannel();
        //5.通过channel指定一个队列
        channel.queueDeclare("chh_checkin_person", true, false, false, null);

        Map<String, Object> map1 = new HashMap<>(1);
//        File file = new File("D:\\朗澈科技\\白帅雷在贵阳出差\\img_crop_201\\liuxu_ID.jpg");
//        BufferedImage bufferedImage = ImageIO.read(file);
//        ByteArrayOutputStream buf = new ByteArrayOutputStream((int) file.length());
//        ImageIO.write(bufferedImage, "jpg", buf);
//        byte[] bytes = buf.toByteArray();
//        String imageTime = DateTimeUtil.getFormatTime(System.currentTimeMillis());
        map1.put("age", "22");
        map1.put("sex", "男");
        map1.put("checkinTime", checkinTime);
        map1.put("checkoutTime", checkoutTime);
        map1.put("hotelName", "宿Hotel酒店");
        map1.put("name", name);
        map1.put("idCard", idCard);

        String string = JSONObject.toJSONString(map1);

        //6.通过channel向队列中添加消息，第一个参数是转发器，使用空的转发器（默认的转发器，类型是direct）
        channel.basicPublish("chh_checkin_person", "chh_checkin_person", null, string.getBytes());
        System.out.println("发送了一条消息:" + map1);
        //7.关闭频道
        channel.close();
        //8.关闭连接
        connection.close();
    }

    /**
     * 模拟摄像头推送图片
     *
     * @throws IOException
     * @throws TimeoutException
     */
    private static void pushCameraPhoto(String imageName) throws IOException, TimeoutException {
        //1.创建一个ConnectionFactory连接工厂connectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //2.通过connectionFactory设置RabbitMQ所在IP等信息
        connectionFactory.setHost("8.16.0.6");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");
        connectionFactory.setVirtualHost("/");
        //3.通过connectionFactory创建一个连接connection
        Connection connection = connectionFactory.newConnection();
        //4.通过connection创建一个频道channel
        Channel channel = connection.createChannel();
        //5.通过channel指定一个队列
        channel.queueDeclare("fgs_fis_img", false, false, false, null);

        Map<String, Object> map1 = new HashMap<>(1);
        File file = new File("D:\\images\\img_crop_201\\" + imageName + ".jpg");
        BufferedImage bufferedImage = ImageIO.read(file);
        ByteArrayOutputStream buf = new ByteArrayOutputStream((int) file.length());
        ImageIO.write(bufferedImage, "jpg", buf);
        byte[] bytes = buf.toByteArray();
        String imageTime = DateTimeUtil.getFormatTime(System.currentTimeMillis());
        map1.put("HOTEL_ID", "fe3f279e4b8f46e7afd0076ffd4c5601");
        map1.put("HOTEL_NAME", "宿Hotel酒店");
        map1.put("IMAGE_DATA", ImageUtil.base64ToString(bytes));
        map1.put("IMAGE_SOURCE", ImageUtil.base64ToString(bytes));
        map1.put("IMAGE_TIME", imageTime);

        String string = JSONObject.toJSONString(map1);

        //6.通过channel向队列中添加消息，第一个参数是转发器，使用空的转发器（默认的转发器，类型是direct）
        channel.basicPublish("", "fgs_fis_img", null, string.getBytes());
        System.out.println("发送了一条消息:" + map1);
        //7.关闭频道
        channel.close();
        //8.关闭连接
        connection.close();
    }

    public Map addUser(String id, String image) {
        // 运营云同步用户信息接口
        String cloudUrl = "http://139.159.142.50:3344/fdcservice/addCustomer";

        // 传入参数
        Map<String, String> postMap = new HashMap<>(1);
//        postMap.put("age", "22");
//        postMap.put("checkinTime", "2019:03:08 15:40:26");
//        postMap.put("checkoutTime", "2019:03:09 12:00:00");
//        postMap.put("days", "1");
//        postMap.put("hotelid", "4201870005");
//        postMap.put("name", "白帅雷");
//        postMap.put("sex", "男");
        postMap.put("id", id);
        InputStream is;
        try {
            is = new FileInputStream("D:\\朗澈科技\\白帅雷在贵阳出差\\img_crop_201\\" + image + ".jpg");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024 * 12];
            int n;
            while ((n = is.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            byte[] bytes = out.toByteArray();
            postMap.put("photo", ImageUtil.base64ToString(bytes));

            // Header设置
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            headers.add("Content-Type", "application/json;charset=UTF-8");
            HttpEntity<Object> httpEntity = new HttpEntity<Object>(postMap, headers);

            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(cloudUrl,httpEntity, Map.class);
            if (responseEntity.getStatusCodeValue() == CODE_OK) {
                System.out.println("    ");
                System.out.println(responseEntity.getBody());
                System.out.println("    ");
                return responseEntity.getBody();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
