package com.youtube.ecommerce.controller;

import com.youtube.ecommerce.entity.User;
import com.youtube.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostConstruct
    public void initRoleAndUser() {
        userService.initRoleAndUser();
    }

    @PostMapping({"/registerNewUser"})
    @ResponseBody
    public User registerNewUser(@RequestBody  User user, HttpServletRequest request)
            throws UnsupportedEncodingException, MessagingException {
        return userService.registerNewUser(user, getSiteURL(request));

    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
    /*
public String processRegister(User user, HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {
		System.out.println(user);
		service.register(user, getSiteURL(request));
		return "register_success";
 */
    @GetMapping({"/forAdmin"})
    @PreAuthorize("hasRole('Admin')")
    public String forAdmin(){
        return "This URL is only accessible to the admin";
    }

    @GetMapping({"/forUser"})
    @PreAuthorize("hasRole('User')")
    public String forUser(){
        return "This URL is only accessible to the user";
    }

    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        if (userService.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }
}
