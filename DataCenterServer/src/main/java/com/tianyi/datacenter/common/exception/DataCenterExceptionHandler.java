package com.tianyi.datacenter.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 公共异常类处理
 *
 * @author zhouwei
 * 2018/11/27 08:29
 * @version 0.1
 **/
@ControllerAdvice
public class DataCenterExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(DataCenterExceptionHandler.class);

    //同样实现打印日志处理异常
    @ExceptionHandler
    public Map<String, Object> publicExceptionHandler(Exception e){
        logger.error("", e);
        Map errorInfo = new HashMap();
        errorInfo.put("success", false);
        errorInfo.put("code", "DC009999");
        errorInfo.put("message", "未知异常"+e.getClass().getSimpleName());
        return errorInfo;
    }


}
