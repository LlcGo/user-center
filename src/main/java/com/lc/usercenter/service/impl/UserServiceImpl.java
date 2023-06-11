package com.lc.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.service.UserService;
import com.lc.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lc.usercenter.contact.UserContant.USER_LOGIN_STATE;

/**
 *
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值，混淆密码
     */
    private static final String SALT= "LC";

    @Override
    public long register(String userAccount, String userPassword, String confirmPassword,String plantCode) {
        if(StringUtils.isAnyBlank(userAccount,userPassword,confirmPassword,plantCode)){
            return -1;
        }
        if(userAccount.length() < 4){
            return -1;
        }
        if(userPassword.length() < 8){
            return -1;
        }
        if(plantCode.length() > 5){
            return -1;
        }

        //判断账户是否含有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()){
            return -1;
        }

        if(!userPassword.equals(confirmPassword)){
            return -1;
        }

        //查询数据库中是否有重复账户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        Long count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            return -1;
        }
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPlantCode,userAccount);
        count = this.baseMapper.selectCount(queryWrapper);
        if(count > 0){
            return -1;
        }

        //对密码进行加密存入数据库
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //存入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if(!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if(userAccount.length() < 4){
            return null;
        }
        if(userPassword.length() < 8){
            return null;
        }

        //判断账户是否含有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if(matcher.find()) {
            return null;
        }


        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        queryWrapper.eq(User::getUserPassword,encryptPassword);
        User user = this.getOne(queryWrapper);
        if(user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }

        //用户脱敏
        User safeUser = getSafeUser(user);
        //存入session
        request.getSession().setAttribute(USER_LOGIN_STATE,safeUser);

        return safeUser;
    }

    @Override
    public User getSafeUser(User user){
        if(user == null){
            return null;
        }
        User safeUser = new User();
        //这里新增字段别忘了加
        safeUser.setPlantCode(user.getPlantCode());
        safeUser.setId(user.getId());
        safeUser.setUserName(user.getUserName());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setAvatarUrl(user.getAvatarUrl());
        safeUser.setGender(user.getGender());
        safeUser.setPhone(user.getPhone());
        safeUser.setEmail(user.getEmail());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setCreateTime(user.getCreateTime());
        return safeUser;
    }

    @Override
    public int outLogin(HttpServletRequest req) {
        req.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




