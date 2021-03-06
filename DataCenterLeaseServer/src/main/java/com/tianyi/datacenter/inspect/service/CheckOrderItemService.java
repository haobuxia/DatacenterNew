package com.tianyi.datacenter.inspect.service;


import com.tianyi.datacenter.common.vo.ResponseVo;

import java.util.List;
import java.util.Map;

/**
 * 数据对象服务接口
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface CheckOrderItemService  {


    ResponseVo search(Map<String,Object> param);

    ResponseVo resultSearch(Map<String,Object> param);

    ResponseVo save(List<Map<String, Object>> param);

    ResponseVo searchResult(Map<String, Object> param);

    ResponseVo imgresult(Map<String,Object> param);

    ResponseVo markresult(Map<String,String> param);

    ResponseVo getBarrage(Map<String, String> param);

    ResponseVo getVoiceRemark(Map<String, String> param);

    ResponseVo updateVoiceRemark(Map<String, Object> param);

    ResponseVo getAnalysisData(Map<String, String> param);

    ResponseVo getItemTagresult(String videoId);
}
