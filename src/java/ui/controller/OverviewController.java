/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui.controller;

import db.DbException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import logger.DJLog;
import model.Entry;
import model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import service.CurrencyRatesRestConsumer;
import service.UserSystem;

/**
 *
 * @author SYUS
 */

/**
 * todo:
 * + need to use a session that is destroyed at logout to save the user credentials
 *   passing them as plain text in params is just idiotic
 * + need to hook up the postgresdb and redo the database type selection
 * + use the constraint system that spring provides instead of exceptions
 * + clean up the project in general, it's kinda a mess
*/



@Controller
//@SessionAttributes({"userEmail","userPassword"})
//@RequestMapping("/Controller")//extends baseontroller for exceptions catching and redirecting
public class OverviewController implements Serializable {
    @Autowired
    private UserSystem userSystem;
    private final DJLog logger;

    public OverviewController(){
        logger = new DJLog();
    }

    public UserSystem getUserSystem() {
        return userSystem;
    }
    
    @Autowired
    public void setUserSystem(UserSystem userSystem) {
        logger.writeInfo("receiving usersystem");
        try {
            if(userSystem!=null){}
        } catch (NullPointerException e) {
            logger.writeFatal("usersystem was not received");
        }
        this.userSystem = userSystem;
        logger.writeInfo("received usersystem");
        logger.writeInfo("usersystem db type, users is: " + userSystem.getUserDbType());
        logger.writeInfo("usersystem db type, entries is: " + userSystem.getEntryDbType());
    }
    
    //===============================
    //User & session management
    //===============================
    
    @RequestMapping(value="/showCurrencies", method=RequestMethod.POST)
    protected ModelAndView showCurrencies() {
        logger.writeInfo("showing login page currencies");
        ModelAndView mav = new ModelAndView("index");
        if(CurrencyRatesRestConsumer.readRates()==null) {
            mav.addObject("error", "Api unreachable");
        } else {
            mav.addObject("currencies", CurrencyRatesRestConsumer.readRates());
        }
        return mav;
    }
    
    @RequestMapping(value="/index")
    protected ModelAndView getIndex(){
        return new ModelAndView("index");
    }
    
    //User login, get overview via POST
    @RequestMapping(value="/overview", method=RequestMethod.POST)
    protected ModelAndView getOverview(HttpServletRequest request, @ModelAttribute("person") Person user) throws Exception {
        Person userTest = null;
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        String currentUserPassword = (String) request.getSession().getAttribute("userPassword");
        String currentUserName = (String) request.getSession().getAttribute("userName");
         
        logger.writeWarning("Before overview, Current userEmail: " + currentUserEmail);
        logger.writeWarning("Before overview, Current userPassword: " + currentUserPassword);
        logger.writeWarning("Before overview, Current userName: " + currentUserName);
        
        logger.writeInfo("receiving login attempt via POST");
        logger.writeInfo("entered form data, email: " + user.getEmail());
        //This is ugly, I need to rework this big time
        try {
            logger.writeInfo("checking user credentials");
            userTest = getUserSystem().getAuthenticatedUser(user.getEmail(), user.getPassword());
            if (userTest==null) {
                logger.writeInfo("in test, user was null");
                throw new NullPointerException();
            }
            logger.writeInfo("fetched credentials:");
            logger.writeInfo("fetched email: " + userTest.getEmail());
            logger.writeInfo("fetched password:" + userTest.getPassword());
            logger.writeInfo("credentials ok, forwarding to overview");
        } catch (NullPointerException | DbException ex) {
            //Sending user back to login form with error notification
            logger.writeWarning("credentials nok, returning to index");
            String error = "Unknown email and password combination";
            return new ModelAndView("index", "error", error);
        }
        //Create user session
        initSession(request, userTest);
        return new ModelAndView("overview","entries",getUserSystem().getAllEntries(user.getEmail()));
    }
    
    //get overview via GET if session is okay
    @RequestMapping(value="/overview", method=RequestMethod.GET)
    protected ModelAndView getOverview(HttpServletRequest request) throws Exception {
        //This way of showing the overview is very unsafe and should use a session
        //String currentUserEmail = getCurrentUser().getEmail();
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        logger.writeInfo("Getoverview when logged in email: " + currentUserEmail);
        //if (currentUserEmail!=null) {
        //    return getOverview(request, getUserSystem().get(currentUserEmail));
        //}
        return new ModelAndView("overview","entries",getUserSystem().getAllEntries(currentUserEmail));
    }   
    
    //User Logout
    @RequestMapping(value="/logout", method=RequestMethod.GET)
    protected ModelAndView logout(HttpServletRequest request) throws Exception {
        return logoutAndDestroy(request, "index");
    }
    
    private ModelAndView logoutAndDestroy(HttpServletRequest request, String destination) throws Exception {
        request.getSession().setAttribute("userEmail", null);
        request.getSession().setAttribute("userPassword", null);
        request.getSession().setAttribute("userName", null);
        //request.getSession().setMaxInactiveInterval(-1);
        request.getSession().invalidate();
        return new ModelAndView(destination);
    }
    
    
    //Session creation
    private void initSession(HttpServletRequest request, Person userTest) {
        System.out.println("in init session");
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        String currentUserPassword = (String) request.getSession().getAttribute("userPassword");
        String currentUserName = (String) request.getSession().getAttribute("userName");
         
        logger.writeWarning("Before session init, Current userEmail: " + currentUserEmail);
        logger.writeWarning("Before session init, Current userPassword: " + currentUserPassword);
        logger.writeWarning("Before session init, Current userName: " + currentUserName);
        //if(null == request.getSession(false)) {
        //if(!isUserAuthenticated(request)) {
            System.out.println("creating session via init");
            request.getSession().setAttribute("userEmail",userTest.getEmail());       //saving user stuff to session
            request.getSession().setAttribute("userPassword",userTest.getPassword());
            request.getSession().setAttribute("userName",userTest.getName());
            request.getSession().setMaxInactiveInterval(900);
            //setCurrentUser(userTest);
        //}
            currentUserEmail = (String) request.getSession().getAttribute("userEmail");
         currentUserPassword = (String) request.getSession().getAttribute("userPassword");
         currentUserName = (String) request.getSession().getAttribute("userName");
         
         logger.writeWarning("after session init, Current userEmail: " + currentUserEmail);
        logger.writeWarning("after session init, Current userPassword: " + currentUserPassword);
        logger.writeWarning("after session init, Current userName: " + currentUserName);
    }
    
    //Checking if user already has a session
    private boolean isUserAuthenticated(HttpServletRequest request){
        if (request.getSession(false)==null) {
            logger.writeInfo("session invalid, user not authd");
            return false;
        } else if(request.getSession().getAttribute("userEmail")==null 
               || request.getSession().getAttribute("userPassword")==null
               || request.getSession().getAttribute("userEmail").equals("")
               || request.getSession().getAttribute("userPassword").equals("")) {
            logger.writeWarning("session valid, user not correct");
            return false;
        }
        logger.writeInfo("session valid, user authenticated");
        return true;
    }

    //===============================
    
    //===============================
    //Entry management
    //===============================
    
    //Create
    @RequestMapping(value="/entryNew", method=RequestMethod.GET)
    protected ModelAndView newEntry(HttpServletRequest request) throws Exception {
        return new ModelAndView("entryNew");
    }
    
    @RequestMapping(value="/entryNew", method=RequestMethod.POST)
    protected ModelAndView newEntry(HttpServletRequest request, @ModelAttribute("entry") Entry newEntry) throws Exception {
        //String currentUserEmail = getCurrentUser().getEmail();
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        
        newEntry.setOwnerEmail(currentUserEmail);
        newEntry.setDate(dateFormat.format(date));
        getUserSystem().addEntry(newEntry);
        return getOverview(request);
    }
    
    //Read
    @RequestMapping(value="/entryView/{entryTitle}", method=RequestMethod.GET)
    protected ModelAndView getEntry(HttpServletRequest request, @PathVariable String entryTitle) throws Exception {
        //String currentUserEmail = getCurrentUser().getEmail();
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        Entry selectedEntry = getUserSystem().getEntry(entryTitle,currentUserEmail);
        if(selectedEntry!=null){
            return new ModelAndView("entryView","entry",selectedEntry);
        }
        else {
            return getOverview(request);
        }
    }
    
    //Update, navigate to edit
    @RequestMapping(value="/entryEdit/{entryTitle}", method=RequestMethod.GET)
    protected ModelAndView updateEntry(HttpServletRequest request, @PathVariable String entryTitle) throws Exception {
        //String currentUserEmail = getCurrentUser().getEmail();
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        Entry selectedEntry = getUserSystem().getEntry(entryTitle,currentUserEmail);
        if(selectedEntry!=null)
            return new ModelAndView("entryEdit","entry",selectedEntry);
        else return getOverview(request);
    }
    
    //Update, save and return to overview
    @RequestMapping(value="/entryEdit/saveEntry/{entryTitle}", method=RequestMethod.POST)
    protected ModelAndView updateEntry(HttpServletRequest request, @ModelAttribute("entry") Entry editedEntry, @PathVariable String entryTitle) throws Exception {
        //String currentUserEmail = getCurrentUser().getEmail();
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        Entry oldEntry = getUserSystem().getEntry(entryTitle, currentUserEmail);
        
        editedEntry.setOwnerEmail(currentUserEmail);
        editedEntry.setDate(oldEntry.getDate());
        editedEntry.setTitle(oldEntry.getTitle());//necessary?
 
        getUserSystem().updateEntry(editedEntry);
        
        return getOverview(request);
    }
    
    //Delete
    @RequestMapping(value="/entryDelete/{entryTitle}", method=RequestMethod.GET)
    protected ModelAndView deleteEntry(HttpServletRequest request, @PathVariable String entryTitle) throws Exception {
        //String currentUserEmail = getCurrentUser().getEmail();
        String currentUserEmail = (String) request.getSession().getAttribute("userEmail");
        getUserSystem().deleteEntry(entryTitle, currentUserEmail);
        logger.writeInfo("attempting to delete entry: " + entryTitle);
        return getOverview(request);
    }
       
    //===============================
    
    
    //===============================
    //Register management
    //===============================
    
    //Navigation to register new user page
    @RequestMapping(value = "/register", method=RequestMethod.GET)
    protected ModelAndView newUser(HttpServletRequest request) throws Exception {
        if(isUserAuthenticated(request)) {
            logoutAndDestroy(request, "register");
        }
        return new ModelAndView("register");
    }
    
    //Registering a new user
    @RequestMapping(value="/register", method=RequestMethod.POST)
    protected ModelAndView newUser(HttpServletRequest request, @ModelAttribute("person") @Valid Person newUser, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("errors", bindingResult.getAllErrors());
            return mav;
        }
        newUser.setHashedPassword(newUser.getPassword());
        getUserSystem().add(newUser);
        if(isUserAuthenticated(request)) {
            logoutAndDestroy(request, "register");
        }
        return new ModelAndView("index");
    }
    
    
    //===============================
    
    //===============================
    //Exception & Error code handling
    //===============================

    
    @RequestMapping(value = "/error")
    public ModelAndView hanldeError(final HttpServletRequest request) {
        return new ModelAndView("error");
    }
    
    @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Not Found")
    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", e);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return mav;
    }
    
}
