<#import "MySQL.ftl" as col_type>

INSERT INTO ${dataObject.defined}
 (plt_creator,plt_createtime,plt_lastmodifier,plt_lastmodifytime,
<#list insertInfo as column>
 ${column.columnName}<#if column_has_next>,</#if>
</#list>
 ) VALUES (${dataObject.pltCreator},'${dataObject.pltCreatetime}',${dataObject.pltLastmodifier},'${dataObject.pltLastmodifytime}',
<#list insertInfo as column>
    <@col_type.getJdbcType attribute=column ></@col_type.getJdbcType> <#if column_has_next>, </#if>
</#list>
 )