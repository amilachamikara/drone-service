package org.amila.droneservice.service.mapper;

import org.amila.droneservice.dao.MedicationDAO;
import org.amila.droneservice.dto.Medication;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper extends BasicMapper<Medication, MedicationDAO> {
}
