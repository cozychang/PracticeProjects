package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.DTO.DishDto;
import com.reggie.DTO.SetmealDto;
import com.reggie.common.R;
import com.reggie.entity.*;
import com.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Utilities;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DIshFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @RequestMapping
    @CacheEvict(value = "setmeal", allEntries = true)
    public R<String> save (@RequestBody SetmealDto dto){
        setmealService.saveWithDish(dto);
        return R.success("保存成功");
    }

    @RequestMapping("/page")
    public R<Page> page(int pageSize, int page, String name){
        Page<Setmeal> pageInfo = new Page<>();
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //根据name进行like查询
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);


        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> dtoPageRecords = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            Category category = categoryService.getById(item.getCategoryId());
            setmealDto.setCategoryName(category.getName());

            BeanUtils.copyProperties(item,setmealDto);
            return setmealDto;

        }).collect(Collectors.toList());

        dtoPage.setRecords(dtoPageRecords);

        return R.success(dtoPage);
    }

    @DeleteMapping
    @CacheEvict(value = "setmeal", allEntries = true)
    public R<String> remove(@RequestParam List<Long> id){
        log.info("removeIds: {}", id);
        setmealService.removeWithDish(id);
        return R.success("套餐删除成功");
    }


    //根据传过来的id和status，返回套餐
    @RequestMapping("/list")
    @Cacheable(value = "setmeal", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, "1");
        setmealLambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(setmealLambdaQueryWrapper);

        return R.success(list);

    }
}
