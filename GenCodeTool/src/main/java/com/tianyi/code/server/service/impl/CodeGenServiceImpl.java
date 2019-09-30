package com.tianyi.code.server.service.impl;

import com.tianyi.code.server.entity.ColumnClass;
import com.tianyi.code.server.service.CodeGenService;
import com.tianyi.code.server.util.FreeMarkerStringUtil;
import com.tianyi.code.server.util.FreeMarkerUtil;
import com.tianyi.code.server.util.MyConstants;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CodeGenServiceImpl implements CodeGenService{
    public void generate(String tableName, String diskPath, String changeTableName, String author, String packageName, String tableAnnotation, String modelName, String suffix,String genWay, List<ColumnClass> columnClassList) throws Exception {
        try {
            //资源生成
            generateFile(suffix,diskPath,changeTableName,tableName,author,packageName,tableAnnotation,modelName,genWay,columnClassList,2);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public List<String> generate(String tableName, String passowrd, String url, String user, String driver, String diskPath, String changeTableName, String author, String packageName, String tableAnnotation, String modelName, String suffix) throws Exception {
        try {
            //获取数据库连接
            Connection connection = getConnection(driver, url, user, passowrd);
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            ResultSet rs = databaseMetaData.getTables(null, null, null,
                    new String[]{"TABLE"});
            List<String> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getString(3));
            }
            //如果是获取表名，则changeTableName为空，返回表名的list，不再往下执行
            if (changeTableName.equals("")) {
                return list;
            }
            //如果是获取所有字段信息生成代码，执行
            ResultSet resultSet = databaseMetaData.getColumns(null, "%", tableName, "%");
            List<ColumnClass> columnClassList = getColumns(resultSet);
            generateFile(suffix, diskPath, changeTableName, tableName, author, packageName, tableAnnotation, modelName,"",columnClassList,2);
            connection.close();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void generateFileByTemplate(final String templateName, File file, Map<String, Object> dataMap, String tableName, String changeTableName, String author, String packageName, String tableAnnotation, String modelName) throws Exception {
        Template template = FreeMarkerUtil.getTemplate(templateName);
        FileOutputStream fos = new FileOutputStream(file);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = sdf.format(new Date());
        dataMap.put("table_name_small", tableName);
        dataMap.put("table_name", changeTableName);
        dataMap.put("author", author);
        dataMap.put("date", currentDate);
        dataMap.put("package_name", packageName);
        dataMap.put("table_annotation", tableAnnotation);
        dataMap.put("model_name", modelName);
        //控制文件输出的大小，写的小可能拦腰斩断
        Writer out = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"), 102400);
        template.process(dataMap, out);
        fos.close();
        out.close();
    }

    /**
     * 将表名和字段名替换为正常的驼峰命名
     * @param str
     * @return
     */
    public String replaceUnderLineAndUpperCase(String str) {
        StringBuffer sb = new StringBuffer();
        sb.append(str);
        int count = sb.indexOf("_");
        while (count != 0) {
            int num = sb.indexOf("_", count);
            count = num + 1;
            if (num != -1) {
                char ss = sb.charAt(count);
                char ia = (char) (ss - 32);
                sb.replace(count, count + 1, ia + "");
            }
        }
        String result = sb.toString().replaceAll("_", "");
        return StringUtils.capitalize(result);
    }

    @Override
    public String generatePart(String templateStr, List<ColumnClass> columnClassList) throws Exception {
        // 代码片段生成
        StringWriter sw = new StringWriter();
        FreeMarkerStringUtil.setTemplate(templateStr);
        Template template = FreeMarkerStringUtil.getTemplate("myTemplate");
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("model_column", columnClassList);
        template.process(dataMap, sw);
        System.out.println(sw.toString());
        return sw.toString();
    }

    private List<ColumnClass> getColumns(ResultSet resultSet) throws Exception {
        List<ColumnClass> columnClassList = new ArrayList<>();

        ColumnClass columnClass = null;
        String typeName="";
        while (resultSet.next()) {
            //id字段略过
            if (resultSet.getString("COLUMN_NAME").equals("id")) continue;
            columnClass = new ColumnClass();
            //获取字段名称
            columnClass.setColumnName(resultSet.getString("COLUMN_NAME"));
            //获取字段类型
            typeName=resultSet.getString("TYPE_NAME");
            if ("datetime".equalsIgnoreCase(typeName))
            {
                typeName = "DATE";
            }else if ("int".equalsIgnoreCase(typeName))
            {
                typeName = "INTEGER";
            }
            columnClass.setColumnType(typeName);
            //转换字段名称，如 sys_name 变成 SysName
            columnClass.setChangeColumnName(replaceUnderLineAndUpperCase(resultSet.getString("COLUMN_NAME")));

            //字段在数据库的注释
            columnClass.setColumnComment(resultSet.getString("REMARKS"));
            columnClass.setType("");
            columnClassList.add(columnClass);
        }
        return columnClassList;
    }


    private Connection getConnection(String driver, String url, String user, String password) throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    private void generateFile(String suffix, String diskPath, String changeTableName, String tableName, String author, String packageName, String tableAnnotation, String modelName,String genWay, List<ColumnClass> columnClassList,int flag) throws Exception {
         String path = "";
        //diskPath + "/" + changeTableName + suffix;
        String packagePath="";
        Map<String, Object> dataMap = new HashMap<>();
        String firstEnityName=  columnClassList.get(0).getChangeColumnName();
        dataMap.put("firstEnityName",firstEnityName);
        String templateName = "";
        switch (suffix) {
            case MyConstants.CONTROLLER_SUFFIX:
                if ("1".equals(genWay) )  // 调用Jar包
                {
                    templateName = "formController1.ftl";
                }else  if ("2".equals(genWay))  // 调用Http方式
                {
                    templateName = "formController1.ftl";
                }else
                {
                    templateName = "formController.ftl";
                }
                packagePath="controller/";
                break;
            case MyConstants.DAO_SUFFIX:
                templateName = "formDao.ftl";
                packagePath="dao/";
                break;
            case MyConstants.ENTITY_SUFFIX:
                templateName = "formBean.ftl";
                dataMap.put("model_column", columnClassList);
                packagePath="entity/";
                break;
            case MyConstants.IMPL_SUFFIX:
                if ("1".equals(genWay))  // 调用Jar包
                {
                    templateName = "formServiceImpl1.ftl";
                }else  if ("2".equals(genWay))  // 调用Http方式
                {
                    templateName = "formServiceImpl2.ftl";
                } else {
                    templateName = "formServiceImpl.ftl";
                }
                packagePath="service/impl/";
                break;
            case MyConstants.MAPPER_SUFFIX:
                templateName = "formMapper.ftl";
                dataMap.put("model_column", columnClassList);
                packagePath="";
                break;
            case MyConstants.SERVICE_SUFFIX:
                if ("1".equals(genWay))  // 调用Jar包
                {
                    templateName = "formService1.ftl";
                }else  if ("2".equals(genWay))  // 调用Http方式
                {
                    templateName = "formService1.ftl";
                } else {
                    templateName = "formService.ftl";
                }
                packagePath="service/";
                break;
            default:
                if(flag == 1){
                    templateName = "formVue.ftl";
                }else if(flag == 2){
                    templateName = "KomatsuVue.ftl";
                } else {
                    templateName = "Vue.ftl";
                }
                dataMap.put("model_column", columnClassList);
                break;
        }


        if ("1".equals(genWay) || "2".equals(genWay))  // 调用Jar包或者调用Http方式
        {
            if (suffix.equals(MyConstants.CONTROLLER_SUFFIX)  || suffix.equals(MyConstants.IMPL_SUFFIX)
                    || suffix.equals(".vue") || suffix.equals(MyConstants.SERVICE_SUFFIX))
            {
                path = diskPath + packagePath + changeTableName + suffix;
                File mapperFile = new File(path);
                File fileParent = mapperFile.getParentFile();
                if(!fileParent.exists()){
                    fileParent.mkdirs();
                }
                generateFileByTemplate(templateName, mapperFile, dataMap, tableName, changeTableName, author, packageName, tableAnnotation, modelName);
            }
        }else
        {
            path = diskPath + packagePath + changeTableName + suffix;
            File mapperFile = new File(path);
            File fileParent = mapperFile.getParentFile();
            if(!fileParent.exists()){
                fileParent.mkdirs();
            }
            generateFileByTemplate(templateName, mapperFile, dataMap, tableName, changeTableName, author, packageName, tableAnnotation, modelName);
        }


    }

}
