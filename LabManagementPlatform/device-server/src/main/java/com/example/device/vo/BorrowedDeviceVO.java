package com.example.device.vo;

import lombok.Data;
import java.util.List;

@Data
public class BorrowedDeviceVO {
    private Long teacherId;
    private String teacherName;
    private List<Long> deviceIds;
    private List<String> deviceNames;
} 