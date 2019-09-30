package com.tianyi.datacenter.author.controller;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tianyi.datacenter.common.util.JsonTreeUtil;
import com.tianyi.datacenter.common.vo.NetUserListVo;
import com.tianyi.datacenter.config.TianYiConfig;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.common.util.DSParamDsBuilder;
import com.tianyi.datacenter.feign.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by tianxujin on 2019/5/13 10:56
 */
@RestController
@RequestMapping("author")
public class TianyiKejiAuthorController {

    Logger logger = LoggerFactory.getLogger(TianyiKejiAuthorController.class);
    @Autowired
    RestTemplate template;
    @Autowired
    TianYiConfig tianYiConfig;
    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${token.expire.time}")
    private long tokenExpireTime;

    @Value("${refresh.token.expire.time}")
    private long refreshTokenExpireTime;

    @Value("${jwt.refresh.token.key.format}")
    private String jwtRefreshTokenKeyFormat;

    @Value("${jwt.blacklist.key.format}")
    private String jwtBlacklistKeyFormat;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;
    private static final String AUTHORIZE_TOKEN = "user-token";
    private static final String AUTHORIZE_UID = "userId";
    private static final String path = "http://192.168.30.61/trusted/";
    private static final String params ="username=sa&target_site=CTY&client_ip=192.168.18.243";

    @RequestMapping(value = "sayhello", method = RequestMethod.GET)
    public String sayHello(@RequestParam String name) {
        return dataCenterFeignService.hello(name);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseVo login(@RequestParam String username, @RequestParam String password, HttpServletRequest request,
                            HttpServletResponse response) {
        ResponseVo responseVo = null;
        DSParamBuilder userParamBuilder = new DSParamBuilder(3);
        userParamBuilder.buildCondition("account", "equals", username)
                .buildCondition("password", "equals", password);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.retrieve(userParamBuilder
                .build());
        String userId = "";
        if (resultVo.isSuccess()) {
            List<Map<String, Object>> userList = (List<Map<String, Object>>) resultVo.getData().get("rtnData");
            if (userList.size() > 0) { // 登录成功
                userId = userList.get(0).get("uid").toString();
                //生成JWT
                String token = buildJWT(userId);
                //生成refreshToken
                String refreshToken = UUID.randomUUID().toString().replaceAll("-", "");
                //保存refreshToken至redis，使用hash结构保存使用中的token以及用户标识
                String refreshTokenKey = String.format(jwtRefreshTokenKeyFormat, refreshToken);
                stringRedisTemplate.opsForHash().put(refreshTokenKey,
                        "token", token);
                stringRedisTemplate.opsForHash().put(refreshTokenKey,
                        "userId", userId);
                //refreshToken设置过期时间
                stringRedisTemplate.expire(refreshTokenKey,
                        refreshTokenExpireTime, TimeUnit.MILLISECONDS);
                //返回结果
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put(AUTHORIZE_UID, userId);
                dataMap.put(AUTHORIZE_TOKEN, token);
//                dataMap.put("token", token);
                dataMap.put("refreshToken", refreshToken);
                responseVo = ResponseVo.success(dataMap);
            } else {
                return ResponseVo.fail("用户名或密码错误！");
            }
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
        /*try {
            String url = tianYiConfig.getTianYiIntesrvUrl()+"/common/login";

            HttpHeaders headers = new HttpHeaders();
            //定义请求体
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("username", username);
            params.add("password", password);
            params.add("loginType", "1");
            // 以表单的方式提交
            //定义请求头
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //将请求头部和参数合成一个请求
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            //参数请求地址,请求封装的实体,响应的类型   向头盔发送请求并获取响应信息
            ResponseEntity<AppAccountVo> response1 = template.postForEntity(url, requestEntity, AppAccountVo.class);
            //获取响应的响应体
            AppAccountVo vo = response1.getBody();
            if("200".equals(vo.getCode())) {
                List<String> cookies = response1.getHeaders().get("Set-Cookie");
                for(String cookie : cookies) {
                    response.addHeader("Set-cookie", cookie);
                }
                Map<String,Object> map = new HashMap<>();
                map.put(AUTHORIZE_UID, response1.getHeaders().getFirst(AUTHORIZE_UID));
                map.put(AUTHORIZE_TOKEN, response1.getHeaders().getFirst(AUTHORIZE_TOKEN));
                responseVo = ResponseVo.success(map);
            }
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }*/
        return responseVo;
    }

    @RequestMapping(value = "authormenus", method = RequestMethod.POST)
    public ResponseVo authorMenus(@RequestParam String userId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("dataObjectId", 20);
        Map<String, String> conditionMap = new HashMap<>();
        conditionMap.put("userId", userId);
        jsonObject.put("condition", conditionMap);
        Map<String, Integer> pageMap = new HashMap<>();
        pageMap.put("page", 0);
        pageMap.put("pageSize", 0);//全是0，代表不分页
        jsonObject.put("pageInfo", pageMap);
        ResponseVo responseVo = dataCenterFeignService.retrieve(jsonObject);
        if (responseVo.isSuccess()) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) responseVo.getData().get("rtnData");
            Map<String, Object> root = JsonTreeUtil.findRoot(list, "mid", "fatherId");
            List<Map<String, Object>> result = JsonTreeUtil.buildByRecursive(list, root.get("fatherId").toString(),
                    "mid", "fatherId");
            Map<String, List<Map<String, Object>>> map = new HashMap<>();
            map.put("rtnData", result);
            return ResponseVo.success(map);
        }
        return ResponseVo.success();
    }

    @RequestMapping(value = "companys", method = RequestMethod.POST)
    public ResponseVo companys(@RequestParam String userId, @RequestParam String companyNames) {
        if (StringUtils.isEmpty(companyNames)) {
            // 根据userId查询82数据集
            DSParamDsBuilder dsParamDsBuilder = new DSParamDsBuilder(82);
            dsParamDsBuilder.buildCondition("userId", userId);
            ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamDsBuilder.build());
            if (retrieve.isSuccess() && retrieve.getMessage() == null) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
                String cid = (String) list.get(0).get("cid");
                // 判断结果集中cid是否为1
                if ("1".equals(cid)) {
                    DSParamBuilder dsParamBuilder = new DSParamBuilder(1);
//                    dsParamBuilder.buildCondition("cid", "equals", cid);
                    // 是1则查询公司表(1)，然后返回结果
                    ResponseVo retrieveCompany = dataCenterFeignService.retrieve(dsParamBuilder.build());
                    return retrieveCompany;
                } else {
                    // 不是1则直接返回结果【rtn】
                    return retrieve;
                }
            } else {
                return retrieve;
            }
        }
        // 查询companyNames的公司列表并返回【田一科技,北部办】
        String[] splitCompanyName = companyNames.split(",");
        List companyList = new ArrayList();
        for (String CompanyName : splitCompanyName) {
            DSParamBuilder dsParamBuilder = new DSParamBuilder(1);
            dsParamBuilder.buildCondition("companyName", "equals", CompanyName);
            ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilder.build());
            if (retrieve.isSuccess()&&retrieve.getMessage()==null){
                List<Map<String, Object>> list = (List<Map<String, Object>>) retrieve.getData().get("rtnData");
                companyList.add(list.get(0));
            }
        }
        Map rtnData = new HashMap();
        rtnData.put("rtnData",companyList);
        return ResponseVo.success(rtnData);
    }

    @RequestMapping(value = "tableauTicket", method = RequestMethod.POST)
    public ResponseVo tableauTicket() {
        String url = tianYiConfig.getTianYiTableauUrl() + "/trusted/";
        HttpHeaders headers = new HttpHeaders();
        //定义请求体
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", tianYiConfig.getTianYiTableauUsername());
        params.add("target_site", tianYiConfig.getTianYiTableauTargetSite());
        params.add("client_ip", tianYiConfig.getTianYiTableauClientIp());
        // 以表单的方式提交
        //定义请求头
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //参数请求地址,请求封装的实体,响应的类型   向头盔发送请求并获取响应信息
        ResponseEntity<String> response1 = template.postForEntity(url, requestEntity, String.class);
        //获取响应的响应体
        System.out.println("tableau返回状态：" + response1.getStatusCode());
        System.out.println("tableau返回BODY：" + response1.getBody());
        String ticket = response1.getBody();
        Map rtnData = new HashMap();
        rtnData.put("ticket", ticket);
        return ResponseVo.success(rtnData);
    }

    @RequestMapping(value = "getYunToken", method = RequestMethod.POST)
    public ResponseVo getYunToken(@RequestParam String loginUserAccount, @RequestParam String loginUserPwd) {
        ResponseVo responseVo = null;
        DSParamBuilder userParamBuilder = new DSParamBuilder(3);
        userParamBuilder.buildCondition("account", "equals", loginUserAccount)
                .buildCondition("password", "equals", loginUserPwd);
        com.tianyi.datacenter.feign.common.vo.ResponseVo resultVo = dataCenterFeignService.retrieve(userParamBuilder
                .build());
        String userName = "";
        if (resultVo.isSuccess()) {
            List<Map<String, Object>> userList = (List<Map<String, Object>>) resultVo.getData().get("rtnData");
            if (userList.size() > 0) {
                userName = userList.get(0).get("name").toString();
            } else {
                return ResponseVo.fail("用户名或密码错误！");
            }
        } else {
            return ResponseVo.fail(resultVo.getMessage());
        }
        try {
            String url = tianYiConfig.getTianYiIntesrvUrl() + "/helmetmedia/get/netuser";
            HttpHeaders headers = new HttpHeaders();
            //定义请求体
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("tyuserNo", loginUserAccount);
            // 以表单的方式提交
            //定义请求头
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //将请求头部和参数合成一个请求
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            //参数请求地址,请求封装的实体,响应的类型   向头盔发送请求并获取响应信息
            ResponseEntity<ResponseVo> response1 = template.postForEntity(url, requestEntity, ResponseVo.class);
            //获取响应的响应体
            responseVo = response1.getBody();
            responseVo.getData().put("userName", userName);
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    @RequestMapping(value = "getOnlineUsers", method = RequestMethod.POST)
    public NetUserListVo getOnlineUsers() {
        NetUserListVo responseVo = null;
        try {
            String url = tianYiConfig.getTianYiIntesrvUrl() + "/helmetmedia/get/online/netuser";
            HttpHeaders headers = new HttpHeaders();
            //定义请求体
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            // 以表单的方式提交
            //定义请求头
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //将请求头部和参数合成一个请求
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
            //参数请求地址,请求封装的实体,响应的类型   向头盔发送请求并获取响应信息
            ResponseEntity<NetUserListVo> response1 = template.postForEntity(url, requestEntity, NetUserListVo.class);
            //获取响应的响应体
            responseVo = response1.getBody();
        } catch (Exception e) {
            responseVo = NetUserListVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    private String buildJWT(String userId) {
        //生成jwt
        Date now = new Date();
        Algorithm algo = Algorithm.HMAC256(secretKey);
        String token = JWT.create()
                .withIssuer("MING")
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + tokenExpireTime))
                .withClaim("userId", userId)//保存身份标识
                .sign(algo);
        return token;
    }

    /**
     * 刷新JWT
     *
     * @param refreshToken
     * @return
     */
    @RequestMapping(value = "/token/refresh", method = RequestMethod.POST)
    public Map<String, Object> refreshToken(@RequestParam String refreshToken) {
        Map<String, Object> resultMap = new HashMap<>();
        String refreshTokenKey = String.format(jwtRefreshTokenKeyFormat, refreshToken);
        String userId = (String) stringRedisTemplate.opsForHash().get(refreshTokenKey,
                "userId");
        if (StringUtils.isBlank(userId)) {
            resultMap.put("code", "10001");
            resultMap.put("msg", "refreshToken过期");
            return resultMap;
        }
        String newToken = buildJWT(userId);
        //替换当前token，并将旧token添加到黑名单
        String oldToken = (String) stringRedisTemplate.opsForHash().get(refreshTokenKey,
                "token");
        stringRedisTemplate.opsForHash().put(refreshTokenKey, "token", newToken);
        stringRedisTemplate.opsForValue().set(String.format(jwtBlacklistKeyFormat, oldToken), "",
                tokenExpireTime, TimeUnit.MILLISECONDS);
        resultMap.put("code", "10000");
        resultMap.put("data", newToken);
        return resultMap;
    }
    @RequestMapping(value = "/dataview", method = RequestMethod.POST)
    public static String post() throws Exception {
        HttpURLConnection httpConn = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            URL url = new URL(path);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // 发送post请求参数
            out = new PrintWriter(httpConn.getOutputStream());
            out.println(params);
            out.flush(); // 读取响应
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuffer content = new StringBuffer();
                String tempStr = "";
                in = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                while ((tempStr = in.readLine()) != null) {
                    content.append(tempStr);
                }
                System.out.println( content.toString());
                System.out.println("CTY_1.7  content>"+content.toString());
                return content.toString();
            } else {
                throw new Exception("请求出现了问题!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
            httpConn.disconnect();
        }
        return null;
    }

}