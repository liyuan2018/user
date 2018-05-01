package com.spark.ims.user.strategy;

import com.spark.ims.auth.strategy.ILoginStrategy;
import com.spark.ims.common.constants.DataDict;
import com.spark.ims.common.util.MD5Utils;
import com.spark.ims.common.util.Md5PasswordEncoder;
import com.spark.ims.common.util.ParamUtils;
import com.spark.ims.core.util.I18nUtils;
import com.spark.ims.user.constants.ErrorCode;
import com.spark.ims.user.model.SysUser;
import com.spark.ims.user.service.ISysUserService;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:22
 */
@Component
public class LocalLoginStrategy implements ILoginStrategy {
    private static Logger logger = LoggerFactory.getLogger(LocalLoginStrategy.class);

    @Autowired
    private ISysUserService sysUserService;

    @Override
    public boolean auth(String userAccount, String password, String loginType, String appId) throws Exception{

        //双重MD5加盐加密
        String enPassword = new Md5PasswordEncoder(userAccount).encode(MD5Utils.encrypt(new String(password)));

        //TODO 暂时不实现
        String launchFlag =  "true";
        int count = 1;
        int time = 0;
        int lockedMinute =  time/60 >0 ?time/60 :1 ;
        String errorLocked = I18nUtils.getMessage(ErrorCode.Ims.loginErrorLocked,new String[]{ lockedMinute+"" });
        String errorMessageUserOrPwdNotMatch = I18nUtils.getMessage(ErrorCode.Ims.userOrPwdNotMatch);

        //2、user is exist?
        SysUser sysUser = sysUserService.findByAccount(userAccount);

        if (ParamUtils.isNull(sysUser)) {
            logger.error("本地登录认证，账号："+userAccount+"，匹配不到!");
            throw new UnknownAccountException(errorMessageUserOrPwdNotMatch);
        }
        if(sysUser.getErrorCount()==null){
            sysUser.setErrorCount(0);
        }
        Date errorTime = sysUser.getErrorTime()==null?new Date():sysUser.getErrorTime();
        Date currentTime = new Date();
        long betweenTime = (currentTime.getTime() - errorTime.getTime())/1000;//时间差，单位秒

        //3、user is locked or forbidden?
        if (sysUser.getStatus().equals(DataDict.SysUserStatus.DISABLE.toString())) {
            throw new LockedAccountException(I18nUtils.getMessage(ErrorCode.Ims.userDisabled));
        }
        if (sysUser.getStatus().equals(DataDict.SysUserStatus.LOCK.toString())) {
            if(betweenTime < time){
                throw new LockedAccountException(errorLocked);
            }
        }
        //4、username and password  match?
        if (!userAccount.equals(sysUser.getAccount()) || !enPassword.equals(sysUser.getPassword())) {
            int loginCount = 0;
            String errorMessage = errorMessageUserOrPwdNotMatch;
            if(launchFlag.equals("true")){
                if(betweenTime >= time){
                    sysUser.setErrorCount(1);
                    sysUser.setErrorTime(currentTime);
                    sysUser.setStatus(DataDict.SysUserStatus.ENABLE.toString());
                    loginCount = count - sysUser.getErrorCount();
                    errorMessage = I18nUtils.getMessage(ErrorCode.Ims.loginErrorChance,new Object[]{loginCount});
                }else{
                    sysUser.setErrorCount(sysUser.getErrorCount()+1);
                    sysUser.setErrorTime(currentTime);
                    if(sysUser.getErrorCount() == count){
                        sysUser.setStatus(DataDict.SysUserStatus.LOCK.toString());
                        errorMessage = errorLocked;
                    }else{
                        loginCount = count - sysUser.getErrorCount();
                        errorMessage = I18nUtils.getMessage(ErrorCode.Ims.loginErrorChance,new Object[]{loginCount});
                    }
                }
                //新启事务，登录错误，更改用户错误次数
                sysUserService.otherAffairUpdate(sysUser);
            }
            logger.error("本地登录认证，账号："+userAccount+"，密码效验失败!");
            throw new AccountException(errorMessage);

        }else{
            if (sysUser.getStatus().equals(DataDict.SysUserStatus.LOCK.toString())) {
                sysUser.setStatus(DataDict.SysUserStatus.ENABLE.toString());
            }
            sysUser.setErrorCount(0);
            sysUserService.update(sysUser);
        }
        return true;
    }
}
