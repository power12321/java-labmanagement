package org.example.labserver.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.example.core.domain.Page;

import java.io.Serializable;

@Data
public class CourseQueryRequest  extends Page implements Serializable {
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
     * 课程所属学院
     */
    private String college;

    /**
     * 课程所属专业
     */
    private String major;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}