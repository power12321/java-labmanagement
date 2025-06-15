package org.example.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息表
 * @TableName sys_user
 */
@TableName(value ="sys_user")
@Data
public class SysUser extends BaseEntity implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 角色ID 0-管理员 1-教师 2-学生
     */
    private Long roleId;

    /**
     * 用户账号 学号/工号
     */
    private String account;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 性别（1男 2女）
     */
    private Integer sex;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 密码
     */
    private String password;

    /**
     * 审核状态 0-未审核 1-审核未通过 2-审核通过 3-无需审核
     */
    private Integer reviewStatus;

    /**
     * 帐号状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 是否逻辑删除 0否 1是
     */
    private Integer deleted;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}