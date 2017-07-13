package org.orh.demo.service;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    Cache cache;

    public OAuthService() {
        CacheManager cacheManager = new SimpleCacheManager();
        this.cache = cacheManager.getCache("code-cache");
    }

    public void addAuthCode(String authCode, String username) {
        cache.put(authCode, username);
    }

    public boolean checkAuthCode(String authCode) {
        return cache.get(authCode) != null;
    }
    
    public String getUsernameByAuthCode(String authCode) {
        return (String) cache.get(authCode).get();
    }
    
    public void addAccessToken(String accessToken, String username) {
        cache.put(accessToken, username);
    }
    
    public boolean checkAccessToken(String accessToken) {
        return cache.get(accessToken) != null;
    }
    
    public String getUsernameByAccessToken(String accessToken) {
        return (String) cache.get(accessToken).get();
    }
}
