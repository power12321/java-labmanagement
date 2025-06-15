package org.example.enrollmentserver.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.core.domain.BaseEntity;

import java.io.Serializable;

/**
 * 课程表
 * @TableName sys_course
 */
@TableName(value ="sys_course")
@Data
public class Course extends BaseEntity implements Serializable {
    /**
     * 课程ID
     */
    @TableId(type = IdType.AUTO)
    private Long courseId;

    /**
     * 课程编号
     */
    private String number;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程所属学院
     */
    private String college;

    /**
     * 课程所属专业
     */
    private String major;

    /**
     * 课时
     */
    private String period;

    /**
     * 学分
     */
    private String credits;

    /**
     * 课程类型 0-必修 1-选修
     */
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}