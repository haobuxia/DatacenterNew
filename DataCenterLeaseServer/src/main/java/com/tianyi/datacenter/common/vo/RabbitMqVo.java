package com.tianyi.datacenter.common.vo;

import java.io.Serializable;
import java.util.Map;

/**
 * 天远信令平台
 *
 * Created by tianxujin on 2019/09/05
 */
public class RabbitMqVo implements Serializable {
    private String layerLevel = "1"; //当前消息所在层级
    private String routingKey; // 路由键
    private String producerId = "inspect01"; // 生产者ID
    private String messageId; // 消息ID
    private String heartbeat = "false"; // true代表是心跳包，false代表不是
    private String sTime; // 消息生成时间戳
    private Map<String,Object> message;

    public RabbitMqVo(){}

    public String getLayerLevel() {
        return layerLevel;
    }

    public void setLayerLevel(String layerLevel) {
        this.layerLevel = layerLevel;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(String heartbeat) {
        this.heartbeat = heartbeat;
    }

    public String getsTime() {
        return sTime;
    }

    public void setsTime(String sTime) {
        this.sTime = sTime;
    }

    public Map<String, Object> getMessage() {
        return message;
    }

    public void setMessage(Map<String, Object> message) {
        this.message = message;
    }
}
