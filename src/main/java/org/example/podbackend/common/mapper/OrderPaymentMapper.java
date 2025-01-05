package org.example.podbackend.common.mapper;

import org.example.podbackend.entities.OrderPayment;
import org.example.podbackend.modules.OrderPayment.response.OrderPaymentResponse;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OrderPaymentMapper {
  @Autowired
  private OrderMapper orderMapper;

  @BeforeMapping
  public void beforeMapping(OrderPayment source, @MappingTarget OrderPaymentResponse target) {
    target.setOrder(orderMapper.mapToResponse(source.getInProgressOrder()));
    target.setPrice(source.getPrice());
  }

  abstract public OrderPaymentResponse mapToResponse(OrderPayment source);
  abstract public List<OrderPaymentResponse> mapToResponses(List<OrderPayment> source);
}
