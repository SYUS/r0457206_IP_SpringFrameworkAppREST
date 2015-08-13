/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Person;
import org.springframework.web.client.RestTemplate;
import service.RestServiceURLs;

/**
 *
 * @author SYUS
 */
public class RestConsumer {
    
    public static final String SERVER_URL = "http://localhost:8080/r0457206_IP_SpringFrameworkApp-nyx";
    
    //Test
    private static void getTestUser() {
        RestTemplate restTemplate = new RestTemplate();
        Person user = restTemplate.getForObject(SERVER_URL + RestServiceURLs.REST_TEST_URI, Person.class);
        printUserData(user);
    }
    
    //Create
    private static void createUser() {
        RestTemplate restTemplate = new RestTemplate();
        Person user = new Person("TestCreatePerson", "TestCreatePerson@gmail.com", "password");
        Person response = restTemplate.postForObject(SERVER_URL + RestServiceURLs.REST_CREATE_USER_URI, user, Person.class);
        printUserData(response);
    }
    
    //Read
    private static void readUser() {
        RestTemplate restTemplate = new RestTemplate();
        Person user = restTemplate.getForObject(SERVER_URL + "/rest/user/user1@gmail.com/read/", Person.class);
        printUserData(user);
    }
    
    //Read all
    public static void readAllUsers() {
        RestTemplate restTemplate = new RestTemplate();
        //getting list not imp
        List<LinkedHashMap> users = restTemplate.getForObject(SERVER_URL + RestServiceURLs.REST_READ_ALL_USERS_URI, List.class);
        System.out.println(users.size());
        for(LinkedHashMap map : users){
            System.out.println("name="+map.get("name")+",email="+map.get("email")+",password="+map.get("password"));
        }
    }
     
    public static void printUserData(Person user){
        System.out.println("Name="+user.getName()+",Email="+user.getEmail()+",Password="+user.getPassword());
    }
    
    
    //=======================
    //REWORK
    //=======================
    
    //Create
    private static void createUserNew(String email, String name, String password) {
        Person newUser = new Person(name, email, password);

        RestTemplate restTemplate = new RestTemplate();
        Person result = restTemplate.postForObject( SERVER_URL + RestServiceURLs.REST_CREATE_USER_URI, newUser, Person.class);
        System.out.println(result);
    }
    
    //Read, single
    private static void readUserNew(String email) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        RestTemplate restTemplate = new RestTemplate();
        Person result = restTemplate.getForObject(SERVER_URL + RestServiceURLs.REST_READ_USER_URI, Person.class, params);

        System.out.println(result);
    }
    
    //Read, all
    private static void readAllUsersNew() {
        RestTemplate restTemplate = new RestTemplate();
        List<LinkedHashMap> users = restTemplate.getForObject(SERVER_URL + RestServiceURLs.REST_READ_ALL_USERS_URI, List.class);
        List<Person> usersList = new ArrayList<>();
        for(LinkedHashMap map : users){
            Person temp = new Person();
            temp.setName((String) map.get("name"));
            temp.setEmail((String) map.get("email"));
            temp.setPassword((String) map.get("password"));
            usersList.add(temp);
            System.out.println("name="+map.get("name")+",email="+map.get("email")+",password="+map.get("password"));
        }
    }
    
    //Update
    private static void updateUserNew(String email, String newName, String oldPassword, String newPassword) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        Person updatedUser = new Person(newName, email, newPassword);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put ( SERVER_URL + RestServiceURLs.REST_UPDATE_USER_URI, updatedUser, params);
    }
    
    //Delete
    private static void deleteUserNew(String email) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(SERVER_URL + RestServiceURLs.REST_DELETE_USER_URI, params);
    }
}
