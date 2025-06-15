package com.example.device.vo;

import lombok.Data;

@Data
public class DeviceVO {
    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private String category;
    private Integer status;
    private Long teacherId;
    private String teacherName;  // 教师名称
} 