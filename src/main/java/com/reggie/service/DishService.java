package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.DTO.DishDto;
import com.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto returnById(Long id);

    void updateWithFlavor (DishDto dishDto);
}
