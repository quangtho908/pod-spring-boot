package org.example.podbackend.common.mapper;

import org.example.podbackend.entities.BankAccount;
import org.example.podbackend.modules.bank.DTO.BankDTO;
import org.example.podbackend.modules.bankAccount.response.BankAccountResponse;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BankAccountMapper {

  @Autowired
  private ModelMapper modelMapper;

  @BeforeMapping
  public void beforeMapping(BankAccount bankAccount, @MappingTarget BankAccountResponse bankAccountResponse) {
    BankDTO bankDTO = modelMapper.map(bankAccount, BankDTO.class);
    bankAccountResponse.setBank(bankDTO);
  }

  public abstract BankAccountResponse mapToResponse(BankAccount bankAccount);
}
