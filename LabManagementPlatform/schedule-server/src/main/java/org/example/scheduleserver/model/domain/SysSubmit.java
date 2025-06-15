package org.example.scheduleserver.model.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 学生提交实验表
 */
@TableName(value ="sys_submit")
@Data
public class SysSubmit {

    private Long userId;

    private Long courseId;

    private String submitResource;
}
