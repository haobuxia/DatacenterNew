package com.tianyi.code.server.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.tianyi.code.server.entity.ColumnClass;
import com.tianyi.code.server.service.CodeGenService;
import com.tianyi.code.server.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：代码生成器by资源中心
 * Created by xy on 2018/10/30.
 *
 */
@Controller
@RequestMapping("/generate")
public class CodeGenResController {
    @Autowired
    private CodeGenService codeGenService;

    @RequestMapping("hello")
    public String index() {
        return "hello";
    }

    @ResponseBody
    @RequestMapping(value = "/code/res", method = RequestMethod.POST)
    @CrossOrigin
/*    public ResponseVo generator(@RequestParam String author, @RequestParam String tableName, @RequestParam String packageName,
                                @RequestParam String diskPath, @RequestParam String tableAnnotation, @RequestParam String modelName,
                                @RequestParam List<String> suffixs, @RequestParam String objectList, @RequestParam Integer resId,
                                @RequestParam String isDic) throws Exception {*/
    public ResponseVo generator(@RequestBody JSONObject jsonParam) throws Exception {
        String author = (String) jsonParam.get("author");
        String tableName = (String) jsonParam.get("tableName");
        String packageName = (String) jsonParam.get("packageName");
        String diskPath = (String) jsonParam.get("diskPath");
        String tableAnnotation = (String) jsonParam.get("tableAnnotation");
        String modelName = (String) jsonParam.get("modelName");
        String beanName = (String) jsonParam.get("beanName");
        List<String> suffixs = (List<String>) jsonParam.get("suffixs");
        String objectList = (String) jsonParam.get("objectList");
        Integer resId = (Integer) jsonParam.get("resId");
        String isDic = (String) jsonParam.get("isDic");
        String genWay = (String) jsonParam.get("genWay");

//        String changeTableName = codeGenService.replaceUnderLineAndUpperCase(tableName);
        String changeTableName = codeGenService.replaceUnderLineAndUpperCase(beanName);
        if (!diskPath.endsWith("\\"))
        {
            diskPath = diskPath + "\\";
        }
        objectList = objectList.replace("\"", "\'");
        JSONObject jsonObject = JSON.parseObject(objectList);
        String listString = jsonObject.getString("list");
        List<Map<String, String>> list = JSON.parseObject(listString, new TypeReference<List>() {});
        //遍历拿到的objectList字符串，将需要的值存入到字段对象的list中

        List<ColumnClass> columnClassList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ColumnClass columnClass = new ColumnClass();
            columnClass.setColumnType(list.get(i).get("jdbcType"));
            columnClass.setColumnComment(list.get(i).get("description"));   //中文名 用于实体类的备注信息
            columnClass.setColumnName(list.get(i).get("columnName"));  //数据库字段名称
            String enityName="",firstUpcase="";
            enityName=list.get(i).get("name");
            columnClass.setChangeColumnName(enityName);  //实体类的属性名称
            firstUpcase =String.valueOf(enityName.charAt(0)).toUpperCase() + enityName.substring(1);
            columnClass.setFirstUpcaseName(firstUpcase);
            columnClass.setResId(resId);
            columnClass.setType(list.get(i).get("type"));
            columnClass.setDicRes(String.valueOf(list.get(i).get("dicRes")));
            columnClass.setIsNull(String.valueOf(list.get(i).get("isNull")));
            columnClassList.add(columnClass);
        }
        for (String suffix : suffixs) {
            codeGenService.generate(tableName, diskPath, changeTableName, author, packageName, tableAnnotation, modelName, suffix, genWay, columnClassList);
        }
        return ResponseVo.success();
    }

    @ResponseBody
    @RequestMapping(value = "/code/part", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseVo generatorPart(@RequestBody JSONObject jsonParam) throws Exception {
        int source = (int) jsonParam.get("source"); // 数据来源0数据库1数据服务2DWF
        String template = (String) jsonParam.get("template");// 模板片段
        String modelName = (String) jsonParam.get("modelName");// 对应source，0表名,1resid,2DWF
        String objectList = (String) jsonParam.get("objectList");
        String changeTableName = codeGenService.replaceUnderLineAndUpperCase(modelName);

        objectList = objectList.replace("\"", "\'");
        JSONObject jsonObject = JSON.parseObject(objectList);
        String listString = jsonObject.getString("list");
        List<Map<String, String>> list = JSON.parseObject(listString, new TypeReference<List>() {
        });
        //遍历拿到的objectList字符串，将需要的值存入到字段对象的list中
        List<ColumnClass> columnClassList = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ColumnClass columnClass = new ColumnClass();
            columnClass.setColumnType(list.get(i).get("jdbcType"));
            columnClass.setColumnComment(list.get(i).get("description"));
            columnClass.setColumnName(list.get(i).get("columnName"));
            columnClass.setChangeColumnName(changeTableName);
            columnClass.setFirstUpcaseName(list.get(i).get("name"));
//            columnClass.setResId(resId);
            columnClass.setType(list.get(i).get("type"));
            columnClass.setDicRes(list.get(i).get("dicRes"));
            columnClassList.add(columnClass);
        }
        Map msg = new HashMap();
        if (template != null && !template.equals("") && columnClassList.size() > 0) {
            String partResult = codeGenService.generatePart(template, columnClassList);
            msg.put("partCode", partResult);
        }
        return ResponseVo.success(msg);
    }
}