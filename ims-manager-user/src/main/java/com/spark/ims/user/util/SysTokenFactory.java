package com.spark.ims.user.util;

import com.spark.ims.common.util.StringUtils;
import com.spark.ims.common.util.UUIDUtils;
import com.spark.ims.user.constants.ITokenConstants;
import com.spark.ims.user.model.SysToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:01
 */
@Component
public class SysTokenFactory implements ITokenConstants {

    @Autowired
    private ServerProperties serverProperties;

    /**
     * 生成默认的token
     * @param userId
     * @return
     */
    public SysToken defaultGenerate(String userId){
        return defaultGenerate(userId, UUIDUtils.generate());
    }

    public SysToken defaultGenerate(String userId, Serializable tokenId){
        return defaultGenerate(userId, tokenId, DEFAULT_APP_ID);
    }

    public SysToken defaultGenerate(String userId,Serializable tokenId,String appId){
        SysToken token = new SysToken();
        token.setAppId(StringUtils.isEmpty(appId) ? DEFAULT_APP_ID : appId);
        token.setCreateTime(new Date());
        token.setToken(tokenId+"");
        // 移动端延长token过期时间
        token.setExpire(MOBILE_DEFAULT_APP_ID.equalsIgnoreCase(appId) ? DEFAULT_TOKEN_EXPIRE_MOBILE : serverProperties.getSession().getTimeout());
        token.setUserId(userId);
        return token;
    }


}
