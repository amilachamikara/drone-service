package org.amila.droneservice.service.mapper;

import java.util.List;

public interface BasicMapper<T, A> {
    A mapIn(T dto);

    List<A> mapListIn(List<T> dtos);

    T mapOut(A dao);

    List<T> mapListOut(List<A> daos);
}
