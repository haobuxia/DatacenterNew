package ${package_name}.service.impl;

import ${package_name}.entity.${table_name};
import ${package_name}.service.${table_name}Service;
import ${package_name}.dao.${table_name}Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
* 描述：${table_annotation} 服务实现层
* @author ${author}
* @date ${date}
* @version
*/
@Service
public class ${table_name}ServiceImpl implements ${table_name}Service {

    @Autowired
    private ${table_name}Dao ${table_name?uncap_first}Dao;

    @Override
    public List<${table_name}> listBy(${table_name} ${table_name?uncap_first}){
        List<${table_name}> ${table_name?uncap_first}List = ${table_name?uncap_first}Dao.listBy(${table_name?uncap_first});
        return ${table_name?uncap_first}List;
    }

    @Override
    public List<${table_name}> listByNoPage(${table_name} ${table_name?uncap_first}){
        List<${table_name}> ${table_name?uncap_first}List = ${table_name?uncap_first}Dao.listByNoPage(${table_name?uncap_first});
        return ${table_name?uncap_first}List;
    }

    @Override
    public int add(${table_name} ${table_name?uncap_first}){
        int rs = ${table_name?uncap_first}Dao.add(${table_name?uncap_first});
        return rs;
    }
    @Override
    public int deleteBy(${table_name} ${table_name?uncap_first}){
        int rs = ${table_name?uncap_first}Dao.deleteBy(${table_name?uncap_first});
        return rs;
    }

    @Override
    public int updateBy(${table_name} ${table_name?uncap_first}){
        int rs = ${table_name?uncap_first}Dao.updateBy(${table_name?uncap_first});
        return rs;
    }
}
