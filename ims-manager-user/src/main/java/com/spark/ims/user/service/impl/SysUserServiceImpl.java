package com.spark.ims.user.service.impl;

import com.spark.ims.core.service.impl.BaseServiceImpl;
import com.spark.ims.user.mapper.SysUserMapper;
import com.spark.ims.user.model.SysUser;
import com.spark.ims.user.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 20:40
 */
@Service("sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl<SysUser, String>
        implements ISysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser findByAccount(String account){
        return  sysUserMapper.findByAccount(account);
    }

    public void otherAffairUpdate(SysUser user){
        sysUserMapper.save(user);
    }

}
