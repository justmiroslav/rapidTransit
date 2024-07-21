package org.rapidTransit.dao;

import org.rapidTransit.model.User;

import java.util.List;

public interface UserDAO {
    User findByEmail(String email);
    User findById(long id);
    List<User> getAllUsers();
    void save(User user);
    void update(User user);
}
