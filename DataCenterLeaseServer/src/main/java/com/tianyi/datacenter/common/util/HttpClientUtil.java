package com.tianyi.datacenter.common.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
public class HttpClientUtil {
    public static String getHttpPostForm(String url,Map<String, String> param, Map<String, String> header) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
//            URIBuilder uriBuilder = new URIBuilder().setScheme(scheme).setPort(port).setHost(host).setPath(path);
            System.out.println(uriBuilder);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            ContentType contentType = ContentType.create("text/plain", Charset.forName("UTF-8"));
            if (param != null) {
                Iterator<String> iterator = param.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
//					multipartEntityBuilder.addTextBody(key, param.get(key));
                    multipartEntityBuilder.addTextBody(key, param.get(key), contentType);
//					pairList.add(new BasicNameValuePair(key, param.get(key)));
                }
            }
            URI uri = uriBuilder.build();
            HttpPost httpPost = new HttpPost(uri);
            if (header != null) {
                Iterator<String> iterator = header.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    httpPost.setHeader(key, header.get(key));
                }
            }
            httpPost.setEntity(multipartEntityBuilder.build());
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }


}
