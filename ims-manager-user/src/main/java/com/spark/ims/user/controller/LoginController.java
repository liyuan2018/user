package com.spark.ims.user.controller;

import com.spark.ims.auth.constants.ITokenConstants;
import com.spark.ims.common.domain.ResultData;
import com.spark.ims.common.util.ParamUtils;
import com.spark.ims.common.util.StringUtils;
import com.spark.ims.core.annotation.Rest;
import com.spark.ims.core.controller.BaseController;
import com.spark.ims.core.domain.UserInfo;
import com.spark.ims.core.exception.BusinessException;
import com.spark.ims.core.util.AssertUtils;
import com.spark.ims.core.util.I18nUtils;
import com.spark.ims.user.constants.ErrorCode;
import com.spark.ims.user.dto.SysUserDTO;
import com.spark.ims.user.model.SysToken;
import com.spark.ims.user.model.SysUser;
import com.spark.ims.user.service.ILoginService;
import com.spark.ims.user.service.ISysTokenService;
import com.spark.ims.user.service.ISysUserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 20:58
 */
@Rest
@RequestMapping("/")
public class LoginController extends BaseController {

    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ILoginService loginService;
    @Autowired
    private ISysTokenService sysTokenService;
    @Autowired
    private SessionRepository sessionRepository;
    /**
     * 用户登入验证
     *
     * @param user
     * @return
     */
    @RequestMapping(value = "/sysUsers/login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResultData login(@RequestBody SysUserDTO user, HttpServletRequest request) throws Exception {
        String appId = request.getParameter(ITokenConstants.APP_ID_KEY);
        if (StringUtils.isEmpty(appId)) {
            appId = request.getHeader(ITokenConstants.APP_ID_KEY);
        }
        if(!StringUtils.isEmpty(user.getCaptchaValue())){
            HttpSession session = request.getSession();
            String captchaToken = (String) session.getAttribute("captchaToken");
            logger.debug("captchaToken:{},输入的值：{}",captchaToken,user.getCaptchaValue());
            if(!user.getCaptchaValue().equalsIgnoreCase(captchaToken)){
                throw new BusinessException(ErrorCode.Ims.captchaValidFailure);
            }
        }
        //1、username and password not null
        if (ParamUtils.isEmpty(user.getPassword()) || ParamUtils.isEmpty(user.getAccount())) {
            throw new BusinessException(I18nUtils.getMessage(ErrorCode.Ims.userOrPwdNotNull));
        }
        return  loginService.login(user.getAccount(),user.getPassword(),user.getLoginType(),appId);


    }


    @RequestMapping(value = "/sysUsers/logout", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResultData logout(HttpServletRequest request) throws Exception {
        ResultData resultData = new ResultData();
        try{
            Subject subject = SecurityUtils.getSubject();
            String sessionId = subject.getSession().getId().toString();
            subject.logout();
            //删除redis缓存
            sessionRepository.delete(sessionId);
            //更新sys_token登出时间
            sysTokenService.updateLogoutTime(sessionId);
            request.getSession().invalidate();
        }catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new BusinessException("用户登出异常！");
        }
        return  resultData;
    }


    /**
     * 根据token获取用户对象
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/public/tokens/{id}", method = RequestMethod.GET)
    public ResultData token(@PathVariable(value = "id") String id) throws Exception {

        AssertUtils.notNull(id);
        ResultData resultData = new ResultData();
        Session session = sessionRepository.getSession(id);
        if(session!=null) {
            UserInfo userInfo = session.getAttribute("user");
            if(userInfo!=null) {
                SysToken sysToken = sysTokenService.findByToken(id);
                SysUser sysUser = sysUserService.findByAccount(userInfo.getAccount());
                //session.
                resultData.put("sysUser", sysUser);
                resultData.put("token", id);

                if (sysToken != null) {
                    resultData.put("create_time", sysToken.getCreateTime());
                    resultData.put("expire", sysToken.getExpire());
                }
            }
        }
        return resultData;
    }

}

