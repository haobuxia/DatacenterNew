package com.tianyi.code.server.vo;

/**
 *  web接口反馈数据对象
 *
 * Created by liuhanc on 2017/10/9.
 */
public class ResponseVo<T> implements java.io.Serializable{
    private boolean success = true;
    private String message;
    private T data;

    public static ResponseVo success(){
        ResponseVo vo = new ResponseVo();
        return vo;
    }

    public static <T> ResponseVo<T> success(T data){
        ResponseVo vo = new ResponseVo();
        vo.setData(data);
        return vo;
    }

    public static <T> ResponseVo<T> success(T data,String message){
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

    public static <T> ResponseVo<T> fail(String message,T data){
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
