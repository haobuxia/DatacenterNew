<#import "MySQL.ftl" as col_type>
<#assign  join="">
SELECT DISTINCT
<#if columns?exists>
    <#list columns as column> ${dataObject.defined}.${column.columnName} as ${column.name}
    <#if column.dicRes != -1>
     , ${column.dicResColumnName}.${column.dicValueColumnName} as ${column.dicResName}${column.dicValueName}
     <#assign join="${join} left join ${column.dicResColumnName} on ${dataObject.defined}.${column.columnName}=${column.dicResColumnName}.${column.dicKeyColumnName}">
    </#if>
     <#if column_has_next>, </#if>
     </#list>
<#else > *
</#if>
 FROM ${dataObject.defined}${join}
<#if condition?exists>
 WHERE <#list condition as col>
${dataObject.defined}.${col.columnName}<@col_type.getOper attribute=col></@col_type.getOper><@col_type.getJdbcType attribute=col > </@col_type.getJdbcType>
 <#if  col_has_next> and </#if></#list>
</#if>
<#if orderBy?exists>
 order by ${orderBy}
</#if>
<#if pageInfo?exists>
 LIMIT ${pageInfo.start},${pageInfo.length}
</#if>