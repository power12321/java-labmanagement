package org.example.authserver.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserResponse implements Serializable {
    /**
     * 用户ID
     */
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
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}