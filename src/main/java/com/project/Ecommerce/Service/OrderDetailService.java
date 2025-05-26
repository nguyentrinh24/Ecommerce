package com.project.Ecommerce.Service;

import com.project.Ecommerce.DTOs.OrderDetailDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.Order;
import com.project.Ecommerce.Model.OrderDetail;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Repository.OrderDetailRepository;
import com.project.Ecommerce.Repository.OrderRepository;
import com.project.Ecommerce.Repository.ProductRepository;
import com.project.Ecommerce.Service.Iml.OrderDetailIml;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service

@RequiredArgsConstructor


public class OrderDetailService implements OrderDetailIml {
    private final OrderRepository  orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelmapper;

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTOs orderDetailDTO) throws Exception {
        //tìm xem orderId có tồn tại ko
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find Order with id : " + orderDetailDTO.getOrderId()));
        // Tìm Product theo id
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + orderDetailDTO.getProductId()));

        com.project.Ecommerce.Model.OrderDetail orderDetail = modelmapper.map(orderDetailDTO,com.project.Ecommerce.Model.OrderDetail.class);
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);

        //lưu vào db
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    @Transactional
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find OrderDetail with id: " + id));
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTOs orderDetailDTO) throws DataNotFoundException {
        //tìm xem order detail có tồn tại ko đã
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Cannot find order detail with id: " + id));
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find order with id: " + id));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + orderDetailDTO.getProductId()));
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());
        existingOrderDetail.setNumberOfProduct(orderDetailDTO.getNumber_of_products());
        // tinh total money
        double total = orderDetailDTO.getPrice() * orderDetailDTO.getNumber_of_products();
        existingOrderDetail.setTotalMoney(total);
        existingOrderDetail.setColor(orderDetailDTO.getColor());
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);


        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<OrderDetail> findByOrderId(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            try {
                throw new DataNotFoundException("No order details found for order id: " + orderId);
            } catch (DataNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return orderDetails;

    }
}
