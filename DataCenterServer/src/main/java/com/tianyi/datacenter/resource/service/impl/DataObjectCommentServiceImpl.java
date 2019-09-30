package com.tianyi.datacenter.resource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.resource.dao.DataObjectCommentDao;
import com.tianyi.datacenter.resource.entity.DataObject;
import com.tianyi.datacenter.resource.entity.DataObjectComment;
import com.tianyi.datacenter.resource.service.DataObjectCommentService;
import com.tianyi.datacenter.resource.service.DataObjectService;
import com.tianyi.datacenter.storage.util.DMLUtil;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据对象服务实现
 *
 * @author tixnujin
 * @version 0.1
 */
@Service
public class DataObjectCommentServiceImpl implements DataObjectCommentService {

    @Autowired
    private DataObjectCommentDao dataObjectCommentDao;
    @Autowired
    private DataObjectService dataObjectService;

    Logger logger = LoggerFactory.getLogger(DataObjectCommentServiceImpl.class);

    @Override
    @Transactional
    public int insert(DataObjectComment dataObjectComment) {
        return dataObjectCommentDao.insert(dataObjectComment);
    }

    @Override
    @Transactional
    public void updates(JSONObject jsonObject) {
        List<Map<String, Object>> requestList = (List<Map<String, Object>>) jsonObject.get("list");
        int resId = (int) jsonObject.get("resId");
        Map<String, Object> params = new HashMap<>();
        params.put("resId", resId);
        List<DataObjectComment> list = new ArrayList<>();
        for(Map<String, Object> map : requestList){
            DataObjectComment dataObjectComment = new DataObjectComment();
            String name = map.get("name")==null?"":map.get("name").toString();
            params.put("name", name);
            list = this.listNoPage(params);
            if(list==null || list.size()<=0) {
                dataObjectComment.setResId(resId);
                dataObjectComment.setName(name);
                dataObjectComment.setComment(map.get("comment")==null?"":map.get("comment").toString());
                this.insert(dataObjectComment);
            } else {
                dataObjectComment = list.get(0);
                dataObjectComment.setResId(resId);
                dataObjectComment.setName(map.get("name")==null?"":map.get("name").toString());
                dataObjectComment.setComment(map.get("comment")==null?"":map.get("comment").toString());
                this.update(dataObjectComment);
            }
        }
    }

    @Override
    public ResponseVo listDataSetColumns(JSONObject jsonParam) {
        Map<String, Object> map = new HashMap<>();
        int resId = Integer.parseInt(jsonParam.get("resId").toString());
        map.put("resId", jsonParam.get("resId"));
        ResponseVo responseVo = ResponseVo.fail("查询对象失败！");
        List<DataObjectComment> list = this.listNoPage(map);
        DataObject dataObject = dataObjectService.getById(resId);
        if(!dataObject.getType().equals("数据集")){
            responseVo = ResponseVo.fail("请传入数据集类型的resId！");
            return responseVo;
        }
        String sql = dataObject.getDefined();
        List<String> columnList = DMLUtil.getColumnListBySqlDruid(sql);
        if(list.size()>0) {
            for(int i = list.size()-1; i >=0; i--) {
                DataObjectComment comment = list.get(i);
                if(columnList.contains(comment.getName())){// 存在保留
                    columnList.remove(comment.getName()); //验证过的删除
                } else {// 不存在删除
                    list.remove(i);
                    this.delete(comment.getId());
                }
            }
        }
        for(String column: columnList) {
            DataObjectComment dataObjectComment = new DataObjectComment();
            dataObjectComment.setResId(resId);
            dataObjectComment.setName(column);
            list.add(dataObjectComment);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        responseVo = ResponseVo.success(result);
        return responseVo;
    }

    @Override
    @Transactional
    public int delete(int id) {
        return dataObjectCommentDao.delete(id);
    }

    @Override
    @Transactional
    public int update(DataObjectComment dataObjectComment) {
        return dataObjectCommentDao.update(dataObjectComment);
    }

    @Override
    public ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException {
        PageListVo pageInfo = requestVo.getPageInfo();
        Map<String, Object> param = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
        param.putAll(requestVo.getRequest());

        List<DataObjectComment> dataObjectCommentList = dataObjectCommentDao.listBy(param);
        int count = dataObjectCommentDao.countBy(param);

        pageInfo.setTotal(count);

        Map<String, Object> result = new HashMap<>();
        result.put("pageInfo", pageInfo);
        result.put("list", dataObjectCommentList);

        ResponseVo responseVo = ResponseVo.success(result);

        return responseVo;
    }

    @Override
    public DataObjectComment getById(int id){
        return dataObjectCommentDao.getById(id);
    }

    @Override
    public List<DataObjectComment> listNoPage(Map<String, Object> param){
        return dataObjectCommentDao.listByNoPage(param);
    }

}
