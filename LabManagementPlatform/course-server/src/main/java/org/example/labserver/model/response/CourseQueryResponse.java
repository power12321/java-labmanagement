package org.example.labserver.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class CourseQueryResponse implements Serializable {
    /**
     * 课程ID
     */
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
     * 课程所属学院id
     */
    private String collegeId;

    /**
     * 课程所属学院名称
     */
    private String collegeLabel;

    /**
     * 课程所属专业id
     */
    private String majorId;

    /**
     * 课程所属专业名称
     */
    private String majorLabel;

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

    /**
     * 打分和评价
     */
    //private List<JSONObject> scoreAndEvaluateList = new ArrayList<>();

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}