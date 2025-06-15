package org.example.authserver.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.core.domain.BaseEntity;

import java.io.Serializable;

/**
 * 学生详情表
 * @TableName sys_student
 */
@TableName(value ="sys_student")
@Data
public class SysStudent extends BaseEntity implements Serializable {
    /**
     * 学生详情ID
     */
    @TableId(type = IdType.AUTO)
    private Long studentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业
     */
    private String major;

    /**
     * 班级
     */
    private String grade;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}