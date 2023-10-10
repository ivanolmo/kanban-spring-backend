package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
  User createUser(User user);
  User getUserById(Long id);
  User getUserByEmail(String email);
}
