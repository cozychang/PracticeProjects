package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.DTO.SetmealDto;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    public void saveWithDish(SetmealDto dto);

    public void removeWithDish(List<Long> id);
}
