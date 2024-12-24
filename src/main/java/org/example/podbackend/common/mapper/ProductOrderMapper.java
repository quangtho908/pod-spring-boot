package org.example.podbackend.common.mapper;

import org.example.podbackend.entities.ProductOrder;
import org.example.podbackend.modules.orders.response.ProductOrderResponseDTO;
import org.mapstruct.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProductOrderMapper {
  @Autowired
  private ModelMapper modelMapper;

  @BeforeMapping
  protected void before(ProductOrder source, @MappingTarget ProductOrderResponseDTO target) {
    modelMapper.map(source.getProduct(), target);
  }

  @Mapping(target = "id", ignore = true)
  public abstract ProductOrderResponseDTO mapToResponse (ProductOrder source);
}
