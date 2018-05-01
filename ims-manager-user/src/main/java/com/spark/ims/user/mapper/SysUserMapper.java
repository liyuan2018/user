package com.spark.ims.user.mapper;

import com.spark.ims.core.repository.BaseJpaRepository;
import com.spark.ims.user.model.SysUser;

import java.util.List;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:43
 */
public interface SysUserMapper extends BaseJpaRepository<SysUser, String> {

    /**
     * 根据用户名查询用户
     *
     * @param account
     * @return
     */
    public SysUser findByAccount(String account);

}

