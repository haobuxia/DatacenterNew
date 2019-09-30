package com.tianyi.code.server.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * httpClient请求工具类
 * CreateTime:2018.11.17
 */
public class HttpClientUtil {
    /**
     * 发送 get请求
     * @param requestUrl
     * @return
     */
    public static String sendGet(String requestUrl) {
        String res="";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(requestUrl);
            System.out.println("executing request " + httpget.getURI());
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                // 打印响应状态
                System.out.println(response.getStatusLine());
                res=EntityUtils.toString(entity,"UTF-8");
            } finally {
                response.close();
            }
        } catch (Exception e) {
           throw new ServiceException("调用查询结果接口异常",e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("调用接口返回:" +res);
        return res;
    }
    /**
     * 发送httpclient post请求
     * @param url
     * @param param
     * @return
     */
    public static String sendPost(String url, String param) {
        final String CONTENT_TYPE_TEXT_JSON = "text/json";
        DefaultHttpClient client = new DefaultHttpClient(new PoolingClientConnectionManager());
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");
            httpPost.setHeader("Accept", "application/json");
            if (param!=null&&!"".equals(param)) {
                httpPost.setEntity(new StringEntity(param, Charset.forName("UTF-8")));
            }
            CloseableHttpResponse response2 = null;
            response2 = client.execute(httpPost);
            HttpEntity entity2 = null;
            if (response2 != null) {
                entity2 = response2.getEntity();
            }
            result = EntityUtils.toString(entity2, "UTF-8");
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常:" + e.getMessage());
        }
        return result;
    }



    public static String postFileMultiPart(String url,String filePath) throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpPost httppost = new HttpPost(url);
            //setConnectTimeout：设置连接超时时间，单位毫秒。setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
            //都设置为200秒
            RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(200000).setConnectionRequestTimeout(200000).setSocketTimeout(200000).build();
            httppost.setConfig(defaultRequestConfig);
            System.out.println("executing request " + httppost.getURI());
            //可提出来增加多个参数
            Map<String,ContentBody> reqParam = new HashMap<>();
            reqParam.put("file", new FileBody(new File(filePath)));

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for(Map.Entry<String,ContentBody> param : reqParam.entrySet()){
                multipartEntityBuilder.addPart(param.getKey(), param.getValue());
            }
            HttpEntity reqEntity = multipartEntityBuilder.build();
            httppost.setEntity(reqEntity);
            // 执行post请求.
            CloseableHttpResponse response = httpclient.execute(httppost);
            System.out.println("获取到结果");
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity,Charset.forName("UTF-8"));
                }
            } finally {
                response.close();
            }
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void main(String[] args) throws IOException {

        //测试服务接口
       // String requestUrl="http://166.111.80.136:31673/predict";
        //String filePath="D:\\data\\content_root\\test1\\2018\\11\\27\\10\\50\\video2img.zip";
       // String httpRes = HttpClientUtil.postFileMultiPart(requestUrl,filePath);
       // System.out.println(httpRes);

        //测试服务接口
        String requestUrl="http://smart.tygps.com:13332/helmetmedia/video/list?orderNo=123";
       // String orderNo="123";
        String httpRes = HttpClientUtil.sendPost(requestUrl,null);

     //   System.out.println(httpRes);
    }
}
