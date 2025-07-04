package org.example.scheduleserver.model.response;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ScheduleResponse implements Serializable {
    /**
     * 排课ID
     */
    private Long scheduleId;

    /**
     * 教师ID
     */
    private Long userId;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 课程ID
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程编号
     */
    private String courseNumber;

    /**
     * 课程开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 课程结束时间
     */
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

    /**
     * 学生提交的实验作业
     */
    private List<JSONObject> submitResources;

    /**
     * 打分和评价
     */
    private List<JSONObject> scoreAndEvaluateList = new ArrayList<>();


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}