package org.example.caffe.mapper;

import org.example.caffe.domain.Order;
import org.example.caffe.domain.OrderItem;
import org.example.caffe.dto.OrderDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper implements EntityMapper<OrderDto, Order> {

    @Override
    public OrderDto toDto(Order order) {
        if (order == null) return null;

        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setCreatedBy(order.getCreatedBy());
        dto.setCreatedDate(order.getCreatedDate());
        dto.setLastModifiedBy(order.getLastModifiedBy());
        dto.setLastModifiedDate(order.getLastModifiedDate());

        if (order.getOrderItems() != null) {
            dto.setOrderItems(new ArrayList<>(order.getOrderItems()));
        }

        return dto;
    }

    @Override
    public Order toEntity(OrderDto dto) {
        if (dto == null) return null;

        Order order = new Order();
        order.setId(dto.getId());
        order.setCreatedBy(dto.getCreatedBy());
        order.setCreatedDate(dto.getCreatedDate());
        order.setLastModifiedBy(dto.getLastModifiedBy());
        order.setLastModifiedDate(dto.getLastModifiedDate());

        if (dto.getOrderItems() != null) {
            order.setOrderItems(new ArrayList<>(dto.getOrderItems()));
        }

        return order;
    }

    @Override
    public List<OrderDto> toDtoList(List<Order> orders) {
        if (orders == null) return new ArrayList<>();
        List<OrderDto> dtos = new ArrayList<>();
        for (Order order : orders) {
            dtos.add(toDto(order));
        }
        return dtos;
    }

    @Override
    public List<Order> toEntityList(List<OrderDto> dtos) {
        if (dtos == null) return new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        for (OrderDto dto : dtos) {
            orders.add(toEntity(dto));
        }
        return orders;
    }
}
