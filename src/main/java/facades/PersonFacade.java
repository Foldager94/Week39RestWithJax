/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;


import entities.Address;
import dtos.PersonDTO;
import dtos.PersonsDTO;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
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
    public PersonDTO addPerson(String fName, String lName, String phone, String street, String city, int zip) throws MissingInputException {
        EntityManager em = emf.createEntityManager();
        if(fName.length() == 0 || lName.length() == 0){
            throw new MissingInputException("First Name and/or Last Name is missing");
        }
        if(street.length() == 0 || city.length() == 0){
            throw new MissingInputException("Street or city is missing");
        }
        Person person = new Person(fName, lName, phone);
        Query q = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.city = :city AND a.zip = :zip");
        q.setParameter("street", street);
        q.setParameter("city", city);
        q.setParameter("zip", zip);
        List<Address> address = q.getResultList();
        
        if(address.size() > 0){
            person.setAddress(address.get(0));
        }else{
            person.setAddress(new Address(street, city, zip));
        }
        
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
            Address address = person.getAddress();
            Query q = em.createQuery("SELECT p FROM Person p WHERE p.address.id = :id");
            q.setParameter("id", address.getId());

            if(person == null){
                throw new PersonNotFoundException("Could not delete, provided id does not exist");
            }
            em.getTransaction().begin();
            em.remove(person);
            if(q.getResultList().size() <= 0){
            em.remove(address);
            }
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
