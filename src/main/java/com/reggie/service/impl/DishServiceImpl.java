package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.DTO.DishDto;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishMapper;
import com.reggie.service.DIshFlavorService;
import com.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
implements DishService {


    @Autowired
    private DIshFlavorService dishFlavorService;

    @Transactional
    public void saveWithFlavor (DishDto dishDto){
        //将菜品信息保存到菜品表
        this.save(dishDto);
        Long id = dishDto.getId();


        //循环遍历，给flavor附上菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item) ->{
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());

        //将口味信息保存到口味表
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto returnById(Long id){
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> eq = new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(eq);

        dishDto.setFlavors(flavors);


        return dishDto;
    }

    @Override
    public void updateWithFlavor (DishDto dishDto){
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDto,dish );
        this.saveOrUpdate(dish);

        List<DishFlavor> dishFlavors = new ArrayList<>();
        List<DishFlavor> flavors = dishDto.getFlavors();
        BeanUtils.copyProperties(flavors, dishFlavors);
        dishFlavorService.saveOrUpdateBatch(dishFlavors);
    }




}
