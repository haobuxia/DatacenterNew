package com.tianyi.datacenter.rabbitmq.impl;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.rabbitmq.MQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tianxujin on 2019/8/30 16:13
 */
@Service
public class MQProducerImpl implements MQProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private Logger logger = LoggerFactory.getLogger(MQProducerImpl.class);
    /* (non-Javadoc)
     * @see com.stnts.tita.rm.api.mq.MQProducer#sendDataToQueue(java.lang.String, java.lang.Object)
     */
    @Override
    public void sendDataToQueue(String queueKey, Object object) {
        try {
            String objectString = JSONObject.toJSONString(object);
            rabbitTemplate.convertAndSend("EX_BUS",queueKey, objectString);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
