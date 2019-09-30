package ${package_name}.controller;

import ${package_name}.service.${table_name}Service;
//import ${package_name}.vo.ResponseVo;
import com.tianyi.code.server.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import java.util.List;
/**
* 描述：${table_annotation}控制层
* @author ${author}
* @date ${date}
* @version
*/
@Controller
@RequestMapping("${table_name?uncap_first}")
public class ${table_name}Controller {

    @Autowired
    private ${table_name}Service ${table_name?uncap_first}Service;

    /**
    * 描述：不分页查询列表
    * @param jsonObject
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo listBy(@RequestBody JSONObject jsonObject) {
        ResponseVo responseVo =  ${table_name?uncap_first}Service.listByNoPage(jsonObject);
        if(responseVo != null){
        return ResponseVo.success(responseVo);
        }else{
        return ResponseVo.fail("查询失败");
        }
    }
    /**
      * 描述：分页查询列表
      * @param ${table_name?uncap_first}
      * @return ResponseVo
      *
      * @author ${author}
      * @version

    @RequestMapping(value = "get", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo listBy(@RequestBody ${table_name} ${table_name?uncap_first},Integer page,Integer pageSize) {
        List<${table_name}> ${table_name?uncap_first}List = ${table_name?uncap_first}Service.listBy(${table_name?uncap_first});
        int count = ${table_name?uncap_first}Service.countBy(map);-->
        PageListVo vo = new PageListVo(page);
        if(${table_name?uncap_first}List != null){
            vo.setList(${table_name?uncap_first}List);
            vo.setTotal(count);
            return ResponseVo.success(vo);
        }else{
            return ResponseVo.fail("查询失败");
        }
    }
*/
    /**
    * 描述:新增${table_name}
    * @param jsonObject
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo add(@RequestBody JSONObject jsonObject){
        ResponseVo responseVo =  ${table_name?uncap_first}Service.add(jsonObject);
        if(responseVo != null){
          return ResponseVo.success(responseVo);
        }else{
          return ResponseVo.fail("添加失败");
        }
    }

    /**
    * 描述：删除设备
    * @param jsonObject
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo deleteBy(@RequestBody JSONObject jsonObject){
        ResponseVo responseVo =  ${table_name?uncap_first}Service.deleteBy(jsonObject);
        if(responseVo != null){
          return ResponseVo.success(responseVo);
        }else{
          return ResponseVo.fail("删除失败");
        }
    }

    /**
    * 描述：更新
    * @param jsonObject
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo updateBy(@RequestBody JSONObject jsonObject){
        ResponseVo responseVo =  ${table_name?uncap_first}Service.updateBy(jsonObject);
        if(responseVo != null){
          return ResponseVo.success(responseVo);
        }else{
           return ResponseVo.fail("修改失败");
        }


    }
}