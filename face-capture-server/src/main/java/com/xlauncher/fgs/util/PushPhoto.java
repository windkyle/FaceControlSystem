package com.xlauncher.fgs.util;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/11 0011
 * @Desc :
 **/
public class PushPhoto {
    public static void main(String[] args) throws IOException, TimeoutException {
        pushPhoto();
    }
    private static void pushPhoto() throws IOException, TimeoutException {
        //1.创建一个ConnectionFactory连接工厂connectionFactory
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //2.通过connectionFactory设置RabbitMQ所在IP等信息
        connectionFactory.setHost("139.159.205.62");
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
        File file = new File("D:\\images\\img_crop_201\\zhouguoxiao_ID.jpg");
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
}
