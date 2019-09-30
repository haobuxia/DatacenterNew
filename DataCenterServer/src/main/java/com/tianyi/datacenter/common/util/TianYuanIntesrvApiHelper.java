package com.tianyi.datacenter.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.config.TianYuanConfig;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * //TODO 说明
 *
 * @author zhouwei
 * 2019/1/15 09:35
 * @version 0.1
 **/
@Service
public class TianYuanIntesrvApiHelper {
    Logger logger = LoggerFactory.getLogger(TianYuanIntesrvApiHelper.class);
    @Autowired
    private TianYuanConfig configService;

    private static final String API_URL_PREFIX = "/dwf/v1/";
    /**
     * 将基准url和接口具体uri连接成一个完整的url
     *
     * @param baseUrl
     * @param uri
     * @return
     */
    public static String concatUrl(String baseUrl, String uri) {
        if (baseUrl.endsWith("/")) {
            if (uri.startsWith("/"))
                return baseUrl + uri.substring(1);
            else
                return baseUrl + uri;
        } else {
            if (uri.startsWith("/"))
                return baseUrl + uri;
            else
                return baseUrl + "/" + uri;
        }
    }
    /**
     * get请求
     * @return
     */
    public JSONObject doGet(String url, String authorization) {
        String respTxt = null;
        try {
            //发送get请求
            HttpGet httpGet = new HttpGet(url);
            // 设置请求的header
            if (!StringUtils.isEmpty(authorization)) {
                httpGet.addHeader("Authorization", authorization);
            }
            CloseableHttpClient client = HttpClientBuilder.create().build();

            HttpResponse response = client.execute(httpGet);
            int code = response.getStatusLine().getStatusCode();
            if (code == HttpStatus.SC_OK) {
                respTxt = EntityUtils.toString(response.getEntity());
            }
            logger.debug("天远提交json请求.url=" + url + ",反馈=" + respTxt);
        } catch (Exception e) {
            throw new RuntimeException("调用天远接口失败." + e.getMessage(), e);
        }
        return parseResp(respTxt, "json");
    }

    public JSONObject getResult(String apiUri) {
        apiUri = concatUrl(API_URL_PREFIX, apiUri);
        String url = concatUrl(configService.getTianYuanIntesrvProdUrl(), apiUri);
        JSONObject retJson = doGet(url, configService.getTianYuanIntesrvJwt());
        return retJson;
    }

    public JSONObject getJson(String apiUri, JSONObject reqJson) {
        apiUri = concatUrl(API_URL_PREFIX, apiUri);
        String url = concatUrl(configService.getTianYuanIntesrvProdUrl(), apiUri);
        JSONObject retJson = postJson(url, reqJson.toJSONString(),
                configService.getTianYuanIntesrvJwt(), "json");
        return retJson;
    }

    /**
     * 执行某个url请求
     *
     * @param url
     * @param requestJson
     * @return
     */
    public JSONObject postJson(String url, String requestJson, String authorization, String respFormat) {
        HttpPost httpPost = new HttpPost(url);
        // 设置请求的header
        if (!StringUtils.isEmpty(authorization)) {
            httpPost.addHeader("Authorization", authorization);
        }

        // 设置请求的参数
        createJsonEntity(requestJson, httpPost);

        String respTxt = null;
        try {
            respTxt = executeHttp(httpPost, false);
            logger.debug("天远提交json请求.url=" + url + ",反馈=" + respTxt);
        } catch (Exception e) {
            throw new RuntimeException("调用天远接口失败." + e.getMessage(), e);
        } finally {
        }

        return parseResp(respTxt, respFormat);
    }
    public HttpEntity createJsonEntity(String jsonString, HttpEntityEnclosingRequestBase method) {
        method.addHeader("Content-Type", "application/json");
        if (StringUtils.isEmpty(jsonString)) jsonString = "{}";
        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        method.setEntity(entity);
        return entity;
    }
    public String executeHttp(HttpUriRequest method, boolean checkStatusCode) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        // 执行请求
        HttpResponse response = client.execute(method);
        int code = response.getStatusLine().getStatusCode();
        if (checkStatusCode && code != 200) {
            logger.error("executeHttp response code is not 200 but " + code + ",url:" + method.getURI() + ",method:" + method.getMethod());
            throw new RuntimeException("请求接口反馈状态码不是200而是" + code);
        }

        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String respString = EntityUtils.toString(entity, "utf-8");
            return respString;
        }
        return null;
    }
    public HttpEntity createJsonEntity(Map<String, String> params, HttpEntityEnclosingRequestBase method) {
        JSONObject jo = new JSONObject();
        params.keySet().stream().forEach(key -> {
            jo.put(key, params.get(key));
        });
        return createJsonEntity(jo.toJSONString(), method);
    }
    protected JSONObject parseResp(String respTxt, String respFormat) {
        if (StringUtils.isEmpty(respTxt)) {
            return null;
        }

        if ("json".equals(respFormat)) {
            JSONObject jo = null;
            try {
                jo = JSON.parseObject(respTxt);
            } catch (Exception e) {
//                logger.error("解析天远接口反馈的json数据异常", e);
                return null;
            }

            String error = jo.getString("error");
            if (!StringUtils.isEmpty(error)) {
//                throw new TianYuanException("调用天远接口失败.url=" + url + ".error=" + error);
            }
            return jo;
        } else if ("xml".equals(respFormat)) {
            //
            String startKeyString = "<string xmlns=\"http://tempuri.org/\">";
            int pos1 = respTxt.indexOf(startKeyString);
            int pos2 = respTxt.lastIndexOf("</string>");
            if (pos1 != -1 && pos2 != -1) {
                String jsonTxt = respTxt.substring(pos1 + startKeyString.length(), pos2).trim();
                JSONObject jo = null;
                try {
                    jo = JSON.parseObject(jsonTxt);
                } catch (Exception e) {
//                    logger.error("解析天远接口反馈xml内容中的json时异常", e);
                    return null;
                }
                return jo;
            } else {
//                throw new TianYuanException("调用天远接口失败.url=" + url + ".反馈字符串格式无法解析");
            }
        }
        return null;
    }
}