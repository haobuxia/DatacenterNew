package com.tianyi.datacenter.inspect.service;

import com.tianyi.datacenter.common.vo.ResponseVo;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author liulele ${Date}
 * @version 0.1
 **/

public interface DistributionService {

    ResponseVo saveAll(List<Map<String,Object>> param);

    ResponseVo realtime(Map<String,Object> map);

    ResponseVo importFile(MultipartFile file);

    void exportModel(HttpServletResponse response);
}
