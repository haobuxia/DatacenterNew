package com.tianyi.datacenter.gateway.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tianyi.datacenter.gateway.config.TianYiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by tianxujin on 2019/5/8 16:31
 * token校验全局过滤器
 * @Version V1.0
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "user-token";
    private static final String AUTHORIZE_UID = "userId";
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizeFilter.class);

    @Autowired
    RestTemplate template;

    @Autowired
    TianYiConfig tianYiConfig;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${auth.skip.urls}")
    private String[] skipAuthUrls;

    @Value("${jwt.blacklist.key.format}")
    private String jwtBlacklistKeyFormat;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse originalResponse = exchange.getResponse();

        String url = request.getURI().getPath();
        //跳过不需要验证的路径
        if(Arrays.asList(skipAuthUrls).contains(url) || ("-1".equals(tianYiConfig.getAutoertype()))){
            return chain.filter(exchange);
        }
        //从请求头中取出token
        String token = request.getHeaders().getFirst("Authorization");
        if("1".equals(tianYiConfig.getAutoertype())) {
            if (authorTokenLease(exchange, originalResponse, token)) {// 租赁token校验提取
                return originalResponse.setComplete();
            }
        } else {
            if (authorToken(exchange, originalResponse, token)) {// 小松工厂token校验提取
                return originalResponse.setComplete();
            }
        }

        /*String path = request.getURI().getPath();
        if(path.indexOf("author/login") != -1){
            return chain.filter(exchange);
        }
        HttpCookie cookie = request.getCookies().getFirst(AUTHORIZE_TOKEN);
        HttpCookie userCookie = request.getCookies().getFirst(AUTHORIZE_UID);
        if(cookie==null){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        String token = cookie.getValue();

        String url = tianYiConfig.getTianYiIntesrvUrl()+"/tianyiuser/islogin";
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("token", token);
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 添加cookie
        headers.set("Cookie", request.getHeaders().getFirst("Cookie"));
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<Boolean> responseEntity = template.postForEntity(url, requestEntity, Boolean.class);
        Boolean isLogin = responseEntity.getBody();
        if (!isLogin) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 设置cookie时长
        ResponseCookie ck = ResponseCookie.from(AUTHORIZE_TOKEN, responseEntity.getHeaders().getFirst(AUTHORIZE_TOKEN))
                .maxAge(60*60)
                .path("/")
                .build();
        response.addCookie(ck);*/
        return chain.filter(exchange);
    }

    private boolean authorTokenLease(ServerWebExchange exchange, ServerHttpResponse originalResponse, String token) {
        //未携带token或token在黑名单内
        if (token == null ||
                token.isEmpty()) {
            originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return true;
        } else {
            if(!token.startsWith("Bearer ")) {
                token = "Bearer "+token;
            }
        }
        String url = tianYiConfig.getLeaseauthorUrl()+"/v1/userinfo";
        HttpHeaders headers = new HttpHeaders();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("token", token);
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", token);
        //将请求头部和参数合成一个请求
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<Map> responseEntity = template.exchange(url, HttpMethod.GET, entity, Map.class);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        if(!"200".equals(responseEntity.getStatusCode().toString())) {
            return true;
        }
//        Map map = responseEntity.getBody();
        return false;
    }

    private boolean authorToken(ServerWebExchange exchange, ServerHttpResponse originalResponse, String token) {
        //未携带token或token在黑名单内
        if (token == null ||
                token.isEmpty() ||
                isBlackToken(token)) {
            originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return true;
        }
        //取出token包含的身份
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String userId = verifyJWT(token);
        if(userId.isEmpty()){
            originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
            return true;
        }
        //将现在的request，添加当前身份
        ServerHttpRequest mutableReq = exchange.getRequest().mutate().header("Authorization-UserId", userId).build();
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * JWT验证
     * @param token
     * @return userId
     */
    private String verifyJWT(String token){
        String userId = "";
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("MING")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            userId = jwt.getClaim("userId").asString();
        } catch (JWTVerificationException e){
            LOGGER.error(e.getMessage(), e);
            return "";
        }
        return userId;
    }

    /**
     * 判断token是否在黑名单内
     * @param token
     * @return
     */
    private boolean isBlackToken(String token){
        assert token != null;
        return stringRedisTemplate.hasKey(String.format(jwtBlacklistKeyFormat, token));
    }
}