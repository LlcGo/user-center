package com.lc.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.model.requset.UserLoginRequest;
import com.lc.usercenter.model.requset.UserRegisterRequest;
import com.lc.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.lc.usercenter.contact.UserContant.ADMIN_ROLE;
import static com.lc.usercenter.contact.UserContant.USER_LOGIN_STATE;

/**
 * @Author Lc
 * @Date 2023/4/11
 * @Description
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long register(@RequestBody UserRegisterRequest registerRequest,String plantCode){
        if(registerRequest == null){
            return null;
        }
        String confirmPassword = registerRequest.getConfirmPassword();
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        if(StringUtils.isAnyBlank(confirmPassword,userAccount,userPassword,plantCode)){
            return null;
        }
        return userService.register(userAccount,userPassword,confirmPassword,plantCode);
    }

    @PostMapping("/login")
    public User Login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if(userLoginRequest == null){
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        return userService.userLogin(userAccount,userPassword,request);
    }

    @PostMapping("/outLogin")
    public Integer outLogin(HttpServletRequest request){
        if(request == null){
            return null;
        }
        return userService.outLogin(request);
    }

    @GetMapping("/current")
    public User currentUser(HttpServletRequest req){
        Object o = req.getSession().getAttribute(USER_LOGIN_STATE);
        User currUser = (User)o;
        if(currUser == null){
            return null;
        }
        Long userId = currUser.getId();
        User user = userService.getById(userId);
        return userService.getSafeUser(user);
    }


    @GetMapping("/search")
    public List<User> searchUsers(String name,HttpServletRequest req){
        if (!isAdmin(req)) return new ArrayList<>();

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(name)){
            wrapper.like("userName",name);
        }
        List<User> userList = userService.list(wrapper);
        //用户脱敏
        return userList.stream()
                .map(user -> userService.getSafeUser(user))
                .collect(Collectors.toList());
    }



    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody Long id ,HttpServletRequest req){
        if(!isAdmin(req)){
            return false;
        };
        if(id < 0){
            return false;
        }
        return userService.removeById(id);
    }


    /**
     * 是否为管理员 false 不是  true 是
     * @param req
     * @return
     */
    private boolean isAdmin(HttpServletRequest req) {
        Object o = req.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User)o;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}

