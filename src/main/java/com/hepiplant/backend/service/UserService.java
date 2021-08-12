package com.hepiplant.backend.service;


import com.hepiplant.backend.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAll();
    User getById(Long id);
}
