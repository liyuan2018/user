package com.spark.ims.user.service;

import com.spark.ims.common.domain.ResultData;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:08
 */
public interface ILoginService {

    ResultData login(String account, String password, String loginType, String appId) throws Exception;


    ResultData createSessionUser(String account, String appId);

}
