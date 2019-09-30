package com.tianyi.datacenter.inspect.service;


import com.tianyi.datacenter.common.vo.ResponseVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 数据对象服务接口
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface CheckItemService {


    ResponseVo saveCheckType(Map<String, Object> param);

    ResponseVo saveCheckItem(Map<String, Object> param);


    ResponseVo saveAll(List<Map<String,Object>> param);

    ResponseVo detail(Map<String,Object> param);

    ResponseVo updateCheckType(Map<String,Object> param);

    ResponseVo updateCheckItem(Map<String,Object> param);

    ResponseVo searchCheckItem(Map<String,Object> param);

    void exportmodel(HttpServletResponse response);

    ResponseVo importExcel(MultipartFile file);

    ResponseVo reltimestatus(Map<String,Object> map);
}
