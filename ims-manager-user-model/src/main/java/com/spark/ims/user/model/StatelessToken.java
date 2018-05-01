package com.spark.ims.user.model;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:10
 */
public class StatelessToken implements AuthenticationToken {

    /**
     *
     */
    private static final long serialVersionUID = 6440240265339179041L;

    private String userId;
    private String token;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

}
