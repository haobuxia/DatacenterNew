package com.tianyi.datacenter.common.util;

public class StringUtil {

    public static boolean isEmpty(Object param) {
        if(param==null) {
            return true;
        }else if("".equals(param.toString().trim())){
            return true;
        }else {
            return false;
        }
    }


}