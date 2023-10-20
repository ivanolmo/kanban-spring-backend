package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
  User createUser(User user);

  User getUserById(String id);

  User getUserByEmail(String email);
}
