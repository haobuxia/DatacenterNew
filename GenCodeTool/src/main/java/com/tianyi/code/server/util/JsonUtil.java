package com.tianyi.code.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.code.server.vo.ResponseVo;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    //json字符串转化为JSONObject类
    public static JSONObject JSON2Object(String json){
        JSONObject object=(JSONObject) JSONObject.parseObject(json);
        return object;
    }
    //json的list转化为ArrayList<JSONObject>
    public static ArrayList<JSONObject> JSONList2Object(String jsonList){
        ArrayList<JSONObject> objectList=(ArrayList<JSONObject>) JSONObject.parseObject(jsonList,ArrayList.class);
        return objectList;
    }

    //object转化为json
    public static String Object2JSON(Object o){
        return JSON.toJSONString(o);
    }

    public static ResponseVo jsonObj2ResponseVo(JSONObject jsonObj)
    {
        ResponseVo responseVo = ResponseVo.success(jsonObj);

        return responseVo;
    }

}
