package com.tianyi.datacenter.common.aop;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tianyi.datacenter.access.service.DataAccessService;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 以controller为关注点做切面
 *面向切面编程实现日志打印
 * @author wenxinyan
 * @version 0.1
 */
@Aspect
public class ControllerAspect {

    private Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    private Gson gson = new Gson();
    @Autowired
    private DataAccessService dataAccessService;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)") //execution(public * com.tianyi.datacenter.access.controller.DataAccessController.*(..))
    public void controllerAspectFunction(){}

    @Before("controllerAspectFunction()")
    public void methodBefore(JoinPoint joinPoint){
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        logger.info("==========请求内容==========");
        logger.info("请求地址:" + request.getRequestURL().toString());
        logger.info("请求方式:" + request.getMethod());
        logger.info("请求类方法:" + joinPoint.getSignature());
        logger.info("请求类方法参数:" + Arrays.toString(joinPoint.getArgs()));
        logger.info("==========请求内容==========");
        Object[] o = joinPoint.getArgs();
        if(o[0] instanceof JSONObject) {// 设置用户的数据范围权限
            JSONObject jsonObjectParam = (JSONObject) o[0];
            if(jsonObjectParam.get("menuId") != null && jsonObjectParam.get("userId") != null) {
                // 查询用户菜单数据范围
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dataObjectId",19);
                Map<String, String> conditionMap = new HashMap<>();
                conditionMap.put("userId", jsonObjectParam.get("userId").toString());
                conditionMap.put("menuId", jsonObjectParam.get("menuId").toString());
                jsonObject.put("condition",conditionMap);
                Map<String, Integer> pageMap = new HashMap<>();
                pageMap.put("page", 0);
                pageMap.put("pageSize", 0);//全是0，代表不分页
                jsonObject.put("pageInfo",pageMap);
                jsonObject.put("condition",conditionMap);
                try {
                    ResponseVo deptResponseVo = dataAccessService.queryData(jsonObject);
                    if(deptResponseVo.isSuccess()) {
                        List<Map<String, Object>> deptList = (List<Map<String, Object>>) deptResponseVo.getData().get("rtnData");
                        jsonObjectParam.put("departRange", deptList);
                        request.setAttribute("body",jsonObjectParam);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @AfterReturning(returning = "o", pointcut = "controllerAspectFunction()")
    public void methodAfterReturning(Object o){
        logger.info("----------返回内容----------");
        logger.info("Response内容:" + gson.toJson(o));
        logger.info("----------返回内容----------");
        if(o instanceof ResponseVo){
            ResponseVo vo = (ResponseVo)o;
            if(vo.getMessage()!=null && vo.getMessage().indexOf("Duplicate")!= -1) {
                vo.setMessage("数据主键重复");
            }
        }
    }

}