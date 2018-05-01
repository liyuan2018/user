package com.spark.ims.user.constants;

import com.spark.ims.common.constants.IMessage;

/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:16
*/
public class ErrorCode implements com.spark.ims.common.constants.ErrorCode {

    public static enum Ims implements IMessage{

        orgHasUser(1200),
        deptHasUser(1201),
        dutyHasPosition(1202),
        avatarNotConform(1203),
        userOrPwdNotNull(1204),
        userDisabled(1205),
        loginErrorLocked(1206),
        loginErrorChance(1207),
        userOrPwdNotMatch(1208),
        loginConfirmPrompt(1209),
        calendarNotFound(1210),
        captchaValidFailure(1211),
        captchaMakeFailure(1212),
        deleteSysRoleCheck(1213),
        certificateInvalid(1214);
        private int code;
        private String category;
        Ims(int code){
            this.code = code;
            this.category = this.getClass().getSimpleName();
        }
        public int getCode(){
            return code;
        }
        public String getCategory(){return category;}
    }

}
