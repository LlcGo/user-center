package com.lc.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.exception.BusinessException;
import com.lc.usercenter.model.domain.Team;
import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.model.domain.UserTeam;
import com.lc.usercenter.model.emus.TeamStatusEnums;
import com.lc.usercenter.service.TeamService;
import com.lc.usercenter.mapper.TeamMapper;
import com.lc.usercenter.service.UserService;
import com.lc.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Date;
import java.util.Optional;

import static com.lc.usercenter.common.ErrorCode.NOT_ROLE;

/**
 *
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserService userService;

    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTeam(Team team, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
            throw new BusinessException(NOT_ROLE,"请用户登录");
        }
        String teamName = team.getTeamName();
        if(StringUtils.isBlank(teamName)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0) ;
        if(maxNum <=1 || maxNum > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置人数因该大于1小于20");
        }
        String description = team.getDescription();
        if(description.length() > 200){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置队伍描述应该小于200");
        }
        int teamStatus = Optional.ofNullable(team.getTeamStatus()).orElse(0);
        TeamStatusEnums enumBystatus = TeamStatusEnums.getEnumBystatus(teamStatus);
        if(enumBystatus == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置队伍状态出错");
        }
        String teamPassword = team.getTeamPassword();
        if(TeamStatusEnums.ENCIPHER.equals(enumBystatus) && StringUtils.isBlank(teamPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请输入密码");
        }
        //如果超时时间大于当前时间
        if(new Date().after(team.getExpireTime())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"设置时间应该大于当前时间");
        }
        //查询用户自己有多少个队伍
        long userId = loginUser.getId();
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getUserId,userId);
        long count = this.count(queryWrapper);
        if(count > 5){
            throw new BusinessException(NOT_ROLE,"创建队伍已超过数量");
        }
        //插入数据前别忘记了给这个队伍设置队伍的id
        team.setUserId(userId);
        boolean TeamSave = this.save(team);
        if(!TeamSave){
            throw new BusinessException(ErrorCode.SYSTEM_ERR);
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(team.getTeamId());
        userTeam.setUserId(userId);
        return userTeamService.save(userTeam);
    }
}




