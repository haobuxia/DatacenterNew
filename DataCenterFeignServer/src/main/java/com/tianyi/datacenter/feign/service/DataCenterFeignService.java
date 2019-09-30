package com.tianyi.datacenter.feign.service;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.feign.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.service.failure.DataCenterFeignServiceFailure;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 负载均衡服务
 *
 * @author wenxinyan
 * @version 0.1
 */
@FeignClient(value = "SERVICE-DATACENTER", fallback = DataCenterFeignServiceFailure.class)
@Service
public interface DataCenterFeignService {

    /**
     * get方式访问
     * @param name
     * @return
     */
    @RequestMapping(value="/data/hello", method = RequestMethod.GET)
    String hello(@RequestParam("name") String name);

    /**
     * 查询数据中心实体/结果集信息
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/data/retrieve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVo retrieve(@RequestBody JSONObject jsonObject);

    /**
     * 获取对象唯一编码
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/data/retrieveid", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVo retrieveId(@RequestBody JSONObject jsonObject);

    /**
     * 查询数据A是否被引用并返回引用A的对象数据
     * dataObjectId是A对象objectId，dataId是A对象数据ID
     * @author tianxujin
     */
    @RequestMapping(value="/data/checkreference", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVo checkReference(@RequestBody JSONObject jsonObject);

    /**
     * 新增数据中心实体
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/data/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVo add(@RequestBody JSONObject jsonObject);

    /**
     * 删除数据中心实体
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/data/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVo delete(@RequestBody JSONObject jsonObject);

    /**
     * 更新数据中心实体
     * @param jsonObject
     * @return
     */
    @RequestMapping(value="/data/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseVo update(@RequestBody JSONObject jsonObject);
}
