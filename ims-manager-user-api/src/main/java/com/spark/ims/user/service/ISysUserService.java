package com.spark.ims.user.service;

import com.spark.ims.service.IBaseService;
import com.spark.ims.user.model.SysUser;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 20:39
 */
public interface ISysUserService extends IBaseService<SysUser,String>{

    /**
     * 根据账号查找用户
     * @param account
     * @return
     * @throws Exception
     */
    public SysUser findByAccount(String account);


    void otherAffairUpdate(SysUser user);
}
