package ${package_name}.entity;
import java.io.Serializable;
import java.util.Date;

/**
* 描述：${table_annotation}模型
* @author ${author}
* @date ${date}
*/
public class ${table_name} implements Serializable{
private static final long serialVersionUID = 1L;
<#if model_column?exists>
  <#list model_column as model>
    /**
    *${model.columnComment!}
    */
    <#if model.columnType = 'VARCHAR' || model.columnType = 'TEXT'|| model.columnType = 'text'|| model.columnType = 'varchar'>
    private String ${model.columnName?uncap_first};

    </#if>
    <#if model.columnType = 'DATE'|| model.columnType = 'DATETIME' || model.columnType = 'datetime'>
    private Date ${model.changeColumnName?uncap_first};

    </#if>
    <#if model.columnType = 'FLOAT' || model.columnType = 'float'>
    private Double ${model.changeColumnName?uncap_first};

    </#if>
    <#if model.columnType = 'INTEGER' || model.columnType = 'INT' || model.columnType = 'TINYINT' || model.columnType = 'int' || model.columnType = 'tinyint'>
    private Integer ${model.changeColumnName?uncap_first};
    </#if>
  </#list>

  <#list model_column as model>
    <#if model.columnType = 'VARCHAR' || model.columnType = 'TEXT'|| model.columnType = 'text'|| model.columnType = 'varchar'>
    public String get${model.changeColumnName}(){
    return ${model.changeColumnName?uncap_first};
    };
    public void set${model.changeColumnName}(String ${model.changeColumnName?uncap_first}){
    this.${model.changeColumnName?uncap_first} = ${model.changeColumnName?uncap_first};
    };

    </#if>
    <#if model.columnType = 'DATE'|| model.columnType = 'DATETIME'|| model.columnType = 'datetime'>
    public Date get${model.changeColumnName}(){
    return ${model.changeColumnName?uncap_first};
    };
    public void set${model.changeColumnName}(Date ${model.changeColumnName?uncap_first}){
    this.${model.changeColumnName?uncap_first} = ${model.changeColumnName?uncap_first};
    };

    </#if>
    <#if model.columnType = 'FLOAT' || model.columnType = 'float'>
    public Double get${model.changeColumnName}(){
    return ${model.changeColumnName?uncap_first};
    };
    public void set${model.changeColumnName}(Double ${model.changeColumnName?uncap_first}){
    this.${model.changeColumnName?uncap_first} = ${model.changeColumnName?uncap_first};
    };

    </#if>
    <#if model.columnType = 'INTEGER' || model.columnType = 'INT' || model.columnType = 'TINYINT' || model.columnType = 'int' || model.columnType = 'tinyint'>
    public Integer get${model.changeColumnName}(){
    return ${model.changeColumnName?uncap_first};
    };
    public void set${model.changeColumnName}(Integer ${model.changeColumnName?uncap_first}){
    this.${model.changeColumnName?uncap_first} = ${model.changeColumnName?uncap_first};
    };
    </#if>
  </#list>
</#if>
}