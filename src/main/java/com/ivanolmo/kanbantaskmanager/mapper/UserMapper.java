package com.ivanolmo.kanbantaskmanager.mapper;

import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserDTO toDTO(User user) {
    UserDTO userDTO = new UserDTO();

    userDTO.setId(user.getId());
    userDTO.setEmail(user.getEmail());

    return userDTO;
  }
}
