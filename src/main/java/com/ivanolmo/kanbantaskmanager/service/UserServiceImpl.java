package com.ivanolmo.kanbantaskmanager.service;

import com.ivanolmo.kanbantaskmanager.entity.User;
import com.ivanolmo.kanbantaskmanager.exception.user.UserNotFoundException;
import com.ivanolmo.kanbantaskmanager.repository.UserRepository;


public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(User user) {
    return userRepository.save(user);
  }

  public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not " +
        "found."));
  }

  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User " +
        "not found."));
  }
}
