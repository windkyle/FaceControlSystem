package com.xlauncher.fis.util;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.xlauncher.fis.dao.SynUserDao;
import com.xlauncher.fis.entity.SynUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/7 0007
 * @Desc :
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class TemplateUtilTest {
    @Autowired
    TemplateUtil templateUtil;
    @Autowired
    SynUserDao synUserDao;
    @Test
    public void queryPersonByDateTime() throws Exception {
        templateUtil.queryPersonByDateTime();
    }

    @Test
    public void queryPersonByIdCard() throws Exception {
        templateUtil.queryPersonByIdCard("52252919910501381");
    }

    @Test
    public void addUser() throws Exception {
//        String[] names = {"caoyan","chenchen","chenjiao","dingxiaohang","liangjia","liuxu","wangyaqi","wubin","zhangfen","zhangxiaolong","zhaoyihan","zhouguoxiao","abm","good","pix","qsx","timg","top","zhouguoxiao"};
//        String[] names = {"caoyan","liuxu","wangyaqi","wubin","zhangfen"};
//        List<SynUser> list = synUserDao.getListUser(450);
//        for (int i=0; i<list.size();i++) {
            templateUtil.addUser("410329199512269633", "liuxu_ID");
//        }
    }

    @Test
    public void pushMQ() throws Exception {
        List<SynUser> list = synUserDao.getListUser(450);
        String[] names = {"caoyan","liuxu","wangyaqi","wubin","zhangfen","chenjiao","dingxiaohang"};
        System.out.println("----------》》" + list.size());
        for (int i=0; i<list.size();i++) {
            templateUtil.addUser(list.get(i).getUserCard(), names[i]);
            System.out.println("_________________>>");
            Thread.sleep(5000);

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

            Map<String, Object> map = new HashMap<>(1);
            map.put("age", "22");
            map.put("sex", "男");
            map.put("checkinTime", list.get(i).getCheckinTime());
            map.put("checkoutTime", list.get(i).getCheckoutTime());
            map.put("hotelName", list.get(i).getUserHotel());
            map.put("name", list.get(i).getUserName());
            map.put("idCard", list.get(i).getUserCard());

//        File file = new File("D:\\sdkimage\\img_crop_200\\huangweiqi.jpg");
//        BufferedImage bufferedImage = ImageIO.read(file);
//        ByteArrayOutputStream buf = new ByteArrayOutputStream((int) file.length());
//        ImageIO.write(bufferedImage, "jpg", buf);
//        byte[] bytes = buf.toByteArray();
//        map.put("photo", ImageUtil.base64ToString(bytes));
            String requestInfo = JSONObject.toJSONString(map);

            //6.通过channel向队列中添加消息，第一个参数是转发器，使用空的转发器（默认的转发器，类型是direct）
            channel.basicPublish("chh_checkin_person", "chh_checkin_person", null, requestInfo.getBytes());
            System.out.println("添加了一条消息:" + map);
            //7.关闭频道
            channel.close();
            //8.关闭连接
            connection.close();

        }

    }
}