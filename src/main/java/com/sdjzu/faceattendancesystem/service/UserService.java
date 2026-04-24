package com.sdjzu.faceattendancesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sdjzu.faceattendancesystem.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     */
    User login(String username, String password);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 注册用户
     */
    boolean register(User user);

    /**
     * 分页查询用户列表
     */
    Page<User> pageList(int current, int size);

    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
}
