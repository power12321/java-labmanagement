package com.example.device.dto;

import lombok.Data;

@Data
public class DeviceQueryDTO {
    private String deviceCode;    // 设备编号
    private String deviceName;    // 设备名称
    private String category;      // 设备分类
    private Integer status;       // 设备状态
    private Integer pageNum = 1;  // 当前页码
    private Integer pageSize = 10; // 每页大小
} 