package org.example.podbackend.common.mapper;

import org.example.podbackend.entities.InProgressOrder;
import org.example.podbackend.entities.Product;
import org.example.podbackend.entities.ProductOrder;
import org.example.podbackend.modules.orders.DTO.ProductOrderDTO;
import org.example.podbackend.modules.orders.response.OrderFilterResponse;
import org.example.podbackend.modules.orders.response.ProductOrderResponseDTO;
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
  protected void before(InProgressOrder source, @MappingTarget OrderFilterResponse target) {
    target.setProducts(source.getProductOrders().stream().map(
            (productOrder) -> productOrderMapper.mapToResponse(productOrder)
    ).toList());
    long totalPrice = target.getProducts().stream().mapToLong(productOrder -> productOrder.getPrice() * productOrder.getQuantity()).sum();
    target.setTotalPrice(totalPrice);
    target.setTable(modelMapper.map(source.getTables(), TablesFilterResponse.class));
  }

  public abstract OrderFilterResponse mapToResponse(InProgressOrder source);
  public abstract List<OrderFilterResponse> mapToResponse(List<InProgressOrder> target);
}
