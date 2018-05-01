package com.spark.ims.user.model;

import com.spark.ims.common.domain.BaseModel;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:25
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "sys_token")
public class SysToken extends BaseModel {

    /**
     *
     */
    private static final long serialVersionUID = -6792101363050176170L;

    /**
     * 用户ID
     */
    @Column
    private String userId;

    /**
     * 令牌
     */
    @Column
    private String token;

    /**
     * 创建时间
     */
    @Column
    private Date createTime;

    /**
     * 有效期
     */
    @Column
    private Integer expire;

    /**
     * 应用ID
     */
    @Column
    private String appId;

    @Column
    private Date logoutTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getExpire() {
        return expire;
    }

    public void setExpire(Integer expire) {
        this.expire = expire;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }
}
