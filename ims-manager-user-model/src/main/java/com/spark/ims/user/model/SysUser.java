package com.spark.ims.user.model;

import com.spark.ims.common.domain.BaseModel;
import com.spark.ims.common.listener.IDeleteListenable;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;


/**
 * 描述：
 *
 * @authhor liyuan
 * @data 2018/5/1 21:10
 */
@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "sys_user")
@Where(clause = "status > '0'")
public class SysUser extends BaseModel implements IDeleteListenable {

    /**
     *
     */
    private static final long serialVersionUID = 2681840209772847421L;

    /**
     * 主机构
     */
    @Column
    //@NotEmpty(message = "所属机构ID不允许为空")
    @Size(max = 32)
    private String orgId;

    /**
     * 主部门
     */
    @Column
    @Size(max = 32)
    private String deptId;

    /**
     * 姓名
     */
    @Column
    @NotEmpty(message = "姓名不能为空")
    @Size(max = 50)
    private String name;

    /**
     * 帐号
     */
    @Column
    @NotEmpty(message = "帐号不能为空")
    @Pattern(regexp = "[^`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]{1,}", message = "帐号只能由中文英文字母、数字、下划线和中划线组成")
    @Size(max = 50)
    private String account;

    /**
     * 微信账号
     */
    @Column
    @Size(max = 50)
    private String wechatNo;

    /**
     * 密码
     */
    @Column
    //@NotEmpty(message = "密码不能为空")
    @Size(max = 50, min = 6, message = "密码长度必须大于6位")
    private String password;

    /**
     * 性别
     */
    @Column
    @Pattern(regexp = "^[1-2]{1}$")
    private String sex;

    /**
     * 用户状态
     */
    @Column
    @Pattern(regexp = "^[0-3]{1}$")
    private String status;

    /**
     * 邮箱
     */
    @Column
    @Size(max = 50)
    @Email(message = "请输入正确的邮箱帐号")
    private String email;

    /**
     * 地址
     */
    @Column
    @Size(max = 300)
    private String address;

    /**
     * 办公室电话
     */
    @Column
    @Size(max = 50)
    private String officePhone;

    /**
     * 家庭电话
     */
    @Column
    @Size(max = 50)
    private String homePhone;

    /**
     * 手机号码
     */
    @Column
//	@Pattern(regexp = "^[0-9]*$", message = "请输入正确的手机号码")
    private String mobile;

    /**
     * 登陆错误次数
     */
    @Column
    private Integer errorCount;

    /**
     * 修改时间
     */
    @Column
    private Date modified;

    /**
     * 描述
     */
    @Column
    @Size(max = 300)
    private String description;

    /**
     * 最后登陆错误时间
     */
    @Column
    private Date errorTime;

    /**
     * 头像
     */
    @Column
    @Size(max = 255)
    private String avatar;

    @Column
    private String employeeId;

    /**
     * RTX账号
     */
    @Column
    private String rtxNo;

    @Column
    private String supplyCompanyId;

    /**
     * 是否外包人员
     */
    @Column
    private String isOutResourceUser;

    @Transient
    private String loginConfirm;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(Date errorTime) {
        this.errorTime = errorTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLoginConfirm() {
        return loginConfirm;
    }

    public void setLoginConfirm(String loginConfirm) {
        this.loginConfirm = loginConfirm;
    }

    public String getWechatNo() {
        return wechatNo;
    }

    public void setWechatNo(String wechatNo) {
        this.wechatNo = wechatNo;
    }

    public String getRtxNo() {
        return rtxNo;
    }

    public void setRtxNo(String rtxNo) {
        this.rtxNo = rtxNo;
    }

    public String getSupplyCompanyId() {
        return supplyCompanyId;
    }

    public void setSupplyCompanyId(String supplyCompanyId) {
        this.supplyCompanyId = supplyCompanyId;
    }

    public String getIsOutResourceUser() {
        return isOutResourceUser;
    }

    public void setIsOutResourceUser(String isOutResourceUser) {
        this.isOutResourceUser = isOutResourceUser;
    }
}
