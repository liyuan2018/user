package com.spark.ims.user.service.impl;

import com.spark.ims.common.util.DESUtils;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 22:58
 */
public class Test {
    public static void main(String[] args) {
        DESUtils des = new DESUtils();
        String key = "tangxing";
        des.encrypt("123456", key);
        String password = des.decrypt(des.encrypt("123456", key),key);
        System.out.print(password);
    }
}
