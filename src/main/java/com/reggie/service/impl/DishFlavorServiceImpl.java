package com.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.mapper.DishFlavorMapper;
import com.reggie.service.DIshFlavorService;
import com.reggie.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl
        extends ServiceImpl<DishFlavorMapper, DishFlavor>
        implements DIshFlavorService {
}
