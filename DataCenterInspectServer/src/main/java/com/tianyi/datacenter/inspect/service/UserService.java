package com.tianyi.datacenter.inspect.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.entity.User;

/**
 * 数据对象服务接口
 *
 * @author wenxinyan
 * @version 0.1
 */
public interface UserService  extends IService<User> {



    ResponseVo selectByPage(int page,int pageSize);

}
