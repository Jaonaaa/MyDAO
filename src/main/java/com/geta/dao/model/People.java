package com.geta.dao.model;

import com.geta.dao.annotation.Entity;
import com.geta.dao.annotation.Fk;
import com.geta.dao.annotation.Id;
import com.geta.dao.entity.Relation;

@Entity(name = "people")
public class People extends Relation {

    @Id(autoIcrement = true)
    Long id;

    Boolean alive;

    @Fk(name = "id_person")
    Person person;

    @Fk(name = "id_person_too")
    Person persontoo;

    public People() {
    }

    public People(Long id, Boolean alive, Person person, Person persontoo) {
        this.id = id;
        this.alive = alive;
        this.person = person;
        this.persontoo = persontoo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "People [id=" + id + ", alive=" + alive + ", person=" + person + ", persontoo=" + persontoo + "]";
    }

    public Person getPersontoo() {
        return persontoo;
    }

    public void setPersontoo(Person persontoo) {
        this.persontoo = persontoo;
    }

}
