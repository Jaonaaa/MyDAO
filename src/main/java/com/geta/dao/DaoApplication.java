package com.geta.dao;

import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.geta.dao.model.People;
import com.geta.dao.model.Person;

@SpringBootApplication
public class DaoApplication {

	public static void main(String[] args) throws Exception {
		// Person person = new Person((19), "Mark", 60.0);
		// // person.createSequence(null);
		List<Person> persons = new Person().selectAll(null);

		People people = new People(null, true, persons.get(0), persons.get(1));
		System.out.println(people.toString());
		// people.createSequence(null);
		// people.persit(null);
		List<People> res = people.selectAll(null);
		System.out.println("\n");
		for (People p : res) {
			System.out.println(p.toString());
		}

	}

}
