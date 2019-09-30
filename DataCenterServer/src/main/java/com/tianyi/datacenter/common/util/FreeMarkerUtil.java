package com.tianyi.datacenter.common.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Freemarker模板相关功能
 *
 * @author zhouwei
 * 2018/11/19 21:48
 * @version 0.1
 **/
public class FreeMarkerUtil {

    private static Configuration config = new Configuration(Configuration.getVersion());


    /**
     * 获取freemarker配置
     *
     * @author zhouwei
     * 2018/11/19 22:00
     */
    private static Configuration getConfiguration() throws IOException {
        //获取freemarker配置
        config.setClassForTemplateLoading(FreeMarkerUtil.class, "/template");

        config.setDefaultEncoding("UTF-8");
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        return config;
    }

    /**
     * 数据与模板绑定，生成dml和ddl语句
     * @author zhouwei
     * 2018/11/19 21:58
     *
     * @param root 数据对象
     * @param name 模板名称
     * @return 查询sql语句
     */
    public static String process(FreeMarkerRoot root, String name) throws TemplateException, IOException {
        Configuration config = getConfiguration();

        //获取模板
        Template template = config.getTemplate(name + ".ftl");

        //输出到string中
        StringWriter sw = new StringWriter();
        BufferedWriter bw = new BufferedWriter(sw);

        template.process(root.getFtlRoot(), bw);
        bw.close();
        sw.close();

        return sw.toString();
    }
}
