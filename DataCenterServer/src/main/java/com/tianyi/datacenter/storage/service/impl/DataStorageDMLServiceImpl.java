package com.tianyi.datacenter.storage.service.impl;

import com.tianyi.datacenter.common.exception.DataCenterException;
import com.tianyi.datacenter.storage.service.DataStorageDMLService;
import com.tianyi.datacenter.storage.util.DBUtil;
import com.tianyi.datacenter.storage.util.DMLUtil;
import com.tianyi.datacenter.storage.vo.DataStorageDMLVo;
import com.tianyi.datacenter.storage.vo.PageListVo;
import com.tianyi.datacenter.storage.vo.RequestVo;
import com.tianyi.datacenter.storage.vo.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据存储模块执行DML语句服务实现
 * 2018/11/15 19:03
 *
 * @author zhouwei
 * @version 0.1
 **/
@Service
public class DataStorageDMLServiceImpl implements DataStorageDMLService {

    @Autowired
    private DBUtil database;

    @Autowired
    private DMLUtil dmlUtil;

    private Logger logger = LoggerFactory.getLogger(DataStorageDMLService.class);
    /**
     * 生成DML执行语句服务
     *
     * @param requestVo 请求对象
     * @return sql语句
     * @author zhouwei
     * 2018/11/15 19:03
     **/
    public ResponseVo doServer(RequestVo<DataStorageDMLVo> requestVo) {
        DataStorageDMLVo dmlVo = requestVo.getRequest();

        ResponseVo validResult = validParam(dmlVo);
        if(!validResult.isSuccess()){
           //参数校验失败
            logger.error(validResult.getMessage());
            return validResult;
        }

        String dmlType = dmlVo.getDmlType();
        //拼接sql语句
        String sql;
        try {
            sql = dmlUtil.generateDML(dmlVo, requestVo.getPageInfo(), requestVo.getOrderBy());
        } catch (DataCenterException e) {
            return ResponseVo.fail("生成dml语句失败");
        }

        Map<String,Object> rtnInfo = new HashMap<>();
        if("R".equals(dmlType)){
            //执行sql语句
            List<Map<String, Object>> datas = database.executeQuery(sql);
            PageListVo pageInfo = requestVo.getPageInfo();
            if(datas.size()>0){
                String totalSql = "";
                if(sql.indexOf("LIMIT") != -1){
                    totalSql = sql.substring(0, sql.indexOf("LIMIT")-1);
                } else {
                    totalSql = sql;
                }
                List<Map<String, Object>> totalNumList = database.executeQuery("select count(*) as total from (" + totalSql + ") a");
                Map<String, Object> map = totalNumList.get(0);
                pageInfo.setTotal(Integer.parseInt(map.get("total").toString()));
            } else {
                pageInfo.setTotal(0);
            }
            if (datas.size() < 1) {
                Map<String, Object> result = new HashMap<>();
                result.put("rtnData", datas);
                return ResponseVo.success(result,"没有查询到数据");
            } else {
                if ("数据集".equals(dmlVo.getDataObject().getType())) {
                    DMLUtil.handleDataList(datas, sql);
                }
                rtnInfo.put("rtnData", datas);
                rtnInfo.put("pageInfo", pageInfo);
                return ResponseVo.success(rtnInfo);
            }
        }else{
            int i = database.executeDML(dmlType, sql);
            if(i<1){
                return ResponseVo.fail("操作失败");
            }else{
                return ResponseVo.success();
            }
        }
    }

    /**
     * 服务上送参数校验方法
     * @author zhouwei
     * 2018/12/3 13:43
     * @param dmlVo 请求参数
     * @return ResponseVo.success 为true表示校验成功
    */
    private ResponseVo validParam(DataStorageDMLVo dmlVo) {
        //校验请求参数
        String dmlType = dmlVo.getDmlType();
        if (StringUtils.isEmpty(dmlType) || ObjectUtils.isEmpty(dmlVo.getDataObject())) {
            //操作类型参数不能为空
            return ResponseVo.fail("参数校验失败，操作类型或数据对象信息不可为空");
        } else {
            //数据集只能做查询操作
            String objectType = dmlVo.getDataObject().getType()==null?"":dmlVo.getDataObject().getType();
            if ( objectType.equals("数据集") && !"R".equals(dmlVo.getDmlType())) {
                return ResponseVo.fail("操作失败,数据集只能做查询操作");
            }
            if ("U".equals(dmlType)) {
                if (CollectionUtils.isEmpty(dmlVo.getCondition()) || CollectionUtils.isEmpty(dmlVo.getAttributes())) {
                    //更新操作，更新信息不能为空
                    return ResponseVo.fail("参数校验失败，更新字段和条件不可为空");
                }
            }else if("C".equals(dmlType)){
                if(CollectionUtils.isEmpty(dmlVo.getAttributes())){
                    //新增操作，字段信息不能为空
                    return ResponseVo.fail("参数校验失败，字段信息不可为空");
                }
            } else if ("D".equals(dmlType)) {
                if (CollectionUtils.isEmpty(dmlVo.getCondition())) {
                    return ResponseVo.fail("参数校验失败，条件不可为空");
                }
            }
        }

        return ResponseVo.success();
    }

}


