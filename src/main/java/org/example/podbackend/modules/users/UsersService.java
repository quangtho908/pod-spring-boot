package org.example.podbackend.modules.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.podbackend.Security.Models.PodUserDetail;
import org.example.podbackend.common.enums.VerifyAction;
import org.example.podbackend.common.exceptions.BadRequestException;
import org.example.podbackend.common.exceptions.NotFoundException;
import org.example.podbackend.common.mapper.UserMerchantMapper;
import org.example.podbackend.entities.UserLogin;
import org.example.podbackend.entities.UserMerchant;
import org.example.podbackend.entities.Users;
import org.example.podbackend.modules.users.DTO.*;
import org.example.podbackend.modules.users.response.SetMerchantResponse;
import org.example.podbackend.modules.users.response.UserCreatedResponse;
import org.example.podbackend.repositories.UserLoginRepository;
import org.example.podbackend.repositories.UserMerchantRepository;
import org.example.podbackend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.Optional;

@Service
public class UsersService {

  private final RedisTemplate<String, Object> redisTemplate;
  private final RedisCacheManager redisCacheManager;
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ModelMapper modelMapper;
  private final UserMerchantRepository userMerchantRepository;
  private final UserLoginRepository userLoginRepository;
  private final UserMerchantMapper userMerchantMapper;

  public UsersService(
          UserRepository userRepository,
          RedisTemplate<String, Object> redisTemplate,
          RedisCacheManager redisCacheManager,
          ModelMapper modelMapper,
          UserMerchantRepository userMerchantRepository,
          UserLoginRepository userLoginRepository,
          UserMerchantMapper userMerchantMapper) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    this.redisTemplate = redisTemplate;
    this.redisCacheManager = redisCacheManager;
    this.modelMapper = modelMapper;
    this.userMerchantRepository = userMerchantRepository;
    this.userLoginRepository = userLoginRepository;
    this.userMerchantMapper = userMerchantMapper;
  }

  public ResponseEntity<UserCreatedResponse> create(CreateUserDTO dto) throws JsonProcessingException {
    Optional<Users> exist = this.userRepository.findByEmailOrPhoneNumber(dto.getEmail().toLowerCase(), dto.getPhoneNumber());
    UserCreatedResponse response = new UserCreatedResponse();
    if (exist.isPresent()) {
      throw new BadRequestException("User already exists");
    }

    dto.setPassword(this.bCryptPasswordEncoder.encode(dto.getPassword()));
    Users userData = modelMapper.map(dto, Users.class);
    Users user = this.userRepository.save(userData);
    modelMapper.map(user, response);
    return ResponseEntity.ok(response);
  }

  public ResponseEntity<Boolean> setPassword(SetPasswordDTO dto) throws JsonProcessingException {
//    Optional<Users> exist = this.userRepository.findById(dto.getId());
//    if (exist.isEmpty()) {
//      throw new NotFoundException("User does not exist");
//    }
//    Users user = exist.get();
//    Cache cache = redisCacheManager.getCache("users_verify");
//    boolean isFirst = user.getPassword() == null || user.getPassword().isEmpty();
//    Cache.ValueWrapper isVerified = cache.get(STR."\{user.getId()}_\{VerifyAction.SET_PASSWORD}");
//    System.out.println(isVerified.get());
//    if(!isFirst && (isVerified == null || !((boolean) isVerified.get()))) {
//      throw new BadRequestException("Set password verification failed");
//    }
//    String hashPassword = this.bCryptPasswordEncoder.encode(dto.getPassword());
//    user.setPassword(hashPassword);
//    this.userRepository.save(user);
//    return ResponseEntity.ok(true);
    return null;
  }

  public ResponseEntity<Boolean> reqVerify(ReqVerifyDTO dto) {
    Cache cache = redisCacheManager.getCache("users_verify");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetails = (PodUserDetail) authentication.getPrincipal();
    cache.put(STR."\{dto.getVerifyAction().toString()}_\{userDetails.getId()}_verifyCode", "000000");
    return new ResponseEntity<>(true, HttpStatus.OK);
  }

  public ResponseEntity<SetMerchantResponse> setMerchant(SetMerchantDTO dto) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) auth.getPrincipal();
    UserMerchant userMerchant = userMerchantRepository.findByUserIdAndMerchantId(userDetail.getId(), dto.getMerchantId());
    if (userMerchant == null) {
      throw new BadRequestException("You do not in merchant");
    }
    UserLogin userLogin = new UserLogin();
    userLogin.setMerchant(userMerchant.getMerchant());
    userLogin.setUser(userMerchant.getUser());
    userLogin.setFcm(dto.getExpoToken());
    this.userLoginRepository.save(userLogin);
    SetMerchantResponse response = userMerchantMapper.toSetMerchantResponse(userMerchant);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public ResponseEntity<Boolean> verify(VerifyDTO dto) {
    Cache cache = redisCacheManager.getCache("users_verify");
    assert cache != null;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetails = (PodUserDetail) authentication.getPrincipal();
    Cache.ValueWrapper verify = cache.get(STR."\{dto.getVerifyAction().toString()}_\{userDetails.getId()}_verifyCode");
    if(verify == null) {
      throw new BadRequestException("Code is expired");
    }
    if(!dto.getCode().equals(verify.get())) {
      throw new BadRequestException("Code is invalid");
    }

    redisTemplate.delete(STR."users_verify::\{dto.getVerifyAction().toString()}_\{userDetails.getId()}_verifyCode");
    cache.put(STR."\{userDetails.getId()}_\{dto.getVerifyAction().toString()}", true);

    return new ResponseEntity<>(true, HttpStatus.OK);
  }
}
