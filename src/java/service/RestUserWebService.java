/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import db.DbException;
import java.util.List;
import logger.DJLog;
import model.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author SYUS
 */
public class RestUserWebService {
    
    private final DJLog logger = new DJLog();
    private UserSystem userSystem;
    
    public RestUserWebService() {
        userSystem = new UserSystem();
    }
    
    public RestUserWebService(UserSystem userSystem) {
        this.userSystem = userSystem;
    }
    
    //REST Read, read user via GET
    @RequestMapping(value = RestServiceURLs.REST_READ_USER_URI, method=RequestMethod.GET)
    public ResponseEntity<Person> getUserNew(@PathVariable("email") String email) {
        logger.writeInfo("Getting user via rest service with email: " + email);
        try {
            Person user = userSystem.get(email);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (DbException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    //REST Read all, read all users via GET
    @RequestMapping(value = RestServiceURLs.REST_READ_ALL_USERS_URI, method=RequestMethod.GET)
    public ResponseEntity<List<Person>> getAllUsersNew() {
        logger.writeInfo("Getting all users via rest service");
        try {
            List<Person> users = userSystem.getAll();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (DbException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    //REST Create, create user via POST
    @RequestMapping(value = RestServiceURLs.REST_CREATE_USER_URI, method=RequestMethod.POST)
    public ResponseEntity<String> createUserNew(@RequestBody Person newUser) {
        logger.writeInfo("Creating new user with rest service");
        newUser.setHashedPassword(newUser.getPassword());//todo:this should be done client side, it could be intercepted in transport to server
        try {
            userSystem.add(newUser);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (DbException e) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);//LMAO, <3 IETF
            //return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
    
    //REST Update, update user via PUT
    @RequestMapping(value = RestServiceURLs.REST_UPDATE_USER_URI, method=RequestMethod.PUT)
    public ResponseEntity<String> updateUserNew(@PathVariable(value="email") String email, @RequestBody Person updatedUser) {
        logger.writeInfo("Updating user via rest service with email: " + email);
        try {
            userSystem.update(updatedUser);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DbException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);//LMAO, <3 IETF
        }
    }
    
    //REST Delete, delete user via DELETE
    @RequestMapping(value = RestServiceURLs.REST_DELETE_USER_URI, method=RequestMethod.DELETE)
    public ResponseEntity<Person> deleteUserNew(@PathVariable(value="email") String email) {
        logger.writeInfo("Deleting user via rest service with email: " + email);
        try {
            userSystem.delete(email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DbException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);//LMAO, <3 IETF
            //return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
