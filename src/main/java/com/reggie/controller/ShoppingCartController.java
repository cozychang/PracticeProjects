package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.ShoppingCart;
import com.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){

        log.info("购物车！！！！");

        Long userId = 1L;
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> list = shoppingCartService.list(wrapper);



        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        log.info("添加物品信息：{}", shoppingCart);

        //利用线程获取用户id，设置用户id
        Long userId = 1L;
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUserId(userId);
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        Long foodId = shoppingCart.getDishId();

        //对比userId，foodId，，查询是否已添加在购物车中
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId());

        //判断是菜品还是套餐
        if(foodId == null){
            //是套餐
            foodId = shoppingCart.getSetmealId();
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getSetmealId, foodId);
            newCart.setSetmealId(foodId);
        }else {
            shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getDishId, foodId);
            newCart.setDishId(foodId);
        }



        ShoppingCart shoppingCart1 = shoppingCartService.getOne(shoppingCartLambdaQueryWrapper);
        if(shoppingCart1 != null){
            //已添加，number +1
            shoppingCart1.setNumber(shoppingCart1.getNumber() + 1);
            shoppingCart1.setAmount(shoppingCart.getAmount());
            shoppingCartService.updateById(shoppingCart1);
            return R.success(shoppingCart1);
        }else {
            //新增记录
            newCart.setAmount(shoppingCart.getAmount());
            shoppingCartService.save(newCart);
            return R.success(newCart);
        }

    }


}
