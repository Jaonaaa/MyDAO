package com.geta.dao.model;

import com.geta.dao.annotation.Entity;
import com.geta.dao.annotation.Id;
import com.geta.dao.annotation.IgnoreField;
import com.geta.dao.entity.Relation;

@Entity(name = "person")
public class Person extends Relation {

    @Id(autoIcrement = true)
    Long id;

    String name;

    Double poids;

    public Person() {
    }

    public Person(Long id, String name, Double poids) {
        this.id = id;
        this.name = name;
        this.poids = poids;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    @Override
    public String toString() {
        return "Person [id=" + id + ", name=" + name + ", poids=" + poids + "]";
    }

}
