package com.spark.ims.user.model;

import org.apache.shiro.authc.UsernamePasswordToken;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:33
 */
public class UserLoginToken extends UsernamePasswordToken {

    private String appId;

    private String loginType;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public UserLoginToken(String loginType,String appId,String username,String password, String host) {
        super(username, password != null?password.toCharArray():null, false, host);
        this.appId = appId;
        this.loginType = loginType;
    }
}
