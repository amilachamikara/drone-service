package org.amila.droneservice.dao;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "DS_MEDICATION")
public class MedicationDAO extends AbstractAuditableEntity {
    @Column(name = "CODE", unique = true, nullable = false)
    private String code;
    @Column(name = "NAME")
    private String name;
    @Column(name = "WEIGHT")
    private Double weight;

    @Column(name = "IMAGE_URL")
    private String imageURL;

    public MedicationDAO() {
    }

    public MedicationDAO(String code, String name, Double weight, String imageURL) {
        this.code = code;
        this.name = name;
        this.weight = weight;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
