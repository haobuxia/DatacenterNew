package com.tianyi.code.server.controller;


import com.alibaba.fastjson.JSONObject;
import com.tianyi.code.server.service.CodeGenService;
import com.tianyi.code.server.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 描述：代码生成器by数据库
 * Created by xy on 2018/10/30.
 */
@Controller
@RequestMapping("/gen")
public class CodeGenerateController {
    private final String DRIVER = "com.mysql.jdbc.Driver";

    @Autowired
    private CodeGenService codeGenService;
    @RequestMapping("hello")
    public String index(){
        return "index";
    }
    @ResponseBody
    @RequestMapping(value = "/code", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseVo generator(@RequestBody JSONObject jsonObject) throws Exception {

        String author = (String)jsonObject.get("author");
        String tableName = (String)jsonObject.get("tableName");
        String password = (String)jsonObject.get("password");
        String url = (String)jsonObject.get("url");
        String user = (String)jsonObject.get("user");
        String diskPath = (String)jsonObject.get("diskPath");
        String packageName = (String)jsonObject.get("packageName");
        String tableAnnotation = (String)jsonObject.get("tableAnnotation");
        String modelName = (String)jsonObject.get("modelName");
        List<String> suffix = (List<String>)jsonObject.get("suffix");
        if (!diskPath.endsWith("\\"))
        {
            diskPath = diskPath + "\\";
        }
        String changeTableName = codeGenService.replaceUnderLineAndUpperCase(tableName);
        //会多选，所以遍历生成
        for(String file: suffix){
            codeGenService.generate(tableName, password, url, user, DRIVER, diskPath, changeTableName, author, packageName, tableAnnotation, modelName,file);
        }

        return ResponseVo.success();
    }

    @ResponseBody
    @RequestMapping(value = "/table", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseVo getTable(@RequestBody JSONObject jsonObject) throws Exception {
        String url = (String)jsonObject.get("url");
        String password = (String)jsonObject.get("password");
        String user = (String)jsonObject.get("user");
        List<String> list = codeGenService.generate("", password, url, user, DRIVER, "", "", "", "", "","","");
        return ResponseVo.success(list);
    }

}