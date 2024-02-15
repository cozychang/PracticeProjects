package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SpringRedisTemplate





    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        //获取手机号
        String phone = user.getPhone();

        //验证手机号是否为空
        if(phone != null){
            //利用工具类，随机生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode4String(4);

            //将验证码保存在session，以后面验证
            //session.setAttribute("code",code);

            //发送短信
            //SMSUtils.sendMessage("瑞吉外卖","", phone, code);
            log.info("code：{}", code);

            return R.success("短信发送成功");

        }

        return R.error("发送短信失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map user, HttpSession session){
        //获取手机号
        //获取传进来的code
        //对比code和session里的code
        //比对成功，通过
        //检查是否为新用户
        //是，自动存入数据库

        String phone = user.get("phone").toString();

        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(phone != null, User::getPhone, phone);
        User theUser = userService.getOne(userLambdaQueryWrapper);

        if(theUser == null){
            User user1 = new User();
            user1.setPhone(phone);
            userService.save(user1);
            theUser = user1;
        }

        session.setAttribute("user", theUser.getId());

        return R.success(theUser);
    }

}
