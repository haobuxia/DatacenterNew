package com.tianyi.datacenter.inspect.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tianyi.datacenter.common.vo.PageListVo;
import com.tianyi.datacenter.common.vo.ResponseVo;
import com.tianyi.datacenter.inspect.dao.UserDao;
import com.tianyi.datacenter.inspect.entity.User;
import com.tianyi.datacenter.inspect.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据对象服务实现
 *
 * @author wenxinyan
 * @version 0.1
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserDao,User> implements UserService {

    @Autowired
     private UserDao userDao;



    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public ResponseVo selectByPage(int page, int pageSize) {
        QueryWrapper queryWrapper = new QueryWrapper<User>();
        Page<User> pages = new Page<User>(page,pageSize);
        IPage<User> iPage = userDao.selectPage(pages, queryWrapper);
        PageListVo pageListVo = new PageListVo();

        pageListVo.setPageSize((int) iPage.getPages());
        pageListVo.setTotal((int) iPage.getTotal());

        Map<Object, Object> map = new HashMap<>();
        map.put("pageList",pageListVo);
        map.put("list",iPage.getRecords());

        ResponseVo responseVo = ResponseVo.success(map);
        return responseVo;
    }



}
