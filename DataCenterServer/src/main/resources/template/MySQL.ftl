<#macro getJdbcType attribute={}>
    <#assign  value="null">
    <#if attribute.value?exists && attribute.value??>
        <#assign value=attribute.value>
        <#if attribute.type?exists>
            <#if attribute.type=="文本" >
                <#if attribute.oper?exists && attribute.oper=="like">'%${value}%'
                <#else>'${value}'
                </#if>
            <#elseif attribute.type=="数字">${value}
            <#else >'${value}'
            </#if>
        <#else >'${value}'
        </#if>
    <#else>
        ${value}
    </#if>
</#macro>

<#macro getOper attribute={}>
    <#if !attribute.oper?exists || attribute.oper=="equals"> =
    <#elseif attribute.oper=="greate than"> >
    <#elseif attribute.oper=="greate than equals"> >=
    <#elseif attribute.oper=="less than"> <
    <#elseif attribute.oper=="less than equals"> <=
    <#elseif attribute.oper=="like"> like
    <#else> =
    </#if>
</#macro>

<#macro column_definition column={}>
    ${column.columnName} <#if column.jdbcType=="datetime">varchar<#else>${column.jdbcType}</#if>
    <#if column.length gt 0>(${column.length}<#if column.jdbcType=="double">,4</#if>)</#if>
    <#if column.isNull?exists && column.isNull=="true"> NULL<#else> NOT NULL</#if>
    <#if column.isIncrement?exists && column.isIncrement=="true"> AUTO_INCREMENT</#if>
    <#if column.isKey?exists && column.isKey=="true"> UNIQUE KEY</#if>
    <#if column.description?exists> COMMENT '${column.description}'</#if>
</#macro>

<#macro column_default>
plt_oid int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '唯一标识',
plt_creator int(11) NULL COMMENT '创建人员',
plt_createtime datetime NULL COMMENT '创建时间',
plt_lastmodifier int(11) NULL COMMENT '修改人员',
plt_lastmodifytime datetime NULL COMMENT '修改时间'
</#macro>