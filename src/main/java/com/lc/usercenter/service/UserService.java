package com.lc.usercenter.service;

import com.lc.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface UserService extends IService<User> {


    /**
     * 注册用户
     * @param userAccount 账户
     * @param userPassword 密码
     * @param confirmPassword 校验密码
     * @return
     */
    long register(String userAccount, String userPassword, String confirmPassword,String plantCode);

    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafeUser(User user);

    int outLogin(HttpServletRequest req);
}
