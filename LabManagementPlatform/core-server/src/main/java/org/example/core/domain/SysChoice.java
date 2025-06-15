package org.example.core.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 学生选课表
 * @TableName sys_choice
 */
@TableName(value ="sys_choice")
@Data
public class SysChoice extends BaseEntity implements Serializable {
    /**
     * 选课ID
     */
    @TableId(type = IdType.AUTO)
    private Long choiceId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程打分
     */
    private Integer score;

    /**
     * 课程评价
     */
    private String evaluate;

    /**
     * 上传资源
     */
    private String uploadResource;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}