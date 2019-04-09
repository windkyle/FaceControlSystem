package com.xlauncher.fis.util;

import com.alibaba.fastjson.JSONObject;
import com.xlauncher.fis.service.SynUserService;
import org.springframework.amqp.core.Message;;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * @author :baisl
 * @Email :baishuailei@xlauncher.io
 * @Date :2019/3/4 0004
 * @Desc :运营云同步用户信息（RabbitMQ增量）
 **/
@Component
public class SynReceiver {
    @Autowired
    private SynUserService synUserService;

    /**
     * 运营云同步注册用户信息(方式二：增量：MQ+接口)
     *
     * @param message
     */
    @RabbitListener(queues = "chh_checkin_person")
    public void process(@Payload Message message) {
        String json = new String(message.getBody());
        Map map = JSONObject.parseObject(json,Map.class);
        synUserService.addSynUserByMQ(map);
    }

}
