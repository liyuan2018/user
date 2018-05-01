package com.spark.ims.user.dto;

import com.spark.ims.user.model.SysUser;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:09
 */
public class SysUserDTO extends SysUser {

    private String captchaValue;

    private String loginType;

    public String getCaptchaValue() {
        return captchaValue;
    }

    public void setCaptchaValue(String captchaValue) {
        this.captchaValue = captchaValue;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
