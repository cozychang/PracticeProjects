package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Employee;
import com.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("保存分类中。。。");
        categoryService.save(category);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){

        //构造分页构造器，传入page，pagesize
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器，传入name查询
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        //添加排序条件
        qw.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,qw);

        //返回R
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> delete(Long ids){


        categoryService.remove(ids);
        log.info("删除分类：{}", ids);
        return R.success("删除成功");
    }


    @PutMapping
    public R<String> update (@RequestBody Category category){

        //使用service更新
        categoryService.updateById(category);
        return R.success("修改消息成功");
}

    @GetMapping("/list")
    public R<List<Category>> returnList (Category category){
        //根据type查询category
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(category.getType() != null,
                Category::getType, category.getType());
        //增加排序条件
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(list);

    }



}
