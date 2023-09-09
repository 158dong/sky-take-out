package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    PageResult pageQuery4User(int page, int pageSize, Integer status);

    OrderVO detailOrder(Long id);

    void userCancelById(Long id) throws Exception;

    void repetition(Long id);

    /**
     * 条件搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return
     */
    OrderStatisticsVO statistics();

    void orderConfirm(OrdersConfirmDTO ordersConfirmDTO);

    void orderRejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception;

    void orderCancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    void orderDelivery(Long id);

    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);

    void reminder(Long id);
}
