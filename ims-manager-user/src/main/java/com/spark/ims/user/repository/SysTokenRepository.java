package com.spark.ims.user.repository;

import com.spark.ims.core.repository.BaseJpaRepository;
import com.spark.ims.user.model.SysToken;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:45
 */
@Repository
public interface SysTokenRepository extends BaseJpaRepository<SysToken, String> {

    /**
     * 根据用户ID查询在线或未正常
     * @param userId
     */
    public List<SysToken> findByUserIdAndLogoutTimeIsNull(String userId);

    /**
     * 根据用户ID查询
     *
     * @param token
     */
    public SysToken findByToken(String token);
}
