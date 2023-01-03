package com.mycompany.myapp.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * A ClassEntity.
 */
@Entity
@Table(name = "class_entity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ClassEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "class_id")
    private Integer classId;

    @Column(name = "name")
    private String name;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ClassEntity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getClassId() {
        return this.classId;
    }

    public ClassEntity classId(Integer classId) {
        this.setClassId(classId);
        return this;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getName() {
        return this.name;
    }

    public ClassEntity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClassEntity)) {
            return false;
        }
        return id != null && id.equals(((ClassEntity) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ClassEntity{" +
            "id=" + getId() +
            ", classId=" + getClassId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
