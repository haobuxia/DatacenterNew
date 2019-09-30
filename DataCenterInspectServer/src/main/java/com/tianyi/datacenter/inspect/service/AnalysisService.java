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
public interface AnalysisService  {


    List<Map<String, Object>> queryByDayinit(Map<String,String> param);

//    List<Map<String,Object>> queryByDay(Map<String,String> param);

    List<Map<String,Object>> getinitByMonth(Map<String,String> param);

    List<Map<String,Object>> getinitByYear(Map<String,String> param);

    List<Map<String,Object>> selectByDate(Map<String,String> param);

   /* List<Map<String,Object>> getCheckersByDay(Map<String,String> param);

    List<Map<String,Object>> getCheckersByMonth(Map<String,String> param);

    List<Map<String,Object>> getCheckersByYear(Map<String,String> param);*/

    List<Map<String,Object>> getProblemByDay(Map<String,String> param);

    List<Map<String,Object>> getProblemByMonth(Map<String,String> param);

    List<Map<String,Object>> getProblemByYear(Map<String,String> param);

    ResponseVo onetime(Map<String,String> param);

    ResponseVo onetimeByDay(Map<String,String> param);
}
