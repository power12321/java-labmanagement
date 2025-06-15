package com.example.device.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeviceApproveDTO {

    private List<Long> deviceIds;  // 设备ID列表
    private boolean approved;  // 是否批准借用
}
