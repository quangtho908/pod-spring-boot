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
import org.example.podbackend.modules.merchants.response.UserMerchantResponse;
import org.example.podbackend.modules.users.DTO.*;
import org.example.podbackend.modules.users.response.SetMerchantResponse;
import org.example.podbackend.modules.users.response.UserCreatedResponse;
import org.example.podbackend.modules.users.response.UserResponse;
import org.example.podbackend.repositories.UserLoginRepository;
import org.example.podbackend.repositories.UserMerchantRepository;
import org.example.podbackend.repositories.UserRepository;
import org.example.podbackend.utils.CloudinaryService;
import org.example.podbackend.utils.MultiPartHandle;
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


import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
  private final MultiPartHandle multiPartHandle;
  private final CloudinaryService cloudinaryService;

  public UsersService(
          UserRepository userRepository,
          RedisTemplate<String, Object> redisTemplate,
          RedisCacheManager redisCacheManager,
          ModelMapper modelMapper,
          UserMerchantRepository userMerchantRepository,
          UserLoginRepository userLoginRepository,
          UserMerchantMapper userMerchantMapper, MultiPartHandle multiPartHandle, CloudinaryService cloudinaryService) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    this.redisTemplate = redisTemplate;
    this.redisCacheManager = redisCacheManager;
    this.modelMapper = modelMapper;
    this.userMerchantRepository = userMerchantRepository;
    this.userLoginRepository = userLoginRepository;
    this.userMerchantMapper = userMerchantMapper;
    this.multiPartHandle = multiPartHandle;
    this.cloudinaryService = cloudinaryService;
  }

  public ResponseEntity<Boolean> setAvatar(SetAvatarDTO dto) throws IOException {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) auth.getPrincipal();
    Optional<Users> users = this.userRepository.findById(userDetail.getId());
    if (users.isEmpty()) {
      throw new BadRequestException("User not found");
    }
    Users user = users.get();
    if(dto.getImage() != null && !dto.getImage().isEmpty()) {
      String localUrl = multiPartHandle.handle(dto.getImage());
      String url = cloudinaryService.upload(localUrl, "avatars");
      if(user.getAvatar() != null && !user.getAvatar().isEmpty()) {
        cloudinaryService.delete(user.getAvatar());
      }
      user.setAvatar(url);
      this.userRepository.save(user);
      multiPartHandle.delete(localUrl);
    }
    return ResponseEntity.ok(true);
  }

  public ResponseEntity<UserResponse> getUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) authentication.getPrincipal();
    Optional<Users> user = userRepository.findById(userDetail.getId());
    if(user.isEmpty()) {
      throw new BadRequestException("User not exist");
    }
    UserResponse userResponse = modelMapper.map(user.get(), UserResponse.class);
    return ResponseEntity.ok(userResponse);
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
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) authentication.getPrincipal();
    Optional<Users> exist = this.userRepository.findById(userDetail.getId());
    if (exist.isEmpty()) {
      throw new NotFoundException("User does not exist");
    }
    Users user = exist.get();
    Cache cache = redisCacheManager.getCache("users_verify");
    Cache.ValueWrapper isVerified = cache.get(STR."\{user.getId()}_\{VerifyAction.SET_PASSWORD}");
    if(isVerified == null || !(boolean) isVerified.get()) {
      throw new BadRequestException("Set password verification failed");
    }

    String hashPassword = this.bCryptPasswordEncoder.encode(dto.getPassword());
    user.setPassword(hashPassword);
    this.userRepository.save(user);
    cache.evict(STR."\{user.getId()}_\{VerifyAction.SET_PASSWORD}");
    return ResponseEntity.ok(true);
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
    UserLogin exist = this.userLoginRepository.findByUserIdAndMerchantId(userDetail.getId(), dto.getMerchantId());
    deActiveLogin(userDetail.getId());
    if (exist == null) {
      UserLogin userLogin = new UserLogin();
      userLogin.setMerchant(userMerchant.getMerchant());
      userLogin.setUser(userMerchant.getUser());
      userLogin.setFcm(dto.getExpoToken());
      this.userLoginRepository.save(userLogin);
    }else {
      exist.setActive(true);
      this.userLoginRepository.save(exist);
    }
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
    cache.evict(STR."\{dto.getVerifyAction().toString()}_\{userDetails.getId()}_verifyCode");
    cache.put(STR."\{userDetails.getId()}_\{dto.getVerifyAction().toString()}", true);
    if(dto.getVerifyAction().compareTo(VerifyAction.ACTIVE_ACCOUNT) == 0) {
      activeAccount(userDetails.getId());
    }
    return new ResponseEntity<>(true, HttpStatus.OK);
  }

  private void activeAccount(long userId) {
    Users users = this.userRepository.findById(userId).orElse(null);
    if(users == null) {
      throw new BadRequestException("User not found");
    }
    users.setActive(true);
    this.userRepository.save(users);
  }

  public ResponseEntity<Boolean> updateUser(UpdateUserDTO dto) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    PodUserDetail userDetail = (PodUserDetail) auth.getPrincipal();
    Optional<Users> users = this.userRepository.findById(userDetail.getId());
    if(users.isEmpty()) {
      throw new BadRequestException("User not found");
    }
    Users user = users.get();
    modelMapper.map(dto, user);
    this.userRepository.save(user);
    return new ResponseEntity<>(true, HttpStatus.OK);
  }

  private void deActiveLogin (Long userId) {
    List<UserLogin> userLogins = this.userLoginRepository.findByUserIdAndIsActiveIsTrue(userId);
    userLogins.forEach(userLogin -> userLogin.setActive(false));
    this.userLoginRepository.saveAll(userLogins);
  }
}
