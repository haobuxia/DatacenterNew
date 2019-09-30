package com.tianyi.datacenter.resource.dao;

import com.tianyi.datacenter.resource.entity.DataObjectComment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by tianxujin on 2019/4/3 14:58
 */
@Repository
public interface DataObjectCommentDao {
    int insert(DataObjectComment dataObjectComment);

    int delete(int id);

    int update(DataObjectComment dataObjectComment);

    List<DataObjectComment> listBy(Map<String, Object> param);

    int countBy(Map<String, Object> param);

    List<DataObjectComment> listByNoPage(Map<String, Object> param);

    DataObjectComment getById(int id);
}
