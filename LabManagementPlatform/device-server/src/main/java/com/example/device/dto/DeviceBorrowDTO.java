package com.example.device.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeviceBorrowDTO {
    private List<Long> deviceIds;  // 设备ID列表
} 