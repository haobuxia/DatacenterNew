package com.tianyi.code.server.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.cache.StringTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;

/**
 * Created by tianxujin
 */

public class FreeMarkerStringUtil {

    private FreeMarkerStringUtil() {
    }

    private static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_22);

    static {
        //这里比较重要，用来指定加载模板所在的路径
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    public static Template getTemplate(String templateName) throws IOException {
        try {
            return CONFIGURATION.getTemplate(templateName);
        } catch (IOException e) {
            throw e;
        }
    }

    public static void setTemplate(String template) {
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("greetTemplate",
                "<#macro greet model>" +
//                "<el-form-item label=\"${model.columnComment}\" prop=\"${model.columnName}\">\n" +
//                "   <el-input v-model=\"viewAdd.data.${model.columnName}\" placeholder=\"请输入${model.columnComment}\"></el-input>\n" +
//                "</el-form-item>\n" +
                        template + "\n" +
                "</#macro>");
        stringLoader.putTemplate("myTemplate", "<#include \"greetTemplate\"><#if model_column?exists><#list model_column as model><@greet model/></#list></#if>");
        CONFIGURATION.setTemplateLoader(stringLoader);
    }

    public static void clearCache() {
        CONFIGURATION.clearTemplateCache();
    }
}