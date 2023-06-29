package com.lc.usercenter.model.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.lc.usercenter.common.RequestPage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author Lc
 * @Date 2023/6/29
 * @Description
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends RequestPage {
    /**
     * 队伍id
     */
    @TableId
    private Long teamId;

    /**
     * 队伍主人的id
     */
    private Long userId;


    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     *  队伍过期时间
     */
    private Date expireTime;


}
