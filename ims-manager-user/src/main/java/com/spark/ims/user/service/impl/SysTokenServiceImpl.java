package com.spark.ims.user.service.impl;

import com.spark.ims.core.service.impl.BaseServiceImpl;
import com.spark.ims.user.repository.SysTokenRepository;
import com.spark.ims.user.model.SysToken;
import com.spark.ims.user.service.ISysTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:30
 */
@Service("sysTokenService")
public class SysTokenServiceImpl extends BaseServiceImpl<SysToken, String> implements ISysTokenService {


    @Autowired
    private SysTokenRepository sysTokenMapper;

    public SysToken findByToken(String token){
        return  sysTokenMapper.findByToken(token);
    }


    public void updateLogoutTime(String token){
        Map<String , Object> params = new HashMap<String,Object>();
        params.put("token",token);
        params.put("logoutTime",new Date());
        SysToken sysToken = sysTokenMapper.findByToken(token);
        sysToken.setLogoutTime(new Date());
    }
}
