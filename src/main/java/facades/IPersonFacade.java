/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import exceptions.PersonNotFoundException;
import dtos.PersonDTO;
import dtos.PersonsDTO;

/**
 *
 * @author ckfol
 */
interface IPersonFacade {
  public PersonDTO addPerson(String fName, String lName, String phone);  
  public PersonDTO deletePerson(int id) throws PersonNotFoundException;  
  public PersonDTO getPerson(int id);  
  public PersonsDTO getAllPersons();  
  public PersonDTO editPerson(PersonDTO p);

}
