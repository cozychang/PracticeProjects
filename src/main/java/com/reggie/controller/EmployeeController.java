package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import com.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")//请求的url
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login (@RequestBody Employee employee, HttpServletRequest request){


        //1.比对用户名
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(employeeLambdaQueryWrapper);

        //没查到 返回失败结果
        if(emp == null){
            return R.error("登录失败：查无此用户");
        }

        //2.比对密码
        //2.1 将接收到的密码装维md5加密
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes(StandardCharsets.UTF_8));

        //2.2密码比对，不一样返回失败
        if(!(password.endsWith(emp.getPassword()))){
            return R.error("登陆失败：密码错误");
        }

        //查看员工状态
        if(emp.getStatus() != 1){//1是可使用状态
            return R.error("登陆失败：此用户已被禁止");

        }

        //3.登录成功，将员工id放入Session并返回结果
        request.getSession().setAttribute("employee", emp.getId());

        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清除Session中保存的当前员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }

    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request){
        //给新增员工设置基本信息
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        Long setterId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(setterId);
//        employee.setUpdateUser(setterId);

        //调用service，添加到数据库
        employeeService.save(employee);


        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //构造分页构造器，传入page，pagesize
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器，传入name查询
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        //增加过滤条件
        qw.like(StringUtils.isNotEmpty(name),Employee::getName, name);
        //添加排序条件
        qw.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,qw);

        //返回R
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update (@RequestBody Employee employee, HttpServletRequest request){

        //更新被修改员工更新时间和修改人
//        Long empId =(Long) request.getSession().getAttribute("employee");
//
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());

        //使用service更新
        employeeService.updateById(employee);
        return R.success("修改员工消息成功");
    }

    @GetMapping("/{id}")
    public R<Employee> updateById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);

        if(employee == null){
            return R.error("查无此员工");
        }

        return R.success(employee);
    }
}
