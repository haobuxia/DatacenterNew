package com.tianyi.datacenter.common.vo;

/**
 *  web接口反馈数据对象
 *
 * Created by liuhanc on 2017/10/9.
 */
public class NetUserListVo<T> implements java.io.Serializable{
    private boolean success = true;
    private String code="200";//200表示成功. 601:账号注册成功，注册云信账号失败
    private String message;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static NetUserListVo success(){
        NetUserListVo vo = new NetUserListVo();
        return vo;
    }

    public static <T> NetUserListVo<T> success(T data){
        NetUserListVo vo = new NetUserListVo();
        vo.setData(data);
        return vo;
    }

    public static <T> NetUserListVo<T> success(T data,String message){
        NetUserListVo vo = new NetUserListVo();
        vo.setData(data);
        vo.setMessage(message);
        return vo;
    }

    public static NetUserListVo fail(String message){
        NetUserListVo vo = new NetUserListVo();
        vo.setSuccess(false);
        vo.setMessage(message);
        return vo;
    }

    public static NetUserListVo fail(String code, String message){
        NetUserListVo vo = new NetUserListVo();
        vo.setSuccess(false);
        vo.setCode(code);
        vo.setMessage(message);
        return vo;
    }

    public static <T> NetUserListVo<T> fail(String message,T data){
        NetUserListVo vo = new NetUserListVo();
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
