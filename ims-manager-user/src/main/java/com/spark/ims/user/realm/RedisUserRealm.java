package com.spark.ims.user.realm;

import com.spark.ims.common.constants.DataDict;
import com.spark.ims.common.util.ParamUtils;
import com.spark.ims.core.exception.BusinessException;
import com.spark.ims.core.util.I18nUtils;
import com.spark.ims.user.constants.ErrorCode;
import com.spark.ims.user.token.StatelessToken;
import com.spark.ims.user.model.SysUser;
import com.spark.ims.user.token.UserLoginToken;
import com.spark.ims.user.service.ISysUserService;
import com.spark.ims.user.strategy.LDAPLoginStrategy;
import com.spark.ims.user.strategy.LocalLoginStrategy;
import com.spark.ims.user.strategy.LoginStrategyFactory;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:15
 */
public class RedisUserRealm extends AuthorizingRealm {

    private static final Logger logger = LoggerFactory.getLogger(RedisUserRealm.class);

    private static final String LoginType_Local = "2";
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private LoginStrategyFactory loginStrategyFactory;

    public RedisUserRealm() {
        logger.info("Create redisUserRealm ");
    }

    @Override
    protected AuthorizationInfo getAuthorizationInfo(
            PrincipalCollection principals) {
        if (principals == null) {
            return null;
        }

        AuthorizationInfo info = null;

        if (logger.isTraceEnabled()) {
            logger.trace("Retrieving AuthorizationInfo for principals ["
                    + principals + "]");
        }

        Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Attempting to retrieve the AuthorizationInfo from cache.");
            }
            Object key = getAuthorizationCacheKey(principals);
            info = cache.get(key);
            if (logger.isTraceEnabled()) {
                if (info == null) {
                    logger.trace("No AuthorizationInfo found in cache for principals ["
                            + principals + "]");
                } else {
                    logger.trace("AuthorizationInfo found in cache for principals ["
                            + principals + "]");
                }
            }
        }
        if (info == null) {
            // Call template method if the info was not found in a cache
            info = doGetAuthorizationInfo(principals);
            // If the info is not null and the cache has been created, then
            // cache the authorization info.
            if (info != null && cache != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Caching authorization info for principals: ["
                            + principals + "].");
                }
                Object key = getAuthorizationCacheKey(principals);
                cache.put(key, info);
            }
        }
        return info;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return (token instanceof UsernamePasswordToken) || (token instanceof StatelessToken);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String userId = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        try {
            SysUser user = sysUserService.findById(userId);
            Assert.notNull(user, "UserRealm doGetAuthorizationInfo can not find user,UserId=" + userId);
            Set<String> permissionSet = new HashSet<>();
            Set<String> userList = new HashSet<String>();
            userList.add(user.getName());
            authorizationInfo.setRoles(userList);
            authorizationInfo.setStringPermissions(permissionSet);
        } catch (Exception e) {
            logger.error("获取用户记录失败:\n" + e.getMessage(), e);
            throw new BusinessException("获取用户记录失败," + e.getMessage());
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String principal = null;
        if (UserLoginToken.class.isAssignableFrom(token.getClass())) {
            try {
                String loginType = ((UserLoginToken)token).getLoginType();
                String appId = ((UserLoginToken)token).getAppId();
                String account = ((UserLoginToken)token).getUsername();
                String password = new String(((UserLoginToken)token).getPassword());

                String loginStrategyKey = getLoginStrategy(loginType, appId);

                logger.info("用户登录认证: account：" + account + "," + loginStrategyFactory.getStrategy(loginStrategyKey).getClass().getSimpleName());
                boolean loginResult = true;
                         //先注释认证
                        //loginStrategyFactory.getStrategy(loginStrategyKey).auth(account, password, loginType,appId);
                if(loginResult) {
                    boolean isLocalLogin = loginStrategyKey.equalsIgnoreCase(LocalLoginStrategy.class.getName());
                    if (isLocalLogin) {
                        this.setSession("loginType", LoginType_Local);
                    }
                    else {
                        this.setSession("loginType", null);
                    }

                    SysUser sysUser = sysUserService.findByAccount(account);
                    if (ParamUtils.isNull(sysUser)) {
                        String errorMessageUserOrPwdNotMatch = I18nUtils.getMessage(ErrorCode.Ims.userOrPwdNotMatch);
                        logger.error("本地登录认证，账号：{}，匹配不到!", account);

                        if (!isLocalLogin) {
                            // bug10789，非本地登陆提示用户不存在于本系统
                            errorMessageUserOrPwdNotMatch = "本系统无此用户，请联系管理员！";
                        }
                        throw new UnknownAccountException(errorMessageUserOrPwdNotMatch);
                    }
                    if (sysUser.getStatus().equals(DataDict.SysUserStatus.DISABLE.toString())) {
                        throw new LockedAccountException(I18nUtils.getMessage(ErrorCode.Ims.userDisabled));
                    }
                    principal = sysUser.getId();
                }
                else{
                    throw new AuthenticationException("login failure");
                }
            }
            catch (Exception ex){
                throw new AuthenticationException(ex.getMessage(),ex);
            }

        } else if (StatelessToken.class.isAssignableFrom(token.getClass())) {
            try {
                principal  = token.getPrincipal().toString();
            } catch (Exception ae) {
                logger.error("UserRealm doGetAuthenticationInfo Error :\n" + ae.getMessage(), ae);
                throw new AccountException(ae.getMessage());
            }
        } else {
            throw new AuthenticationException("UserRealm doGetAuthenticationInfo Error,Not Support token:" + token.getClass());
        }
        return new SimpleAuthenticationInfo(principal, token.getCredentials(), getName());
    }

    /**
     * 获取登录策略
     * @param loginType
     * @param appId
     */
    private String getLoginStrategy(String loginType, String appId) {
        String loginStrategyKey = LocalLoginStrategy.class.getName();
        //TODO 先设置为null
        String loginStrategyService = null;
        if (loginStrategyService != null) {
            loginStrategyKey = loginStrategyService;
        }
        //先设置为null
        String ldapEnable = null;
        if (ldapEnable != null && ldapEnable.equalsIgnoreCase("true")) {
            //当开启ldap时，如果客户端指定类型为本地登录，则使用本地登录
            boolean ldapFlag = true;
            if (loginType != null && loginType.equalsIgnoreCase(LoginType_Local)) {
                ldapFlag = false;
            }
            //当开启ldap时，如果客户端指定登录来源为ITSM,则使用本地登录
            else if (appId!= null &&appId.equalsIgnoreCase("ITSM")) {
                ldapFlag = false;
            }
            if (ldapFlag) {
                loginStrategyKey = LDAPLoginStrategy.class.getName();
            } else {
                loginStrategyKey = LocalLoginStrategy.class.getName();
            }
        }
        return  loginStrategyKey;
    }

    /**
     * 将一些数据放到ShiroSession中,以便于其它地方使用
     *
     * 比如Controller,使用时直接用HttpSession.getAttribute(key)就可以取到
     */
    private void setSession(Object key, Object value) {
        Subject subject = SecurityUtils.getSubject();
        if (null != subject) {
            Session session = subject.getSession();
            logger.debug("Session默认超时时间为[" + session.getTimeout() + "]毫秒");
            if (null != session) {
                session.setAttribute(key, value);
            }
        }
    }

}
