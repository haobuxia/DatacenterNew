package com.tianyi.datacenter.inspect.controller;

import com.tianyi.datacenter.common.framework.controller.SuperController;
import com.tianyi.datacenter.common.vo.RequestVo;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.config.TianYiConfig;
import com.tianyi.datacenter.inspect.entity.User;
import com.tianyi.datacenter.inspect.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianxujin on 2019/5/13 10:56
 */
@Api("用户操作相关接口")
@RestController
@RequestMapping("user")
public class UserController extends SuperController {

    @Autowired
    RestTemplate template;

    @Autowired
    TianYiConfig tianYiConfig;

    @Autowired
    UserService userService;

    @RequestMapping("hello")
    @ApiIgnore
    public ResponseVo hello(@RequestParam String name) {
        ResponseVo responseVo = null;
        try {
            User user = userService.getById((long) 1);
            Map<String, User> result = new HashMap<>();
            if (user != null) {
                result.put("userInfo", user);
                responseVo = ResponseVo.success(result);
            } else {
                responseVo = ResponseVo.fail("用户不存在");
            }
        } catch (Exception e) {
            responseVo = ResponseVo.fail(e.getMessage());
            logger.error(e.toString());
        }
        return responseVo;
    }

    @ApiOperation(value = "新增操作")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @Transactional(propagation = Propagation.REQUIRED,readOnly = false)
    public ResponseVo add(
            @RequestBody RequestVo requestVo) {
        //userService.save(requestVo);
        return ResponseVo.success();
    }


    @ApiOperation(value = "删除操作")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseVo delete(
           @ApiParam(name = "id",value = "用户id",required = true) @PathVariable Long id) {
       // userService.delete();
        return success();
    }


    @ApiOperation(value = "更新操作")
    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseVo update(
            @RequestBody RequestVo requestVo) {
       // userService.saveOrUpdate(requestVo.getRequest());
        return ResponseVo.success();
    }


    @ApiOperation(value = "查询操作")
    @RequestMapping(value = "/select/{id}", method = RequestMethod.GET)
    public ResponseVo select(
            @ApiParam(name = "id", value = "用户id", required = true) @PathVariable Long id) {
        User user = (User) userService.getById(id);
        if (user==null){
            return fail("查询失败");
        }
        Map map = new HashMap();
        map.put("user", user);
        return success(map);
    }

    @ApiOperation(value = "分页查询操作")
    @RequestMapping(value = "/selectByPage/{page}/{pageSize}", method = RequestMethod.GET)
    public ResponseVo selectByPage(
            @ApiParam(name = "page",value = "当前页",required = true)@PathVariable int page,
            @ApiParam(name = "pageSize",value = "每页显示数量",required = true) @PathVariable int pageSize) {
        ResponseVo responseVo = userService.selectByPage(page, pageSize);
        return responseVo;
    }
}
