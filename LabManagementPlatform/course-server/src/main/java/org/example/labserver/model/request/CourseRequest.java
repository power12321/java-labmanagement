package org.example.labserver.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class CourseRequest implements Serializable {
    /**
     * 课程ID 更新时候传入
     */
    private Long courseId;

    /**
     * 课程编号
     */
    @NotBlank(message = "课程编号不能为空")
    private String number;

    /**
     * 课程名称
     */
    @NotBlank(message = "课程名称不能为空")
    private String courseName;

    /**
     * 课程所属学院
     */
    @NotBlank(message = "课程所属学院不能为空")
    private String college;

    /**
     * 课程所属专业
     */
    @NotBlank(message = "课程所属专业不能为空")
    private String major;

    /**
     * 课时
     */
    @NotBlank(message = "课时不能为空")
    private String period;

    /**
     * 学分
     */
    @NotBlank(message = "学分不能为空")
    private String credits;

    /**
     * 课程类型 0-必修 1-选修
     */
    @NotNull(message = "课程类型不能为空")
    private Integer type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}