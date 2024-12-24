package org.example.podbackend.modules.tables;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.common.exceptions.NotFoundException;
import org.example.podbackend.entities.Merchant;
import org.example.podbackend.entities.Tables;
import org.example.podbackend.modules.tables.DTO.SetTablesDTO;
import org.example.podbackend.modules.tables.DTO.FilterTablesDTO;
import org.example.podbackend.modules.tables.response.SetCreateResponse;
import org.example.podbackend.modules.tables.response.TablesFilterResponse;
import org.example.podbackend.repositories.MerchantRepository;
import org.example.podbackend.repositories.TableRepository;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TableService {
  private final TableRepository tableRepository;
  private final MerchantRepository merchantRepository;
  private final ObjectMapper objectMapper;
  private final ModelMapper modelMapper;

  public TableService(TableRepository tableRepository, MerchantRepository merchantRepository, ObjectMapper objectMapper, ModelMapper modelMapper) {
    this.tableRepository = tableRepository;
    this.merchantRepository = merchantRepository;
    this.objectMapper = objectMapper;
    this.modelMapper = modelMapper;
  }

  public ResponseEntity<?> filter(Map<String, String> allParam) {
    FilterTablesDTO dto = this.objectMapper.convertValue(allParam, FilterTablesDTO.class);
    if(dto.getMerchantId() == null) {
      throw new NotFoundException("Merchant not found");
    }
    if(dto.getId() != null) {
      Tables table = this.tableRepository.findByIdAndMerchantIdAndIsDeletedIsFalse(dto.getId(), dto.getMerchantId());
      TablesFilterResponse response = new TablesFilterResponse();
      modelMapper.map(table, response);
      return ResponseEntity.ok(response);
    }
    Optional<Merchant> merchant = this.merchantRepository.findById(dto.getMerchantId());
    if(merchant.isEmpty()) {
      throw new NotFoundException("Merchant not found");
    }
    List<Tables> tables = this.tableRepository.filter(dto.getName(), merchant.get());
    List<TablesFilterResponse> responses = tables.stream().map((table) -> modelMapper.map(table, TablesFilterResponse.class)).toList();
    return ResponseEntity.ok(responses);
  }

  public ResponseEntity<SetCreateResponse> create(SetTablesDTO dto) {
    Merchant merchant = this.merchantRepository.findById(dto.getMerchantId()).orElseThrow(() -> new NotFoundException("Merchant not found"));
    Tables tableData = this.modelMapper.map(dto, Tables.class);
    tableData.setMerchant(merchant);
    Tables table = this.tableRepository.save(tableData);
    SetCreateResponse response = modelMapper.map(table, SetCreateResponse.class);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  public ResponseEntity<Boolean> update(Long id, SetTablesDTO dto) {
    Tables table = this.tableRepository.findByIdAndMerchantIdAndIsDeletedIsFalse(id, dto.getMerchantId());
    if(table == null) throw new NotFoundException("Table not found");
    modelMapper.map(dto, table);
    this.tableRepository.save(table);
    return ResponseEntity.ok(true);
  }

  public ResponseEntity<Boolean> delete(Long id, Long merchantId) {
    Tables table = this.tableRepository.findByIdAndMerchantIdAndIsUsedIsFalseAndIsDeletedIsFalse(id, merchantId);
    if(table == null) throw new NotFoundException("Table not found");
    table.setDeleted(true);
    this.tableRepository.save(table);
    return ResponseEntity.ok(true);
  }
}
