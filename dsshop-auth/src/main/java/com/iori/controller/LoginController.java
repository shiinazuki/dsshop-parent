package com.iori.controller;

import com.iori.oauth.util.AuthToken;
import com.iori.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class LoginController {

    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public AuthToken login(String username, String password) {
        return loginService.login(username, password, clientId, clientSecret);
    }

}
