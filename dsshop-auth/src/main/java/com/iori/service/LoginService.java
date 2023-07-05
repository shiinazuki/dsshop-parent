package com.iori.service;

import com.iori.oauth.util.AuthToken;

public interface LoginService {

    AuthToken login(String username,String password,String clientId,String clientSecret);

}
