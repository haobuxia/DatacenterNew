package com.tianyi.datacenter.common.util;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.http.HttpRequest;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianxujin on 2019/7/31 9:50
 */
@Component
public class TydeviceLocationUtil {
    public Map<String, Object> getDeviceLocationInfo(Map<String, Object> deviceMap) {
        String token = getToken();
        String deviceRequestXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<data>"
                + "<machine_no>"+deviceMap.get("deviceID")+"</machine_no>"
                + "<machine_type>"+deviceMap.get("deviceModel")+"</machine_type>"
                + "<token>"+token.replace("Bearer ", "")+"</token>"
//+ "<machine_no>DBB3703</machine_no>"
//+ "<machine_type>PC200-7</machine_type>"
//						+ "<token>Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IkE0MDc1MDAwRUNFMTVDRDZBMjY3N0M0QUJDNUMxNTMwRDYzRDRCREYiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJwQWRRQU96aFhOYWlaM3hLdkZ3Vk1OWTlTOTgifQ.eyJuYmYiOjE1NTc3OTU3ODQsImV4cCI6MTU2MDM4Nzc4NCwiaXNzIjoiaHR0cHM6Ly93d3cudHltaWNzLmNvbS5jbi9vYXV0aC9hcGkiLCJhdWQiOlsiaHR0cHM6Ly93d3cudHltaWNzLmNvbS5jbi9vYXV0aC9hcGkvcmVzb3VyY2VzIiwiYXBpIiwiY3NyYXBpIiwicmVudGFsYXBpIl0sImNsaWVudF9pZCI6InJlbnRhbHdlYiIsInN1YiI6IjEwMDAwMDMyMzMiLCJhdXRoX3RpbWUiOjE1NTc3OTU3ODQsImlkcCI6ImxvY2FsIiwiYWNudF9pZCI6IjEwMDAwMDMyMzMiLCJncm91cF9pZCI6IiIsInN5c2lkIjoiMSIsIm9wcnRfbmFtZSI6InR5a2pzeXMiLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwiYXBpIiwiY3NyYXBpIiwicmVudGFsYXBpIiwib2ZmbGluZV9hY2Nlc3MiXSwiYW1yIjpbInBhc3N3b3JkIl19.fbg-CQZDWDiV_cj6JGHHFvw-ABYGSYVTWvkaM-kiyZZnes8-BquzlK6Zr7kJLQ1qHuicaY8gA6qlvapmZBcaMsgwgleNM-9M9cGu6YeGEixenyGX_jsGYHqOAIMm2p_CqFDQRlOPwaa0HPvd__9CyUwW2AhTfCEsALABAnwpnMauupRMQ3yc54OzSXp9evkmuvldVL9TgmwU_-t-LpUTJW0WCPyu7xUI0ofrPLLUXbI_JonVqLvL0BmzjgEfUUUhxCCA5UPu536MrKhD-xuCTjwAIzsnRvlh5Dqk8qIIMFT_EK_h08-GG2EINQAz4BnvO00zYcPJeGqhZW1edkIBGg</token>"
                + "</data>";
        //同步车辆位置等数据
        String deviceResponseXML = HttpRequest.post("https://www.tymics.com.cn/tyrental/komatsu/komtraxdataoauth/carservice/getMachineInfo_Batch")
                .body(deviceRequestXML)
                .execute().body();
        Document locationDocument = XmlUtil.parseXml(deviceResponseXML);
        Object status = XmlUtil.getByXPath("//root/status_code", locationDocument, XPathConstants.STRING);
        Map<String, Object> result = new HashMap<>();
        if(status != null && "0".equals(status)) {
            result.put("lat", -1);
            result.put("lon", -1);
        } else {
            Object lat = XmlUtil.getByXPath("//root/machine/lat", locationDocument, XPathConstants.NUMBER);
            System.out.println(lat);
            Object lon = XmlUtil.getByXPath("//root/machine/lon", locationDocument, XPathConstants.NUMBER);
            System.out.println(lon);
            result.put("lat", lat);
            result.put("lon", lon);
        }
        return result;
    }

    private String getToken() {
        return "eyJhbGciOiJSUzI1NiIsImtpZCI6IkE0MDc1MDAwRUNFMTVDRDZBMjY3N0M0QUJDNUMxNTMwRDYzRDRCREYiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJwQWRRQU96aFhOYWlaM3hLdkZ3Vk1OWTlTOTgifQ.eyJuYmYiOjE1NjI5Nzg1ODgsImV4cCI6MTU2NTU3MDU4OCwiaXNzIjoiaHR0cHM6Ly93d3cudHltaWNzLmNvbS5jbi9vYXV0aC9hcGkiLCJhdWQiOlsiaHR0cHM6Ly93d3cudHltaWNzLmNvbS5jbi9vYXV0aC9hcGkvcmVzb3VyY2VzIiwiYXBpIiwiY3NyYXBpIiwicmVudGFsYXBpIl0sImNsaWVudF9pZCI6InJlbnRhbHdlYiIsInN1YiI6IjEwMDAwMDMyMzMiLCJhdXRoX3RpbWUiOjE1NjI5Nzg1ODgsImlkcCI6ImxvY2FsIiwiYWNudF9pZCI6IjEwMDAwMDMyMzMiLCJncm91cF9pZCI6IiIsInN5c2lkIjoiMSIsIm9wcnRfbmFtZSI6InR5a2pzeXMiLCJzY29wZSI6WyJvcGVuaWQiLCJwcm9maWxlIiwiYXBpIiwiY3NyYXBpIiwicmVudGFsYXBpIiwib2ZmbGluZV9hY2Nlc3MiXSwiYW1yIjpbInBhc3N3b3JkIl19.ZEHhg1pE3XcLIp4f2CXJ6lkUgqtmTNC_KMvEMiRpAqTcjLjcnlvh3HCPnwSHJUE0tvQSSW_p4IbrW2ea0FOJVvE1WTOZ4KY95Tzp1ftIbauOy6W2Ux76hp9CL9SycpRibeJqAGY77yO28VxSgeIX4WBSLbt0tcVwS2g5xTM_C8OVgJ6XnZNAWtXcSlCknF7m2IG6QJbLJnDQcs31uNt8Dl119HVNxhjSew3UlA4AV6cuK1TfH5xMXraqcj1JPFKPBdgfXTs4bOj67P6WO3dwMAclfJRixaea4r4x_cyMxM_27lbOiVEnAspMqsuCIqYj0OUjPt0GEo65wapYr5Bz7g";
    }

    public static void main(String[] args) {
//        Map<String, Object> deviceMap = new HashMap<>();
//        deviceMap.put("deviceID","DBBJ0169");
//        deviceMap.put("deviceModel","PC240LC-8");
//        try {
//            syncDeviceLocationInfo(deviceMap);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
