package org.example.podbackend.common.mapper;

import org.example.podbackend.entities.Merchant;
import org.example.podbackend.modules.merchants.response.MerchantFilterResponse;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public abstract class MerchantMapper {
  @BeforeMapping
  protected void before(Merchant source, @MappingTarget MerchantFilterResponse target) {
    String role = String.valueOf(Objects.requireNonNull(source.getUserMerchants().stream().findFirst().orElse(null)).getRole());
    target.setRole(role);
  };

  public abstract MerchantFilterResponse mapToResponse(Merchant source);

  public abstract Merchant fromResponseToSource(MerchantFilterResponse target);

  public abstract List<MerchantFilterResponse> mapToResponse(List<Merchant> source);
}
