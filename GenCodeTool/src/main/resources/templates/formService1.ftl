package ${package_name}.service;

import com.alibaba.fastjson.JSONObject;
import com.tianyi.code.server.vo.ResponseVo;
import java.util.List;
/**
* 描述：${table_annotation} 服务实现层接口
* @author ${author}
* @date ${date}
* @version
*/
public interface ${table_name}Service{

    /**
     * 分页查询${table_annotation}
     * @param jsonObject
     * @return
     *
     * @author ${author}
     * @version
     * */
    ResponseVo listBy(JSONObject jsonObject);
    /**
    * 不分页查询${table_annotation}
    * @param jsonObject
    * @return
    *
    * @author ${author}
    * @version
    * */
     ResponseVo listByNoPage(JSONObject jsonObject);
    /**
    * 增加${table_annotation}
    * @param jsonObject
    * @return
    *
    * @author ${author}
    * @version
    * */
    ResponseVo add(JSONObject jsonObject);
    /**
    * 删除${table_annotation}
    * @param jsonObject
    * @return
    *
    * @author ${author}
    * @version
    * */
    ResponseVo deleteBy(JSONObject jsonObject);
    /**
    * 更新${table_annotation}
    * @param jsonObject
    * @return
    *
    * @author ${author}
    * @version
    * */
    ResponseVo updateBy(JSONObject jsonObject);

    <#--int countBy();-->

}