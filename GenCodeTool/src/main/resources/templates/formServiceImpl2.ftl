package ${package_name}.service.impl;

import ${package_name}.service.${table_name}Service;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tianyi.code.server.util.HttpClientUtil;
import com.tianyi.code.server.util.JsonUtil;
import com.tianyi.code.server.vo.ResponseVo;
import org.springframework.stereotype.Service;
import com.tianyi.code.server.vo.ResponseVo;

/**
* 描述：${table_annotation} 服务实现层
* @author ${author}
* @date ${date}
* @version
*/
@Service
public class ${table_name}ServiceImpl implements ${table_name}Service {
private String severUrl = "http://localhost:19081";

    @Override
    public ResponseVo listBy(JSONObject jsonObject){
        //测试服务接口
        String requestUrl= severUrl + "/data/retrieve";
        Gson gson=new Gson();
        //传入的参数
        String param=gson.toJson(jsonObject);
        String httpRes = HttpClientUtil.sendPost(requestUrl,param);
        JSONObject jsonObj = JsonUtil.JSON2Object(httpRes);
        ResponseVo responseVo =  ResponseVo.success(jsonObj);
        return responseVo;
    }

    @Override
    public ResponseVo listByNoPage(JSONObject jsonObject){
        //测试服务接口
        String requestUrl=severUrl +"/data/retrieve";
        Gson gson=new Gson();
        //传入的参数
        String param=gson.toJson(jsonObject);
        String httpRes = HttpClientUtil.sendPost(requestUrl,param);
        JSONObject jsonObj = JsonUtil.JSON2Object(httpRes);
        ResponseVo responseVo =  ResponseVo.success(jsonObj);
        return responseVo;
    }

    @Override
    public ResponseVo add(JSONObject jsonObject){
        //测试服务接口
        String requestUrl=severUrl +"/data/add";
        Gson gson=new Gson();
        //传入的参数
        String param=gson.toJson(jsonObject);
        String httpRes = HttpClientUtil.sendPost(requestUrl,param);
        JSONObject jsonObj = JsonUtil.JSON2Object(httpRes);
        ResponseVo responseVo =  ResponseVo.success(jsonObj);
        return responseVo;
    }
    @Override
    public ResponseVo deleteBy(JSONObject jsonObject){
        //测试服务接口
        String requestUrl= severUrl +"/data/delete";
        Gson gson=new Gson();
        //传入的参数
        String param=gson.toJson(jsonObject);
        String httpRes = HttpClientUtil.sendPost(requestUrl,param);
        JSONObject jsonObj = JsonUtil.JSON2Object(httpRes);
        ResponseVo responseVo =  ResponseVo.success(jsonObj);
        return responseVo;
    }

    @Override
    public ResponseVo updateBy(JSONObject jsonObject){
        //测试服务接口
        String requestUrl=severUrl +"/data/update";
        Gson gson=new Gson();
        //传入的参数
        String param=gson.toJson(jsonObject);
        String httpRes = HttpClientUtil.sendPost(requestUrl,param);
        JSONObject jsonObj = JsonUtil.JSON2Object(httpRes);
        ResponseVo responseVo =  ResponseVo.success(jsonObj);
        return responseVo;
     }
}
