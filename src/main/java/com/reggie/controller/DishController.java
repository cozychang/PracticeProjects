package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.DTO.DishDto;
import com.reggie.common.R;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.DishFlavor;
import com.reggie.service.CategoryService;
import com.reggie.service.DIshFlavorService;

import com.reggie.service.DishService;
import com.reggie.service.impl.DishServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DIshFlavorService dIshFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DIshFlavorService dishFlavorService;

    @PostMapping
    public R<String> save (@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        log.info(dishDto.toString());

        return R.success("保存成功！");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        Page<Dish> dishPage = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(dishPage, dishLambdaQueryWrapper);

        //由于dishDto没有表，不能进行查询
        //而此时dishPage中已经有所需数据了
        //所以将dishPage的数据拷贝到dishDtoPage中

        //需要将dish中的categoryId拿出来，通过其查询到对应的categoryName，再保存到dishDto中
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> dishPageRecords = dishPage.getRecords();

        List<DishDto> dishDtoRecords = dishPageRecords.stream().map((item)->{
            //将其余属性拷上去
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);

            //item没有categoryName，所以dishDto的categoryName还是空的

            Long categoryId = item.getCategoryId();

            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }


            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoRecords);


        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get (@PathVariable Long id){
        DishDto dishDto = dishService.returnById(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update (@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        log.info(dishDto.toString());

        return R.success("保存成功！");
    }


//    //根据传过来的菜品数据进行查询，返回数据
//    @RequestMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
//        LambdaQueryWrapper<Dish> eq = dishLambdaQueryWrapper.eq(dish.getCategoryId() != null,
//                Dish::getCategoryId, dish.getCategoryId());
//        //添加查询条件：正在售卖中的菜品，状态status == 1
//        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
//        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(eq);
//
//        return R.success(list);
//
//    }
        //根据传过来的菜品数据进行查询，返回菜品和口味
        @RequestMapping("/list")
        public R<List<DishDto>> list(Dish dish){
            LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            LambdaQueryWrapper<Dish> eq = dishLambdaQueryWrapper.eq(dish.getCategoryId() != null,
                    Dish::getCategoryId, dish.getCategoryId());
            //添加查询条件：正在售卖中的菜品，状态status == 1
            dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
            dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
            List<Dish> list = dishService.list(eq);



            List<DishDto> dishDtoList = list.stream().map((item)->{
                //将其余属性拷上去
                DishDto dishDto = new DishDto();
                BeanUtils.copyProperties(item, dishDto);

                //dishDto的flavor还是空的
                Long id = item.getId();


                LambdaQueryWrapper<DishFlavor> flavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
                flavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
                List<DishFlavor> dishFlavorList = dishFlavorService.list(flavorLambdaQueryWrapper);

               dishDto.setFlavors(dishFlavorList);

                return dishDto;

            }).collect(Collectors.toList());




            return R.success(dishDtoList);

        }


}
