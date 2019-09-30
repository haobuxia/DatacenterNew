package com.tianyi.code.server.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 异常封装类
 */
public class ServiceException extends RuntimeException{
    static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(ServiceException.class);

    public static final String SYSTEM_DATA_ERROR="系统数据出错";
    private String errorMsg;
    private String stackMsg;
    public boolean errorFlag;

    private String code;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setErrorCode(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public ServiceException(String errorMsg, boolean isError){
        super();
        this.errorMsg = errorMsg;
        this.errorFlag = isError;
    }

    public ServiceException(String errorMsg, boolean isError, String code){
        super();
        this.errorMsg = errorMsg;
        this.errorFlag = isError;
        this.code = code;
    }

    public ServiceException(String errorMsg){
        super();
        this.errorMsg = errorMsg;
        setMessage();
    }

    public ServiceException(Throwable ex, String errorMsg){
        this(errorMsg,ex);
    }

    public ServiceException(String errorMsg, Throwable ex){
        super(ex);
        setStackTrace(ex.getStackTrace());
        this.errorMsg = errorMsg;
        setMessage();
    }

    public String getMessage() {
        return this.errorMsg;
    }
    private void setMessage() {
        if (getCause()!=null){
            StringBuffer errorMessages = new StringBuffer();
            errorMessages.append(getCause().getMessage()+"\n");
            StackTraceElement[] traces =  getCause().getStackTrace();
            for (int ni=0; ni<traces.length; ni++)
                errorMessages.append(traces[ni].toString()+"\n");
            this.stackMsg = errorMessages.toString();
        }
    }

    public String getStackTraceMessage(){
        return this.stackMsg;
    }
}
