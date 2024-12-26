package org.example.podbackend.common.mapper;

import org.example.podbackend.entities.InProgressOrder;
import org.example.podbackend.modules.orders.response.OrderResponse;
import org.example.podbackend.modules.tables.response.TablesFilterResponse;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OrderMapper {

  @Autowired
  private ProductOrderMapper productOrderMapper;

  @Autowired
  private ModelMapper modelMapper;

  @BeforeMapping
  protected void before(InProgressOrder source, @MappingTarget OrderResponse target) {
    if(source.getProductOrders() != null) {
      target.setProducts(source.getProductOrders().stream().map(
              (productOrder) -> productOrderMapper.mapToResponse(productOrder)
      ).toList());
      long totalPrice = target.getProducts().stream().mapToLong(productOrder -> productOrder.getPrice() * productOrder.getQuantity()).sum();
      target.setTotalPrice(totalPrice);

    }
    if(source.getTables() != null) {
      target.setTable(modelMapper.map(source.getTables(), TablesFilterResponse.class));
    }
  }

  public abstract OrderResponse mapToResponse(InProgressOrder source);
  public abstract List<OrderResponse> mapToResponse(List<InProgressOrder> target);
}
