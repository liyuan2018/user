package com.spark.ims.user.repository;

import com.spark.ims.core.repository.BaseJpaRepository;
import com.spark.ims.user.model.SysUser;
import org.springframework.stereotype.Repository;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:43
 */
@Repository
public interface SysUserRepository extends BaseJpaRepository<SysUser, String> {

    /**
     * 根据用户名查询用户
     *
     * @param account
     * @return
     */
    public SysUser findByAccount(String account);

}

