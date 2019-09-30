<#import 'MySQL.ftl' as columnInfo>
CREATE TABLE ${dataObject.defined}
(
<@columnInfo.column_default></@columnInfo.column_default>
<#if columns??>,
<#list columns as column>
 <@columnInfo.column_definition column=column></@columnInfo.column_definition>
 <#if column_has_next>, </#if>
</#list>
</#if>
)
 default character set = 'utf8'
<#if dataObject.description?exists> COMMENT '${dataObject.description}'</#if>
