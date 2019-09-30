<#import "MySQL.ftl" as columnInfo>
ALTER TABLE ${dataObject.defined}<#if pkInfo?exists && pkInfo??> DROP PRIMARY KEY,</#if>
<#if addColumns?exists && addColumns?size gt 0>
 <#list addColumns as column>
 ADD COLUMN<@columnInfo.column_definition column=column></@columnInfo.column_definition>
 <#if column_has_next>,</#if>
 <#if alterColumns?exists && alterColumns?size gt 0 >,
 <#elseif dropColumns?exists && dropColumns?size gt 0>,</#if>
 </#list>
</#if>
<#if alterColumns?exists && alterColumns?size gt 0 >
 <#list alterColumns as column>
 CHANGE COLUMN ${column.oldColumnName} <@columnInfo.column_definition column=column></@columnInfo.column_definition>
 <#if column_has_next>,
 <#elseif dropColumns?exists && dropColumns?size gt 0 >,</#if>
 </#list>
</#if>
<#if dropColumns?exists && dropColumns?size gt 0 >
 <#list dropColumns as column>
 DROP COLUMN ${column.columnName}
 <#if column_has_next>,</#if>
 </#list>
</#if>