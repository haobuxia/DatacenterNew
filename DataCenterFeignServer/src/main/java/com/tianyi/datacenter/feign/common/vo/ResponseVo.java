package com.tianyi.datacenter.feign.common.vo;

import java.util.Map;

/**
 *  web接口反馈数据对象
 *
 * @author wenxinyan
 * @version 0.1
 */
public class ResponseVo implements java.io.Serializable{
    private boolean success = true;
    private String message;
    private String code;
    private Map data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static ResponseVo success(Map data){
        ResponseVo vo = new ResponseVo();
        vo.setData(data);
        return vo;
    }

    public static ResponseVo success(){
        ResponseVo vo = new ResponseVo();
        return vo;
    }

    public static ResponseVo success(Map data, String message){
        ResponseVo vo = new ResponseVo();
        vo.setData(data);
        vo.setMessage(message);
        return vo;
    }

    public static ResponseVo fail(String message){
        ResponseVo vo = new ResponseVo();
        vo.setSuccess(false);
        vo.setMessage(message);
        return vo;
    }

    public static ResponseVo fail(String message, Map data){
        ResponseVo vo = new ResponseVo();
        vo.setSuccess(false);
        vo.setMessage(message);
        vo.setData(data);
        return vo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map getData() {
        return data;
    }

    public <T> T getData(String key, Class<T> clz){
        return clz.cast(data.get("key"));
    }

    public ResponseVo setData(Map data) {
        this.data = data;
        return this;
    }
}
