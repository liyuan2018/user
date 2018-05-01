package com.spark.ims.user.service.impl;

import com.spark.ims.auth.constants.ITokenConstants;
import com.spark.ims.common.domain.ResultData;
import com.spark.ims.common.util.DESUtils;
import com.spark.ims.common.util.StringUtils;
import com.spark.ims.core.domain.UserInfo;
import com.spark.ims.core.exception.BusinessException;
import com.spark.ims.user.model.SysToken;
import com.spark.ims.user.model.SysUser;
import com.spark.ims.user.model.UserLoginToken;
import com.spark.ims.user.service.ILoginService;
import com.spark.ims.user.service.ISysTokenService;
import com.spark.ims.user.service.ISysUserService;
import com.spark.ims.user.util.SysTokenFactory;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;


/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:31
 */
@Service("loginService")
public class LoginServiceImpl implements ILoginService {


    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysTokenService sysTokenService;

    @Autowired
    private SysTokenFactory sysTokenFactory;


    /**
     * 登录接口
     * @param account
     * @param password
     * @param loginType
     * @param appId
     * @return
     * @throws Exception
     */
    public ResultData login(String account,String password,String loginType,String appId) throws Exception{
        if(StringUtils.isEmpty(appId)){
            appId = ITokenConstants.DEFAULT_APP_ID;
        }
        //DES解密
        DESUtils des = new DESUtils();
        password = des.decrypt(password,account);
        UserLoginToken token = new UserLoginToken(loginType,appId,account, password,null);
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        if(subject.isAuthenticated()){
            return this.createSessionUser(account, appId);
        } else {
            throw new UnauthenticatedException();
        }
    }

    public ResultData createSessionUser(String account,String appId){
        Subject subject = SecurityUtils.getSubject();
        SysUser sysUser = sysUserService.findByAccount(account);

        //构造userInfo对象
        UserInfo userInfo = new UserInfo();
        try {
            PropertyUtils.copyProperties(userInfo,sysUser);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new BusinessException("创建用户失败，请联系管理员。");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new BusinessException("创建用户失败，请联系管理员。");
        }
        //设置会话对象user
        subject.getSession().setAttribute("user", userInfo);
        //token持久化
        SysToken sysToken = sysTokenService.create(
                sysTokenFactory.defaultGenerate(userInfo.getId(), subject.getSession().getId().toString(), appId)
        );
        ResultData rd = new ResultData();
        rd.put("loginType", subject.getSession().getAttribute("loginType"));
        rd.put("token", sysToken.getToken());
        rd.put("create_time", sysToken.getCreateTime());
        rd.put("expire", sysToken.getExpire());
        rd.put("sysUser", sysUser);
        rd.put("sessionId", subject.getSession().getId());
        return  rd;
    }
}
