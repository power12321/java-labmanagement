package com.example.device.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.core.domain.BaseEntity;

@Data
@TableName("sys_device")
public class Device extends BaseEntity {
    @TableId(value = "device_id", type = IdType.AUTO)
    private Long deviceId;
    
    private String deviceCode;    // 设备编号
    private String deviceName;    // 设备名称
    private String category;      // 设备分类
    private Integer status;       // 设备状态：0-待借用，1-借用中 2-申请中

    private Long teacherId;       // 借用教师ID
} 