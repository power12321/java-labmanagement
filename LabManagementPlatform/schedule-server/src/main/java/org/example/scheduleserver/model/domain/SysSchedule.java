package org.example.scheduleserver.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.core.domain.BaseEntity;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 排课表
 * @TableName sys_schedule
 */
@TableName(value ="sys_schedule")
@Data
public class SysSchedule extends BaseEntity implements Serializable {
    /**
     * 排课ID
     */
    @TableId(type = IdType.AUTO)
    private Long scheduleId;

    /**
     * 教师ID
     */
    @NotNull(message = "教师ID不能为空")
    private Long userId;

    /**
     * 课程ID
     */
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    /**
     * 课程开始时间
     */
    @NotNull(message = "课程开始时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 课程结束时间
     */
    @NotNull(message = "课程结束时间不能为空")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 实验课程指导书,实验报告模版上传
     */
    private String uploadResource;

    /**
     * 实验概论
     */
    private String content;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}