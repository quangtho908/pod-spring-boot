package org.example.podbackend.modules.merchants;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.common.exceptions.NotFoundException;
import org.example.podbackend.common.mapper.MerchantMapper;
import org.example.podbackend.entities.Merchant;
import org.example.podbackend.entities.UserMerchant;
import org.example.podbackend.entities.Users;
import org.example.podbackend.modules.merchants.DTO.CreateMerchantDTO;
import org.example.podbackend.modules.merchants.DTO.FilterMerchantDTO;
import org.example.podbackend.modules.merchants.DTO.UpdateMerchantDTO;
import org.example.podbackend.modules.merchants.response.MerchantCreateResponse;
import org.example.podbackend.modules.merchants.response.MerchantFilterResponse;
import org.example.podbackend.repositories.MerchantRepository;
import org.example.podbackend.repositories.UserMerchantRepository;
import org.example.podbackend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class MerchantService {
  private final MerchantRepository merchantRepository;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final UserMerchantRepository userMerchantRepository;
  private final ModelMapper modelMapper;
  private final MerchantMapper merchantMapper;

  public MerchantService(
          MerchantRepository merchantRepository,
          ObjectMapper objectMapper,
          UserRepository userRepository,
          UserMerchantRepository userMerchantRepository,
          ModelMapper modelMapper,
          MerchantMapper merchantMapper) {
    this.merchantRepository = merchantRepository;
    this.objectMapper = objectMapper;
    this.userRepository = userRepository;
    this.userMerchantRepository = userMerchantRepository;
    this.modelMapper = modelMapper;
    this.merchantMapper = merchantMapper;
  }

  public ResponseEntity<?> filter(Map<String, String> filters) {
    FilterMerchantDTO dto = this.objectMapper.convertValue(filters, FilterMerchantDTO.class);
    if(dto.getId() != null) {
      Optional<Merchant> merchant = merchantRepository.findById(dto.getId());
      if(merchant.isEmpty()) throw new NotFoundException("Merchant not found");
      MerchantFilterResponse response = merchantMapper.mapToResponse(merchant.get());
      return ResponseEntity.ok(response);
    }
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetails = (PodUserDetail) authentication.getPrincipal();
    List<Merchant> merchants = merchantRepository.filter(userDetails.getId(), Pageable.unpaged());
    List<MerchantFilterResponse> response = merchantMapper.mapToResponse(merchants);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<MerchantCreateResponse> create(CreateMerchantDTO createMerchantDTO) {
    Merchant exist = this.merchantRepository.findByName(createMerchantDTO.getName());
    if (exist != null) {
      throw new BadRequestException("Merchant already exists");
    }

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetails = (PodUserDetail) authentication.getPrincipal();
    Optional<Users> existUser = this.userRepository.findById(userDetails.getId());
    if (existUser.isEmpty()) {
      throw new BadRequestException("User does not exists");
    }
    Users user = existUser.get();

    Merchant merchantModel = this.objectMapper.convertValue(createMerchantDTO, Merchant.class);
    merchantModel.setPhoneNumber(user.getPhoneNumber());
    Merchant merchant = this.merchantRepository.save(merchantModel);

    _createUserMerchant(merchant, user);

    MerchantCreateResponse response = new MerchantCreateResponse();
    response.setMerchantId(merchant.getId());
    response.setMerchantName(merchant.getName());
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<Boolean> update(UpdateMerchantDTO updateMerchantDTO, Long merchantId) {
    Optional<Merchant> exist = this.merchantRepository.findById(merchantId);

    if (exist.isEmpty()) {
      throw new BadRequestException("Merchant does not exists");
    }
    Merchant merchant = exist.get();
    modelMapper.map(updateMerchantDTO, merchant);
    this.merchantRepository.save(merchant);

    return ResponseEntity.ok(true);
  }

  private void _createUserMerchant(Merchant merchant, Users user) {
    UserMerchant userMerchant = new UserMerchant();
    userMerchant.setUser(user);
    userMerchant.setMerchant(merchant);
    this.userMerchantRepository.save(userMerchant);
  }
}