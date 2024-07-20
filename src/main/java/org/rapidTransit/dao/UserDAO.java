package org.rapidTransit.dao;

import org.rapidTransit.model.User;

public interface UserDAO {
    User findByEmail(String email);
    User findById(long id);
    void save(User user);
    void update(User user);
}
