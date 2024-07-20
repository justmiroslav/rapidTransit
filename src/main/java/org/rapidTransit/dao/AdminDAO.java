package org.rapidTransit.dao;

import org.rapidTransit.model.Admin;

public interface AdminDAO {
    Admin findByEmail(String email);
}
