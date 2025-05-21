package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.DTOs.OrderDetailDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.OrderDetail;
import com.project.Ecommerce.Service.OrderDetailService;

import java.util.List;

public interface OrderDetailIml {
    OrderDetail createOrderDetail(OrderDetailDTOs newOrderDetail) throws Exception;

    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;

    OrderDetail updateOrderDetail(Long id, OrderDetailDTOs newOrderDetailData)
            throws DataNotFoundException;

    void deleteById(Long id);

    List<OrderDetail> findByOrderId(Long orderId);
}
