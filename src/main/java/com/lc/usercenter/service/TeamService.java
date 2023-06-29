package com.lc.usercenter.service;

import com.lc.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface TeamService extends IService<Team> {

    boolean saveTeam(Team team, HttpServletRequest request);
}
