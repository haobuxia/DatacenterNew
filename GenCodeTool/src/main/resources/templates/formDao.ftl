package ${package_name}.dao;

import ${package_name}.entity.${table_name};
import org.springframework.stereotype.Repository;
import java.util.List;
/**
* 描述：${table_annotation} Repository接口
* @author ${author}
* @date ${date}
* @version
*/
@Repository
public interface ${table_name}Dao{

    /**
    * 分页查询${table_annotation}
    * @param ${table_name?uncap_first}
    * @return
    *
    * @author ${author}
    * @version
    * */
    List<${table_name}> listBy(${table_name} ${table_name?uncap_first});

    /**
    * 查询${table_annotation}
    * @param ${table_name?uncap_first}
    * @return
    *
    * @author ${author}
    * @version
    * */
    List<${table_name}> listByNoPage(${table_name} ${table_name?uncap_first});

    /**
    * 增加${table_annotation}
    * @param ${table_name?uncap_first}
    * @return
    *
    * @author ${author}
    * @version
    * */
    int add(${table_name} ${table_name?uncap_first});

    /**
    * 删除${table_annotation}
    * @param ${table_name?uncap_first}
    * @return
    *
    * @author ${author}
    * @version
    * */
    int deleteBy(${table_name} ${table_name?uncap_first});

    /**
    * 更新${table_annotation}
    * @param ${table_name?uncap_first}
    * @return
    *
    * @author ${author}
    * @version
    * */
    int updateBy(${table_name} ${table_name?uncap_first});


}