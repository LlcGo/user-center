package com.lc.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lc.usercenter.common.BaseRespons;
import com.lc.usercenter.common.ErrorCode;
import com.lc.usercenter.common.ResponsUtil;
import com.lc.usercenter.exception.BusinessException;
import com.lc.usercenter.model.domain.Team;
import com.lc.usercenter.model.dto.TeamQuery;
import com.lc.usercenter.model.requset.TeamAddRequest;
import com.lc.usercenter.service.TeamService;
import com.lc.usercenter.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import sun.dc.pr.PRError;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.lc.usercenter.contact.UserContant.USER_LOGIN_STATE;

/**
 * @Author Lc
 * @Date 2023/6/28
 * @Description
 */
@RestController
public class TeamController {

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    private BaseRespons<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if(teamAddRequest == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        boolean save = teamService.saveTeam(team,request);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERR,"插入关系表出错");
        }
        return ResponsUtil.success(team.getTeamId());
    }

    @GetMapping("/delete/{id}")
    private BaseRespons<Boolean> deleteTeam(@PathVariable long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.removeById(id);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERR);
        }
        return ResponsUtil.success(true);
    }

    @PostMapping("/update")
    private BaseRespons<Boolean> update(@RequestBody Team team){
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        boolean result = teamService.updateById(team);
        if(!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERR);
        }
        return ResponsUtil.success(true);
    }

    @GetMapping("/select/{id}")
    private BaseRespons<Team> selectOne(@PathVariable long id){
        if(id <= 0){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Team team = teamService.getById(id);
        if(team == null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"没有关于这个id的数据");
        }
        return ResponsUtil.success(team);
    }

    //根据用户查看自己加了多少队伍
    @GetMapping("/list")
    private BaseRespons<List<Team>> listTeams(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        BeanUtils.copyProperties(team,teamQuery);
        List<Team> teamList = teamService.list(queryWrapper);
        if(teamList == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询数据为null");
        }
        return ResponsUtil.success(teamList);
    }

    @GetMapping("/list/page")
    private BaseRespons<Page<Team>> page(TeamQuery teamQuery){
        if(teamQuery == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        long pageSize = teamQuery.getPageSize();
        long pageNum = teamQuery.getPageNum();
        if(pageSize <= 0 || pageNum <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        Page<Team> teamPage = new Page<>(pageNum,pageSize);
        //TODO
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>(team);
        Page<Team> pageTeam = teamService.page(teamPage,teamQueryWrapper);
        if(pageTeam == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有查到数据");
        }
        return ResponsUtil.success(pageTeam);
    }
}
