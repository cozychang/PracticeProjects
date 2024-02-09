package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.common.R;
import com.reggie.entity.Orders;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrdersService extends IService<Orders> {

    public void submit(Orders orders);
}
