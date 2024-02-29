CREATE DATABASE dao ;

CREATE TABLE person (
    id BIGINT PRIMARY KEY ,
    name VARCHAR(255) NOT NULL ,
    poids NUMERIC(10,3) NOT NULL 
);

CREATE TABLE people (
    id BIGINT PRIMARY KEY ,
    alive BOOL NOT NULL ,
    id_person BIGINT  REFERENCES Person (id) ON DELETE CASCADE,
        id_person_too BIGINT  REFERENCES Person (id) ON DELETE CASCADE

    
);
