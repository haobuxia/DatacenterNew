package com.tianyi.datacenter.inspect.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianyi.datacenter.common.vo.PageListVo;
import com.tianyi.datacenter.common.vo.RequestVo2;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.feign.common.util.DSParamBuilder;
import com.tianyi.datacenter.feign.service.DataCenterFeignService;
import com.tianyi.datacenter.inspect.dao.CheckstationDao;
import com.tianyi.datacenter.inspect.dao.StationCheckDao;
import com.tianyi.datacenter.inspect.dao.StationDao;
import com.tianyi.datacenter.inspect.entity.Checkstation;
import com.tianyi.datacenter.inspect.entity.KStationcheck;
import com.tianyi.datacenter.inspect.service.StationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 工位检查配置
 *
 * @author wenxinyan
 * @version 0.1
 */
@Service
@Slf4j
public class StationServiceImpl extends ServiceImpl<StationDao, KStationcheck> implements StationService {
    @Autowired
    private CheckstationDao checkstationDao;
    @Autowired
    private StationDao stationDao;

    @Autowired
    private StationCheckDao stationCheckDao;
    @Autowired
    private DataCenterFeignService dataCenterFeignService;

    @Transactional
    public ResponseVo saveOfBest(Map<String, Object> param) {
        String stationName = (String) param.get("stationName");

        if (stationName == null) {
            return ResponseVo.fail("请配置工位");

        }
        //获取工位ID  获取的是前台配置的显示的工位名称的相应的ID
        Map<String, Object> cloummap = new HashMap<>();
        cloummap.put("stationName", stationName);
        List<Checkstation> checkstations = checkstationDao.selectByMap(cloummap);
        String stationId = checkstations.get(0).getId();
        log.info("查询的工位ID" + stationId);
        Map<String, Object> wrappermap = new HashMap<>();

        //所有的已经配置的与前台工位一致的检查项类别ID，开始可能没有
        Map<String, Object> cloum = new HashMap<>();
        cloum.put("stationId", stationId);
        List<KStationcheck> kStationchecks = stationDao.selectByMap(cloum);

        if (kStationchecks.size() == 0) {
            handelBatchIds(param);
            return ResponseVo.success("保存成功");
        }

        //前台传过来的检查类别ID  cidList如果之前有值现在传过来  没有值  的操作
        List<String> cidList = (List<String>) param.get("cid");
        String midFromWeb = (String) param.get("mid");
        if (cidList.size() == 0 || cidList == null) {
            //不是什么操作都不做  todo  根据机型删除同机型同工位的所有配置
            for (int i = 0; i < kStationchecks.size(); i++) {
                //查询已经配置了的同一工位的检查项类别对应的机型
                DSParamBuilder dsParamBuilderExist = new DSParamBuilder(12);
                dsParamBuilderExist.buildCondition("cid", "equals", kStationchecks.get(i).getChecktypeId());
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckTypeExist = dataCenterFeignService.retrieve(dsParamBuilderExist.build());
                String modelIdExist = "";
                if (retrieveCheckTypeExist.isSuccess() && retrieveCheckTypeExist.getMessage() == null) {
                    List<Map<String, Object>> rtnDataExist = (List<Map<String, Object>>) retrieveCheckTypeExist.getData().get("rtnData");
                    modelIdExist = (String) rtnDataExist.get(0).get("modelId");
                }
                if (modelIdExist.equalsIgnoreCase(midFromWeb)) {
                    //查询scid
                    DSParamBuilder dsParamBuilderExistScid = new DSParamBuilder(15);
                    dsParamBuilderExistScid.buildCondition("scid","equals",kStationchecks.get(i).getScid());
                    com.tianyi.datacenter.feign.common.vo.ResponseVo delete = dataCenterFeignService.delete(dsParamBuilderExistScid.build());
                    if (delete.isSuccess()&&delete.getMessage()==null){
                        return ResponseVo.success("保存成功");
                    }
                }
            }
        }

        /*//遍历前台的检查项类别ID因为是同一机型下的所以。。。
        String modelId = "";
        //{"condition":[{"key":"cid","condition":"equals","value":"2019062800087"}],"dataObjectId":12,"pageInfo":{"page":1,"count":0,"pageSize":100,"total":37}}
        DSParamBuilder dsParamBuilder = new DSParamBuilder(12);
        dsParamBuilder.buildCondition("cid", "equals", cidList.get(0));
        com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckType = dataCenterFeignService.retrieve(dsParamBuilder.build());
        if (retrieveCheckType.isSuccess() && retrieveCheckType.getMessage() == null) {
            List<Map<String, Object>> rtnData = (List<Map<String, Object>>) retrieveCheckType.getData().get("rtnData");
            modelId = (String) rtnData.get(0).get("modelId");

        }*/

        //旧机型检查项类别ID
        Set<String> listmodelExistScid = new HashSet<>();

        for (String cidlist : cidList) {
            //已经配置了的同一工位的配置实体
            for (int i = 0; i < kStationchecks.size(); i++) {
                //查询已经配置了的同一工位的检查项类别对应的机型
                DSParamBuilder dsParamBuilderExist = new DSParamBuilder(12);
                dsParamBuilderExist.buildCondition("cid", "equals", kStationchecks.get(i).getChecktypeId());
                com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckTypeExist = dataCenterFeignService.retrieve(dsParamBuilderExist.build());
                String modelIdExist = "";
                if (retrieveCheckTypeExist.isSuccess() && retrieveCheckTypeExist.getMessage() == null) {
                    List<Map<String, Object>> rtnDataExist = (List<Map<String, Object>>) retrieveCheckTypeExist.getData().get("rtnData");
                    modelIdExist = (String) rtnDataExist.get(0).get("modelId");
                }
                if (modelIdExist.equalsIgnoreCase(midFromWeb)) {
                    //查询scid
                   /* DSParamBuilder dsParamBuilderExistScid = new DSParamBuilder(15);
                    dsParamBuilderExistScid.buildCondition("checktypeId","equals",kStationchecks.get(i).getChecktypeId());
                    com.tianyi.datacenter.feign.common.vo.ResponseVo retrieve = dataCenterFeignService.retrieve(dsParamBuilderExistScid.build());
                    List<Map<String, Object>> list = (List<Map<String, Object>>) retrieve.getData().get("rtnData");*/
                    listmodelExistScid.add(kStationchecks.get(i).getScid());
                }
            }
        }
        //删除同机型同工位下的所有检查项类别
        if (listmodelExistScid.size()>0) {
            for (String scid : listmodelExistScid) {
                //先获取scid
                //删除数据路id listDB
                //{"dataObjectId":15,"condition":[{"key":"scid","condition":"equals","value":"2"}]}
                //{"dataObjectId":15,"condition":[{"key":"scid","condition":"equals","value":"18"}]}
                JSONObject jsonObjectDelete = new JSONObject();
                jsonObjectDelete.put("dataObjectId", 15);
                List<Map<String, Object>> condition = new ArrayList<>();
                Map<String, Object> conditionMap = new HashMap<>();
                conditionMap.put("key", "scid");
                conditionMap.put("condition", "equals");
                conditionMap.put("value", scid);
                condition.add(conditionMap);
                jsonObjectDelete.put("condition", condition);
                System.out.println(jsonObjectDelete);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.delete(jsonObjectDelete);
                if (responseVo.isSuccess()) {
                    log.info("后台要删除的工位配置ID" + scid);
                }
            }
            //保存前台传递的相同机型下的检查项类别
            for (String saveList : cidList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dataObjectId", 15);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieveId(jsonObject);
                if (responseVo.isSuccess()) {
                    JSONObject jsonObjectsave = new JSONObject();
                    jsonObjectsave.put("dataObjectId", 15);
                    Map<String, Object> data = new HashMap<>();
                    data.put("stationId", stationId);
                    data.put("checktypeId", saveList);
                    data.put("scid", responseVo.getData().get("rtnData"));
                    jsonObjectsave.put("data", data);
                    System.out.println(jsonObject);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSave = dataCenterFeignService.add(jsonObjectsave);
                    if (responseVoSave.isSuccess()) {
                        log.info("后台要保存的工位配置检查项ID" + responseVo.getData().get("rtnData"));
                    }else {
                        return ResponseVo.fail("保存失败");
                    }
                }
            }
        }else {
            //新机型的检查项类别的添加
            for (String cidlist : cidList) {
                //已经配置了的同一工位的配置实体
                for (int i = 0; i < kStationchecks.size(); i++) {
                    DSParamBuilder dsParamBuilderExist = new DSParamBuilder(12);
                    dsParamBuilderExist.buildCondition("cid", "equals", kStationchecks.get(i).getChecktypeId());
                    com.tianyi.datacenter.feign.common.vo.ResponseVo retrieveCheckTypeExist = dataCenterFeignService.retrieve(dsParamBuilderExist.build());
                    String modelIdExist = "";
                    if (retrieveCheckTypeExist.isSuccess() && retrieveCheckTypeExist.getMessage() == null) {
                        List<Map<String, Object>> rtnDataExist = (List<Map<String, Object>>) retrieveCheckTypeExist.getData().get("rtnData");
                        modelIdExist = (String) rtnDataExist.get(0).get("modelId");
                    }
                    if (modelIdExist.equalsIgnoreCase(midFromWeb)) {
                        break;
                    } else if ((kStationchecks.size() - 1) == i) {
                        //机型不同走新逻辑
                        //直接保存新的检查项工位配置
                        //检查项类别ID cidlist  工位ID  stationId
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("dataObjectId", 15);
                        com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieveId(jsonObject);
                        if (responseVo.isSuccess()) {
                            JSONObject jsonObjectsave = new JSONObject();
                            jsonObjectsave.put("dataObjectId", 15);
                            Map<String, Object> data = new HashMap<>();
                            data.put("stationId", stationId);
                            data.put("checktypeId", cidlist);
                            data.put("scid", (String) responseVo.getData().get("rtnData"));
                            jsonObjectsave.put("data", data);
                            System.out.println(jsonObject);
                            dataCenterFeignService.add(jsonObjectsave);
                        }
                    }
                }

            }
        }
        return ResponseVo.success("保存成功");

    }


    @Override
    @Transactional
    public ResponseVo handelBatchIds(Map<String, Object> param) {
        String stationName = (String) param.get("stationName");

        if (stationName == null) {
            return ResponseVo.fail("请配置工位");

        }
        //获取工位ID  获取的是前台配置的显示的工位名称的相应的ID
        Map<String, Object> cloummap = new HashMap<>();
        cloummap.put("stationName", stationName);
        List<Checkstation> checkstations = checkstationDao.selectByMap(cloummap);
        String stationId = checkstations.get(0).getId();
        log.info("查询的工位ID" + stationId);
        Map<String, Object> wrappermap = new HashMap<>();

        //所有的已经配置的检查项类别ID，开始可能没有
        Map<String, Object> cloum = new HashMap<>();
        cloum.put("stationId", stationId);
        List<KStationcheck> kStationchecks = stationDao.selectByMap(cloum);
        log.info("所有的已经配置的检查项类别实体长度" + kStationchecks.size());

        //前台传过来的检查类别ID  cidList
        List<String> cidList = (List<String>) param.get("cid");
        if (cidList.size() == 0 || cidList == null) {
            return ResponseVo.success();
        }


        //后台没有配置的保存所有
        log.info("前台的检查类别ID的长度" + cidList.size());
        if (kStationchecks == null) {
            for (String saveAll : cidList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("dataObjectId", 15);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieveId(jsonObject);
                if (responseVo.isSuccess()) {
                    JSONObject jsonObjectsave = new JSONObject();
                    jsonObjectsave.put("dataObjectId", 15);
                    Map<String, Object> data = new HashMap<>();
                    data.put("stationId", stationId);
                    data.put("checktypeId", saveAll);
                    data.put("scid", responseVo.getData().get("rtnData"));
                    jsonObject.put("data", data);
                    com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSave = dataCenterFeignService.add(jsonObjectsave);
                    if (responseVoSave.isSuccess()) {
                        continue;
                    }
                    {
                        return ResponseVo.success("保存失败");
                    }
                }
            }
        }
        /*List<String> temps = new ArrayList<>();
        log.info("前后台都有的配置了的工位和检查项长度" + temps.size());
        //获取  temps是前后都有的  cidList是后台要保存的
        if (kStationchecks != null || kStationchecks.size() != 0) {
            for (KStationcheck listDB : kStationchecks) {
                for (int i = cidList.size() - 1; i >= 0; i--) {
                    System.out.println(listDB.getChecktypeId());
                    System.out.println(cidList.get(i));
                    if (listDB.getChecktypeId().equals(cidList.get(i))) {
                        temps.add(cidList.get(i));
                        cidList.remove(i);
                        break;
                    } else {
                        continue;

                    }
                }
            }*/
            //kStationchecks  只保存要删除的
            /*if (cidList.size()>0&&cidList!=null) {
                for (int i = temps.size() - 1; i >= 0; i--) {
                    for (int y = 0; y < kStationchecks.size(); y++) {
                        if (kStationchecks.get(y).getChecktypeId().equals(cidList.get(i))) {
                            kStationchecks.remove(y);
                            continue;
                        }
                    }

                }
            }*/

            //遍历后删除前台没有传递的
            for (KStationcheck listDB : kStationchecks) {
                //先获取scid
                //删除数据路id listDB
                //{"dataObjectId":15,"condition":[{"key":"scid","condition":"equals","value":"2"}]}
                //{"dataObjectId":15,"condition":[{"key":"scid","condition":"equals","value":"18"}]}
                JSONObject jsonObjectDelete = new JSONObject();
                jsonObjectDelete.put("dataObjectId", 15);
                List<Map<String, Object>> condition = new ArrayList<>();
                Map<String, Object> conditionMap = new HashMap<>();
                conditionMap.put("key", "scid");
                conditionMap.put("condition", "equals");
                conditionMap.put("value", listDB.getScid());
                System.out.println(listDB.getScid());
                condition.add(conditionMap);
                jsonObjectDelete.put("condition", condition);
                System.out.println(jsonObjectDelete);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.delete(jsonObjectDelete);
                if (responseVo.isSuccess()) {
                    log.info("后台要删除的工位配置检查项ID" + listDB.getScid());
                }
            }
        //保存
        //{"dataObjectId":15,"data":{"checktypeId":"3","stationId":"4","scid":"222"}}
        //listDB.getChecktypeId()是 checktypeId  stationId 是stationId  scid是 雪花算法
        for (String saveList : cidList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("dataObjectId", 15);
            com.tianyi.datacenter.feign.common.vo.ResponseVo responseVo = dataCenterFeignService.retrieveId(jsonObject);
            if (responseVo.isSuccess()) {
                JSONObject jsonObjectsave = new JSONObject();
                jsonObjectsave.put("dataObjectId", 15);
                Map<String, Object> data = new HashMap<>();
                data.put("stationId", stationId);
                data.put("checktypeId", saveList);
                data.put("scid", responseVo.getData().get("rtnData"));
                jsonObjectsave.put("data", data);
                System.out.println(jsonObject);
                com.tianyi.datacenter.feign.common.vo.ResponseVo responseVoSave = dataCenterFeignService.add(jsonObjectsave);
                if (responseVoSave.isSuccess()) {
                    log.info("后台要保存的工位配置检查项ID" + responseVo.getData().get("rtnData"));
                    continue;
                }
                {
                    return ResponseVo.success("保存失败");
                }
            }
        }


        return ResponseVo.success("保存成功");
    }

    @Override
    @Transactional
    public ResponseVo selectByPage(RequestVo2 requestVo) {

        PageListVo pageInfo = requestVo.getPageInfo();
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> param = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
        param.putAll((Map<? extends String, ?>) requestVo.getCondition());
        if ((pageInfo.getPageSize()) == 0) {
            List<Map<String, Object>> mapsNoPage = stationCheckDao.searchNoPage(param);
            int count = stationCheckDao.countBy(param);
            result.put("rtnData", mapsNoPage);
            return ResponseVo.success(result);
        }

        List<Map<String, Object>> dataObjectList = stationCheckDao.searchByPage(param);
        int count = stationCheckDao.countBy(param);
        pageInfo.setTotal(count);
        result.put("pageInfo", pageInfo);
        result.put("rtnData", dataObjectList);
        ResponseVo responseVo = ResponseVo.success(result);
        return responseVo;
    }

    @Override
    public int selectNoPage(Map<String, Object> param) {
        return 0;
    }


    /**
     *  public ResponseVo list(RequestVo<Map> requestVo) throws DataCenterException {
     *         PageListVo pageInfo = requestVo.getPageInfo();
     *         Map<String, Object> param = PageListVo.createParamMap(pageInfo.getPage(), pageInfo.getPageSize());
     *         param.putAll(requestVo.getRequest());
     *
     *         List<DataObject> dataObjectList = dataObjectDao.listBy(param);
     *         int count = dataObjectDao.countBy(param);
     *
     *         pageInfo.setTotal(count);
     *
     *         Map<String, Object> result = new HashMap<>();
     *         result.put("pageInfo", pageInfo);
     *         result.put("list", dataObjectList);
     *
     *         ResponseVo responseVo = ResponseVo.success(result);
     *
     *         return responseVo;
     *     }
     */
}




