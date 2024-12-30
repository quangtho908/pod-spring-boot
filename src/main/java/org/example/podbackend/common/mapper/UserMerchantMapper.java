package org.example.podbackend.common.mapper;

import org.example.podbackend.entities.UserMerchant;
import org.example.podbackend.modules.merchants.response.UserMerchantResponse;
import org.example.podbackend.modules.users.response.SetMerchantResponse;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMerchantMapper {
  @Autowired
  private ModelMapper modelMapper;

  @BeforeMapping()
  public void beforeMapping(UserMerchant userMerchant, @MappingTarget UserMerchantResponse userMerchantResponse) {
    modelMapper.map(userMerchant.getUser(), userMerchantResponse);
  }

  public abstract UserMerchantResponse toUserMerchantResponse(UserMerchant userMerchant);

  public abstract SetMerchantResponse toSetMerchantResponse(UserMerchant userMerchant);
}
