package org.amila.droneservice.repository;

import org.amila.droneservice.common.State;
import org.amila.droneservice.dao.DroneDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DroneRepository extends JpaRepository<DroneDAO, String> {
    List<DroneDAO> findAllByState(State state);

    Optional<DroneDAO> findBySerialNumber(String serialNumber);
}
