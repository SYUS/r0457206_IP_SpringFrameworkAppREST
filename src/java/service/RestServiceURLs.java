/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

/**
 *
 * @author SYUS
 */
public class RestServiceURLs {
    
    public static final String REST_TEST_URI = "/user/new";
    public static final String REST_CREATE_USER_URI = "/user/create";
    public static final String REST_READ_USER_URI = "/user/{email}/read";
    public static final String REST_READ_ALL_USERS_URI = "/user/readall";
    public static final String REST_UPDATE_USER_URI = "/user/{email}/update";
    public static final String REST_DELETE_USER_URI = "/user/{email}/delete";
    
}