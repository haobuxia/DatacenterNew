import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/
public class demo {
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
        try {
            uploadFile("http://103.20.114.238:8866/upload?app=f8377e2911f4443aa3cbfd43c1f56e0c&token=1f147baacc3e4d3abb50561782c5451a "
            ,"D:\\abc\\1.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("hh");
    }


    public static void uploadFile(String uri, String fileName) throws
            ClientProtocolException, IOException {
        HttpClient client = HttpClients.createDefault();
        File file = new File(fileName);
        MultipartEntityBuilder multipartEntityBuilder =
                MultipartEntityBuilder
                        .create();
        HttpPost httpPost = new HttpPost(uri);
        UUID uid = UUID.randomUUID();
        String boundary = uid.toString().replaceAll("-", "");
        boundary = "------------"+boundary;
        System.out.println(boundary);
        multipartEntityBuilder.setBoundary(boundary);
        multipartEntityBuilder.addBinaryBody("multipart/form-data", file);
        httpPost.setEntity(multipartEntityBuilder.build());
        HttpResponse httpResponse = client.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        String content = EntityUtils.toString(httpEntity);
        System.out.println(content+"111111");
    }
}
