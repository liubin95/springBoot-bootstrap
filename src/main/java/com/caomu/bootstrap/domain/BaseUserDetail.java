package com.caomu.bootstrap.domain;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * spring security 使用的user实体
 *
 * @author 刘斌
 */
public abstract class BaseUserDetail implements UserDetails, CredentialsContainer {

    private Long id;

    private String loginName;

    private String password;

    private String token;

    private Set<GrantedAuthority> authSet;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return this.authSet;
    }

    @Override
    public String getPassword() {

        return this.password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    @Override
    public String getUsername() {

        return this.loginName;
    }

    @Override
    public void eraseCredentials() {

        this.password = null;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public void setLoginName(String loginName) {

        this.loginName = loginName;
    }

    public String getToken() {

        return token;
    }

    public void setToken(String token) {

        this.token = token;
    }

    public void setAuthSet(Set<GrantedAuthority> authSet) {

        this.authSet = authSet;
    }

}
