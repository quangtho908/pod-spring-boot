package org.example.podbackend.modules.tables;

import jakarta.validation.Valid;
import org.example.podbackend.modules.tables.DTO.SetTablesDTO;
import org.example.podbackend.modules.tables.response.SetCreateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/tables")
@RestController
public class TableController {

  private final TableService tableService;
  public TableController(TableService tableService) {
    this.tableService = tableService;
  }

  @GetMapping()
  public ResponseEntity<?> filterTables(@RequestParam(required = false) Map<String, String> allParam) {
    return this.tableService.filter(allParam);
  }

  @PostMapping()
  public ResponseEntity<SetCreateResponse> create(@RequestBody @Valid SetTablesDTO dto) {
    return this.tableService.create(dto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Boolean> update(@RequestBody @Valid SetTablesDTO dto, @PathVariable Long id) {
    return this.tableService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Boolean> delete(@PathVariable Long id, @RequestParam(name = "merchantId") Long merchantId) {
    return this.tableService.delete(id, merchantId);
  }
}
