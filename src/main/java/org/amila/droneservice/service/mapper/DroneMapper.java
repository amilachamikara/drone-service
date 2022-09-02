package org.amila.droneservice.service.mapper;

import org.amila.droneservice.dao.DroneDAO;
import org.amila.droneservice.dto.Drone;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DroneMapper extends BasicMapper<Drone, DroneDAO> {
}
