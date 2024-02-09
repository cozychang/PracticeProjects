package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.DTO.SetmealDto;
import com.reggie.common.CustomerException;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.SetmealMapper;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;


    @Override
    @Transactional
    public void saveWithDish(SetmealDto dto) {
        //保存套餐信息 到setmeal表，插入操作
        this.save(dto);

        //1. 设置菜品的categoryId
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(dto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存套餐和菜品的关系，到setmeal_dish表
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> id) {
        //先查询套餐状态，在售的不可删除
        //select count(*) from setmeal where setmealId in (id) and status = 1
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, id);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(setmealLambdaQueryWrapper);
        if(count > 0){
            throw new CustomerException("套餐正在售卖中，不可删除");
        }


        //对setMeal表操作，进行删除
        this.removeByIds(id);

        //对setMealDish表操作，进行删除
        //delete from setmeal_dish where setmealId in (id)
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, id);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);


    }
}
