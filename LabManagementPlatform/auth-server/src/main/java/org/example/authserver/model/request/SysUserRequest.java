package org.example.authserver.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SysUserRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空")
    private String account;

    /**
     * 用户姓名
     */
    @NotBlank(message = "用户姓名不能为空")
    private String name;

    /**
     * 角色ID 0-管理员 1-教师 2-学生
     */
    @NotNull(message = "角色不能为空")
    private Long roleId;

    /**
     * 密码
     */
    @NotBlank(message = "用户密码不能为空")
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别（1男 2女）
     */
    @NotNull(message = "性别不能为空")
    private Integer sex;

    /**
     * 学生-学院
     */
    private String college;

    /**
     * 学生-专业
     */
    private String major;

    /**
     * 学生-年级
     */
    private String grade;
}
