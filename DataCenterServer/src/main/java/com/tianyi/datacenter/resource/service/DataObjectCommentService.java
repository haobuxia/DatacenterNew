package com.tianyi.datacenter.resource.service;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.entity.DataObjectComment;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;

import java.util.List;
import java.util.Map;

/**
 * Created by tianxujin on 2019/4/12 14:42
 */

public interface DataObjectCommentService {
    ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException;

    DataObjectComment getById(int id);

    List<DataObjectComment> listNoPage(Map<String, Object> map);

    int update(DataObjectComment dataObjectComment);

    int delete(int id);

    int insert(DataObjectComment dataObjectComment);

    void updates(JSONObject jsonObject);

    ResponseVo listDataSetColumns(JSONObject jsonParam);
}
