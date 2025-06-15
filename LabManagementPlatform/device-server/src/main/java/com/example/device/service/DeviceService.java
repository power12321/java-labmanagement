package com.example.device.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.device.dto.DeviceApproveDTO;
import com.example.device.dto.DeviceQueryDTO;
import com.example.device.dto.DeviceBorrowDTO;
import com.example.device.entity.Device;
import com.example.device.vo.DeviceVO;
import com.example.device.vo.BorrowedDeviceVO;

import java.util.List;

public interface DeviceService {
    // 分页查询设备
    IPage<DeviceVO> queryDevicePage(DeviceQueryDTO query);
    
    // 添加设备
    Boolean addDevice(Device device);
    
    // 更新设备
    Boolean updateDevice(Device device);
    
    // 删除设备
    Boolean deleteDevice(Long id);
    
    // 教师借用设备
    Boolean borrowDevices(Long teacherId, DeviceBorrowDTO borrowDTO);
    
    // 查询借用设备列表
    List<BorrowedDeviceVO> queryBorrowedDevices();
    
    // 审批设备借用
    Boolean approveDeviceBorrow(DeviceApproveDTO dto);
} 