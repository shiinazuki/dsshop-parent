package com.iori.service.impl;

import com.iori.oauth.util.AuthToken;
import com.iori.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DiscoveryClient discoveryClient;


    @Override
    public AuthToken login(String username, String password, String clientId, String clientSecret) {

        List<ServiceInstance> instances = discoveryClient.getInstances("auth-server");
        ServiceInstance serviceInstance = instances.get(0);

        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/oauth/token";

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        LinkedMultiValueMap<String, String> head = new LinkedMultiValueMap<>();
        head.add("authorization", httpHeader(clientId, clientSecret));

        //远程调用
        ResponseEntity<Map> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(body, head), Map.class);

        Map value = responseEntity.getBody();

        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(value.get("access_token").toString());
        authToken.setRefreshToken(value.get("refresh_token").toString());
        authToken.setJti(value.get("jti").toString());

        return authToken;
    }

    /**
     * Base64加密
     *
     * @param clientId
     * @param clientSecret
     * @return
     */
    private static String httpHeader(String clientId, String clientSecret) {
        String str = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(str.getBytes());
        try {
            String code = new String(encode, "UTF-8");
            return "Basic " + code;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


}
