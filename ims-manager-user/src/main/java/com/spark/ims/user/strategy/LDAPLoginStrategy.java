package com.spark.ims.user.strategy;

import com.spark.ims.auth.strategy.ILoginStrategy;
import com.spark.ims.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.InitialLdapContext;
import java.util.Hashtable;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:38
 */
@Component
public class LDAPLoginStrategy implements ILoginStrategy {

    private static Logger logger = LoggerFactory.getLogger(LDAPLoginStrategy.class);


    @Override
    public boolean auth(String userAccount, String password, String loginType, String appId) throws Exception{

        //TODO 暂时都设为false
        String ldapSsl = "false";
        String ldapPath = "false";

        DirContext ctx = null;
        Hashtable<String, String> env = new Hashtable<String, String>();
        if(ldapSsl.trim().equals("true")){
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        }
        env.put(Context.PROVIDER_URL, ldapPath.trim());
        //认证安全级别，此属性的值为 none、 simple 或 strong 关键字中的一个
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, getPrincipal(userAccount));
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

        logger.info("LDAP SECURITY_PRINCIPAL：" + getPrincipal(userAccount)+",LDAP PROVIDER_URL："+ldapPath.trim());
        try {
            if (ldapPath.contains("dc=")){
                ctx = new InitialDirContext(env);
            }
            else{
                env.put("com.sun.jndi.ldap.connect.pool","true");
                ctx = new InitialLdapContext(env,null);
            }
            logger.debug("验证成功！");
            return true;
        } catch (NamingException err) {
            String[] str = err.toString().split(":");
            if (str.length > 0) {
                String eCode = str[str.length-1];
                //用户不存在（code要改）
                if (eCode.contains("755")) {

                    throw new BusinessException("域用户不存在,请联系管理员！",err);
                }else if(eCode.contains("775")){
                    throw new BusinessException("域用户被锁定,请联系管理员！",err);
                }
                else if(eCode.contains("error code 34 - invalid DN")){
                    throw new BusinessException("请核对LDAP配置信息是否正确！",err);
                }
                else if(eCode.contains("error code 49 - Invalid Credentials")){
                    logger.error("LDAP登录认证，账号："+userAccount+",密码效验失败!");
                    throw new BusinessException("用户名或者密码错误！",err);
                }
                else if (err instanceof AuthenticationException) {
                    // 不同的LDAP服务器的错误返回信息似乎不同，上面的匹配会失败，这里额外再处理一次
                    // TODO 可能需要处理不同登陆失败原因，code49下还有不少子分类
                    logger.error("LDAP登录认证失败，账号：{}！", userAccount);
                    // 处理Active Directory的错误码

                    if (eCode.contains("data 52e") || eCode.contains("data 525")) {
                        throw new BusinessException("用户名或者密码错误！", err);
                    } else if (eCode.contains("data 530")) {
                        throw new BusinessException("此时不允许登陆！", err);
                    } else if (eCode.contains("data 531")) {
                        throw new BusinessException("在此工作站上不允许登陆！", err);
                    } else if (eCode.contains("data 532")) {
                        throw new BusinessException("密码过期！", err);
                    } else if (eCode.contains("data 533")) {
                        throw new BusinessException("账户禁用！", err);
                    } else if (eCode.contains("data 701")) {
                        throw new BusinessException("账户过期！", err);
                    } else if (eCode.contains("data 773")) {
                        throw new BusinessException("用户必须重置密码！", err);
                    } else if (eCode.contains("data 775")) {
                        throw new BusinessException("用户账户锁定！", err);
                    }

                    throw new BusinessException("LDAP登录认证失败，可能是用户名或者密码错误！", err);
                }
                else {
                    throw new BusinessException("请核对LDAP配置信息是否正确！",err);
                }
            }
            logger.error("Ldap auth failure！", err);
        }finally{
            try {
                if(ctx != null){
                    ctx.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            }
        }
        return true;
    }

    private String getPrincipal(String userName){
        //TODO 暂时写死
        String ldapAdDomain = "ldapAdDomain";
        logger.debug("environment ldapAdDomain:"+ldapAdDomain);
        return ldapAdDomain.trim().replace("{userName}", userName);
    }

}
