/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.PersonDTO;
import dtos.PersonsDTO;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author ckfol
 */
public class PersonFacade implements IPersonFacade {
    
    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
        public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }
    
    

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone) throws MissingInputException {
        EntityManager em = emf.createEntityManager();
        if(fName.length() == 0 || lName.length() == 0){
            throw new MissingInputException("First Name and/or Last Name is missing");
        }
        Person person = new Person(fName, lName, phone);
        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            
            return new PersonDTO(person);
        }finally{
            em.close();
        }
        
    }

    @Override
    public PersonDTO deletePerson(int id) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        try{
            Person person = em.find(Person.class, id);
            if(person == null){
                throw new PersonNotFoundException("Could not delete, provided id does not exist");
            }
            em.getTransaction().begin();
            em.remove(person);
            em.getTransaction().commit();
            return new PersonDTO(person);
            
        }finally{
            em.close();
        }
    }

    @Override
    public PersonDTO getPerson(int id) throws PersonNotFoundException{
        EntityManager em = emf.createEntityManager();
        try {
            Person person = em.find(Person.class, id);
            if (person == null){
                throw new PersonNotFoundException("No person with provided id found");
            }
            return new PersonDTO(person);
            
        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersons(){
        EntityManager em = emf.createEntityManager();
        TypedQuery<Person> query =  em.createQuery("SELECT p FROM Person p",Person.class);
        List<Person> personList = query.getResultList();
        em.close();
        return new PersonsDTO(personList); 
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException{
        EntityManager em = emf.createEntityManager();
        if(p.getfName().length() == 0 || p.getlName().length() == 0){
            throw new MissingInputException("First Name and/or Last Name is missing");
        }        
        Person person = em.find(Person.class, p.getId());
        if(person == null){
            throw new PersonNotFoundException("No person with provided id found");
        }
        try{
            em.getTransaction().begin();
            person.setFirstName(p.getfName());
            person.setLastName(p.getlName());
            person.setPhone(p.getPhone());
            person.setLastEdited();
            em.getTransaction().commit();
            
            return new PersonDTO(person);
        }finally{
            em.close();
        }
        
    }
    
}
