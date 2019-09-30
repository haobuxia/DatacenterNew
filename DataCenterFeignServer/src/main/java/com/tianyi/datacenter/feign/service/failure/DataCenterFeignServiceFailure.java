package com.tianyi.datacenter.feign.service.failure;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.feign.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import org.springframework.stereotype.Component;

/**
 * 负载均衡错误返回
 *
 * @author wenxinyan
 * @version 0.1
 */
@Component
public class DataCenterFeignServiceFailure implements DataCenterFeignService {

    @Override
    public String hello(String name) {
        return "hello world service is not available !";
    }

    @Override
    public ResponseVo retrieve(JSONObject jsonObject) {
        return ResponseVo.fail("查询失败");
    }

    @Override
    public ResponseVo retrieveId(JSONObject jsonObject) {
        return ResponseVo.fail("查询失败");
    }

    @Override
    public ResponseVo checkReference(JSONObject jsonObject) {
        return ResponseVo.fail("校验失败");
    }

    @Override
    public ResponseVo add(JSONObject jsonObject) {
        return ResponseVo.fail("新增失败");
    }

    @Override
    public ResponseVo delete(JSONObject jsonObject) {
        return ResponseVo.fail("删除失败");
    }

    @Override
    public ResponseVo update(JSONObject jsonObject) {
            return ResponseVo.fail("更新失败");
    }
}
