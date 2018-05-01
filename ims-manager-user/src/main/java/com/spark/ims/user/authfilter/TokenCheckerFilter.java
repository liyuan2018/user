package com.spark.ims.user.authfilter;

import com.alibaba.fastjson.JSONObject;
import com.spark.ims.common.domain.ErrorMessage;
import com.spark.ims.core.domain.UserInfo;
import com.spark.ims.core.util.I18nUtils;
import com.spark.ims.user.constants.ErrorCode;
import com.spark.ims.user.model.StatelessToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:09
 */
public class TokenCheckerFilter extends AccessControlFilter {

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {

        HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
        if(httpServletRequest.getSession()!=null&&httpServletRequest.getSession().getAttribute("user")!=null){
            UserInfo userInfo = (UserInfo)httpServletRequest.getSession().getAttribute("user");

            if(SecurityUtils.getSubject().getPrincipal()==null){
                StatelessToken token = new StatelessToken();
                token.setUserId(userInfo.getId());
                token.setToken(httpServletRequest.getSession().getId());
                SecurityUtils.getSubject().login(token);
            }
            return  true;
        }else{
            sessionRedirect(request,response);
            return false;
        }
    }

    private void sessionRedirect(ServletRequest request, ServletResponse response) throws Exception{

        if( ((HttpServletRequest) request).getHeader("x-auth-token") == null ) {
            Cookie c = new Cookie("my_app_auth_session", "");
            c.setMaxAge(2);
            c.setPath("/");
            ((HttpServletResponse) response).addCookie(c);
        }

        HttpServletResponse res = WebUtils.toHttp(response);
        String errorMsg = I18nUtils.getMessage(ErrorCode.Common.sessionTimeout);
        if(errorMsg==null){
            errorMsg = "会话已过期,请刷新页面重新登录！";
        }
        String result = JSONObject.toJSON(new ErrorMessage("000-B-"+ErrorCode.Common.sessionTimeout.getCode(), errorMsg)).toString();
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        res.setContentType("application/json");
        res.getWriter().write(result);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return  false;
    }



}
