package org.amila.droneservice.dao;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class AbstractAuditableEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    //TODO: Created and Update time to be added here (@CreatedDate and @LastModifiedDate)

    protected AbstractAuditableEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
