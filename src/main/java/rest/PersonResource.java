/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;

/**
 * REST Web Service
 *
 * @author ckfol
 */
@Path("person")
public class PersonResource {
    
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
               
    private static final PersonFacade FACADE =  PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    
    


    /**
     * Creates a new instance of PersonResource
     */
    public PersonResource() {
    }
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo(){
        
        return "{\"Hallo\":\"World\"}";
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addPerson(String person){
        PersonDTO personDTO = GSON.fromJson(person, PersonDTO.class);
        PersonDTO newPersonDTO = FACADE.addPerson(personDTO.getfName(), personDTO.getlName(), personDTO.getPhone());
        
        return GSON.toJson(newPersonDTO);
        
    }
    
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String getPerson(@PathParam("id") int id){
        PersonDTO personDTO = FACADE.getPerson(id);
        
        return GSON.toJson(personDTO);
    }
    
    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPersons(){
        
        return GSON.toJson(FACADE.getAllPersons());
    }
    
    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String editPerson(@PathParam("id") int id, String person){
        PersonDTO personDTO = GSON.fromJson(person, PersonDTO.class);
        personDTO.setId(id);
        PersonDTO editPersonDTO = FACADE.editPerson(personDTO);
        
        return GSON.toJson(editPersonDTO);
        
    }
    
    
    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String deletePerson(@PathParam("id") int id){
        PersonDTO pDeleted = FACADE.deletePerson(id);
        
        return GSON.toJson(pDeleted);
    }
    
    
    
    
    
}
