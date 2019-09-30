<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${package_name}.dao.${table_name}Dao">
    <resultMap id="BaseResultMap" type="${package_name}.entity.${table_name}">
        <id column="id" property="id" jdbcType="INTEGER"/>
    <#if model_column?exists>
        <#list model_column as model>
        <result column="${model.columnName}" property="${model.changeColumnName?uncap_first}" jdbcType="${model.columnType}"/>
        </#list>
    </#if>
    </resultMap>

    <sql id="Base_Column_List">
    <#if model_column?exists>
        <#list model_column as model>
        ${model.columnName?uncap_first}<#if model_has_next>,</#if>
        </#list>
    </#if>
    </sql>
    <sql id="condition1">
        <where>
        <#if model_column?exists>
            <#list model_column as model>

                <#if model.columnType = 'DATE'>
                    <if test="${model.changeColumnName?uncap_first} != null ">
                        and ${model.columnName?uncap_first} = <#noparse>#</#noparse>{${model.changeColumnName?uncap_first},jdbcType=DATE}
                    </if>
                <#elseif model.columnType ='INTEGER'>
                    <if test="${model.changeColumnName?uncap_first} != null ">
                        and ${model.columnName?uncap_first} = <#noparse>#</#noparse>{${model.changeColumnName?uncap_first},jdbcType=INTEGER}
                    </if>
                <#else>
                    <if test="${model.changeColumnName?uncap_first} != null and ${model.changeColumnName?uncap_first} != ''">
                        and ${model.columnName?uncap_first} = <#noparse>#</#noparse>{${model.changeColumnName?uncap_first}}
                    </if>
                </#if>
            </#list>
        </#if>
        </where>
    </sql>
    <sql id="condition2">
    <#if model_column?exists>
        <#list model_column as model>
            <#if model.columnType = 'DATE'>
                <if test="${model.changeColumnName?uncap_first} != null">
                ${model.columnName?uncap_first} = <#noparse>#</#noparse>{${model.changeColumnName?uncap_first},jdbcType=DATE}<#if model_has_next>,</#if>
                </if>
            <#elseif model.columnType ='INTEGER'>
                <if test="${model.changeColumnName?uncap_first} != null">
                ${model.columnName?uncap_first} = <#noparse>#</#noparse>{${model.changeColumnName?uncap_first},jdbcType=INTEGER}<#if model_has_next>,</#if>
                </if>
            <#else>
            <if test="${model.changeColumnName?uncap_first} != null and ${model.changeColumnName?uncap_first} != ''">
               ${model.columnName?uncap_first} = <#noparse>#</#noparse>{${model.changeColumnName?uncap_first}}<#if model_has_next>,</#if>
            </if>
            </#if>


        </#list>
    </#if>
    </sql>
    <sql id="condition3">
        <#if model_column?exists>
            <#list model_column as model>
                <#if model.columnType = 'DATE'>
                    <if test="${model.changeColumnName?uncap_first} != null ">
                        <#noparse>#</#noparse>{${model.changeColumnName?uncap_first},jdbcType=DATE}<#if model_has_next>,</#if>
                    </if>
                <#elseif model.columnType ='INTEGER'>
                    <if test="${model.changeColumnName?uncap_first} != null">
                        <#noparse>#</#noparse>{${model.changeColumnName?uncap_first},jdbcType=INTEGER}<#if model_has_next>,</#if>
                    </if>
                <#else>
                    <if test="${model.changeColumnName?uncap_first} != null and ${model.changeColumnName?uncap_first} != ''">
                        <#noparse>#</#noparse>{${model.changeColumnName?uncap_first}}<#if model_has_next>,</#if>
                    </if>
                </#if>
            </#list>
        </#if>
    </sql>

    <select id="listBy" parameterType="${package_name}.entity.${table_name}" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from ${table_name_small}
        <include refid="condition1"/>
    </select>

    <select id="listByNoPage" parameterType="${package_name}.entity.${table_name}" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List"/>
        from ${table_name_small}
        <include refid="condition1"/>
    </select>

    <insert id="add">
        insert into ${table_name_small}
         (
            <#if model_column?exists>
                <#list model_column as model>
                <#if model.columnType = 'DATE'>
                    <if test="${model.changeColumnName?uncap_first} != null">
                    ${model.columnName?uncap_first}<#if model_has_next>,</#if>
                    </if>
                <#elseif model.columnType ='INTEGER'>
                    <if test="${model.changeColumnName?uncap_first} != null">
                    ${model.columnName?uncap_first}<#if model_has_next>,</#if>
                    </if>
                <#else>
                    <if test="${model.changeColumnName?uncap_first} != null and ${model.changeColumnName?uncap_first} != ''">
                    ${model.columnName?uncap_first}<#if model_has_next>,</#if>
                    </if>
                </#if>

                </#list>
            </#if>
        )
        values
        (<include refid="condition3"/>)
    </insert>

    <update id="updateBy" parameterType="${package_name}.entity.${table_name}">
        update ${table_name_small}
        set
        <include refid="condition2"/>
        where   1=1
        <#if model_column?exists>
            <#list model_column as model>
                <#if model.changeColumnName?uncap_first = 'pltOid' ||  model.changeColumnName?uncap_first == 'id' >
                    and ${model.columnName?uncap_first} = <#noparse>#</#noparse>{${model.changeColumnName?uncap_first}}
                </#if>
            </#list>
        </#if>
    </update>


    <delete id="deleteBy" parameterType="${package_name}.entity.${table_name}">
        DELETE FROM ${table_name_small}
        <include refid="condition1"/>
    </delete>

</mapper>