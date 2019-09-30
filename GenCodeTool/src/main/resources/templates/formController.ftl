package ${package_name}.controller;

import ${package_name}.service.${table_name}Service;
import ${package_name}.entity.${table_name};
//import ${package_name}.vo.ResponseVo;
import com.tianyi.code.server.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

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
    * @param ${table_name?uncap_first}
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "list", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo listBy(@RequestBody ${table_name} ${table_name?uncap_first}) {
        List<${table_name}> ${table_name?uncap_first}List = ${table_name?uncap_first}Service.listByNoPage(${table_name?uncap_first});
        if(${table_name?uncap_first}List != null){
            return ResponseVo.success(${table_name?uncap_first}List);
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
    * @param ${table_name?uncap_first}
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo add(@RequestBody ${table_name} ${table_name?uncap_first}){
        int rs = ${table_name?uncap_first}Service.add(${table_name?uncap_first});
        if(rs > 0){
            return ResponseVo.success();
        }
        return ResponseVo.fail("添加失败");
    }

    /**
    * 描述：删除设备
    * @param ${table_name?uncap_first}
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo deleteBy(@RequestBody ${table_name} ${table_name?uncap_first}){
        int rs = ${table_name?uncap_first}Service.deleteBy(${table_name?uncap_first});
        if(rs > 0){
            return ResponseVo.success();
        }
        return ResponseVo.fail("删除失败");
    }

    /**
    * 描述：更新
    * @param ${table_name?uncap_first}
    * @return ResponseVo
    *
    * @author ${author}
    * @version
    */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseVo updateBy(@RequestBody ${table_name} ${table_name?uncap_first}){
        int rs = ${table_name?uncap_first}Service.updateBy(${table_name?uncap_first});
        if(rs > 0){
            return ResponseVo.success();
        }
        return ResponseVo.fail("修改失败");
    }
}