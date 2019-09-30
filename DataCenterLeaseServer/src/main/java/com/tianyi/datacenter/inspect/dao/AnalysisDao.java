package com.tianyi.datacenter.inspect.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AnalysisDao extends BaseMapper<Object> {

    List<Map<String, Object>> queryByDay(Map<String, String> param);

    List<Map<String, Object>> queryByMouth(Map<String, String> param);

    List<Map<String, Object>> queryByYear(Map<String, String> param);

    List<Map<String, Object>> getCheckerByDay(Map<String, String> param);

    List<Map<String, Object>> getCheckerByMonth(Map<String, String> param);

    List<Map<String, Object>> getCheckerByYear(Map<String, String> param);

    List<Map<String, Object>> getProblemByDay(Map<String, String> param);

    List<Map<String, Object>> getProblemByMonth(Map<String, String> param);

    List<Map<String, Object>> getProblemByYear(Map<String, String> param);

    List<Map<String, Object>> getProblemByDayHaveProblem(Map<String, String> param);

    List<Map<String, Object>> getProblemByMonthHaveProblem(Map<String, String> param);

    List<Map<String, Object>> getProblemByYearHaveProblem(Map<String, String> param);
}
