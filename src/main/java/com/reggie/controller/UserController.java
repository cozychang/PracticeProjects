package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;





    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){

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

            //保存到redis里
            redisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            log.info("code：{}", code);

            return R.success("短信发送成功");

        }

        return R.error("发送短信失败");
    }


    @PostMapping("/login")
    public R<User> login(@RequestBody Map user, HttpSession session){
        //获取手机号
        //获取传进来的code



        String phone = user.get("phone").toString();

        String code = user.get("code").toString();

        Object codeInRedis = redisTemplate.opsForValue().get(phone);


        //对比code和redis里的code
        //比对成功，通过
        if(!code.equals(codeInRedis) ){
            log.info("验证码错误");
            return R.error("验证码错误");
        }

        //检查是否为新用户
        //是，自动存入数据库
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

        //登录成功，删除redis中的验证码
        redisTemplate.delete(phone);

        return R.success(theUser);
    }

}
