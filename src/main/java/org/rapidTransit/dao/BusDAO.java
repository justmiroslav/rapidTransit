package org.rapidTransit.dao;

import org.rapidTransit.model.Bus;

public interface BusDAO {
    Bus findById(int id);
}
