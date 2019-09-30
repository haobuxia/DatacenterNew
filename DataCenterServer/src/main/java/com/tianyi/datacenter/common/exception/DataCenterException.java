package com.tianyi.datacenter.common.exception;

public class DataCenterException extends Exception {

    public static final String DC_DO_8901 = "DCDO8901";
    public static final String DC_DO_8901_MSG = "未知的DML操作类型";

    public DataCenterException(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public DataCenterException(String message) {
        this.message = message;
    }

    private String code = "";
    private String message = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
