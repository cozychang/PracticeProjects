package com.reggie.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.IdUtils;
import com.reggie.entity.AddressBook;
import com.reggie.entity.OrderDetail;
import com.reggie.entity.Orders;
import com.reggie.entity.ShoppingCart;
import com.reggie.mapper.OrdersMapper;
import com.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Override
    @Transactional
    public void submit(Orders orders) {
        //获取用户当前id
        Long id = IdUtils.getId();
        //查询购物车表单
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, id);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);


        //查询用户数据
        userService.getById(id);

        //查询地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        //往订单表插入数据
        ordersService.save(orders);

        //往订单明细表插入多个数据
        List<OrderDetail> orderDetailList = null;
        BeanUtils.copyProperties(shoppingCartList, orderDetailList);
        orderDetailService.saveBatch(orderDetailList);

        //清空购物车数据
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

    }
}
