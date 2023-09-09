package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;


    //每分钟查看一次
    @Scheduled(cron = "* 1 * * * ?")
    public void executeOrder(){
       //处理未支付15分钟后自动取消的订单
            //获取当前时间-15分钟用于查出超时订单
            LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
            List<Orders> ordersList = orderMapper.processOutTimeOrder(Orders.UN_PAID, time);
            if(ordersList != null && ordersList.size() >0){
                //遍历超时订单后设置取消状态和其他属性
                for (Orders order : ordersList) {
                    order.setStatus(Orders.CANCELLED);
                    order.setCancelTime(LocalDateTime.now());
                    order.setCancelReason("订单超时，自动取消");
                    orderMapper.update(order);
                }
            }

    }

    //每晚1点查看一次
    @Scheduled(cron = "0 0 1 * * ?")
    public void executeOrder1(){
        //处理订单到货后未及时完成的订单
            //当前时间-60分钟查出一天前的订单
            LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
            List<Orders> ordersList = orderMapper.processOutTimeOrder(Orders.DELIVERY_IN_PROGRESS, time);
            //遍历超时订单后设置取消状态和其他属性
            if(ordersList != null && ordersList.size() >0){
                //遍历超时订单后设置取消状态和其他属性
                for (Orders order : ordersList) {
                    order.setStatus(Orders.CANCELLED);
                    order.setCancelTime(LocalDateTime.now());
                    order.setCancelReason("订单超时，自动取消");
                    orderMapper.update(order);
                }
            }
    }


}
