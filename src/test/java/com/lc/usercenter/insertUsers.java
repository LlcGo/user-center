package com.lc.usercenter;
import java.util.ArrayList;
import java.util.Date;

import com.lc.usercenter.model.domain.User;
import com.lc.usercenter.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @Author Lc
 * @Date 2023/6/25
 * @Description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class insertUsers {
    @Resource
    private UserService userService;

    @Test
    public void insert(){
        StopWatch stopWatch = new StopWatch();
        final int  NUM = 1000;
        ArrayList<User> users = new ArrayList<>();
        stopWatch.start();
        for (int i = 1; i < NUM ; i++) {
            User user = new User();
            user.setUserName("Ji"+i);
            user.setUserAccount("Ji"+i);
            user.setUserPassword("123456");
            user.setAvatarUrl("http://localhost:8080/api/ikun.jpg");
            user.setGender(0);
            user.setPhone("12316546");
            user.setTags("[]");
            user.setEmail("164"+NUM +"@qq.com");
            user.setUpdateTime(new Date());
            user.setCreateTime(new Date());
            user.setPlantCode("66"+i);
            user.setProfile("666"+i);
            users.add(user);
        }
        stopWatch.stop();
        userService.saveBatch(users,50);
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
