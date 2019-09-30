package com.tianyi.datacenter.common.vo;

import java.util.Map;

/**
 * 头盔早期接口数据传输类
 *
 * Created by liuhanc on 2017/11/2.
 */
public class AppAccountVo {
    private String code;//200表示成功. 601:账号注册成功，注册云信账号失败
    private String msg;
    private Map<String,Object> data;

    public AppAccountVo(){}

    public AppAccountVo(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public AppAccountVo(String code, String msg, Map<String,Object> data){
        this(code,msg);
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
