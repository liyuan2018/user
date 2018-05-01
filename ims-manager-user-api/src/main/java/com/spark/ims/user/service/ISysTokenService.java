package com.spark.ims.user.service;

import com.spark.ims.service.IBaseService;
import com.spark.ims.user.model.SysToken;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:09
 */
public interface ISysTokenService extends IBaseService<SysToken, String> {

     SysToken findByToken(String token);

     void updateLogoutTime(String token);


}
