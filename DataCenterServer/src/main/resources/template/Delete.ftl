<#import "MySQL.ftl" as col_type>
DELETE FROM ${dataObject.defined}
 WHERE
<#list condition as col>
 ${col.columnName}<@col_type.getOper attribute=col></@col_type.getOper><@col_type.getJdbcType attribute=col ></@col_type.getJdbcType>
 <#if  col_has_next> and</#if>
</#list>
