package com.tianyi.code.server.service;

import com.tianyi.code.server.entity.ColumnClass;

import java.io.IOException;
import java.util.*;


public interface CodeGenService {
    void generate(String tableName, String diskPath, String changeTableName, String author, String packageName, String tableAnnotation, String modelName, String suffix,String genWay, List<ColumnClass> columnClassList) throws Exception;

    List<String> generate(String tableName, String passowrd, String url, String user, String driver, String diskPath, String changeTableName, String author, String packageName, String tableAnnotation, String modelName, String suffix) throws Exception;

    String replaceUnderLineAndUpperCase(String str);

    String generatePart(String template, List<ColumnClass> columnClassList) throws IOException, Exception;
}
