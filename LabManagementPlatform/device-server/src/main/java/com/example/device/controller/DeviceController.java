package com.example.device.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.device.dto.DeviceApproveDTO;
import com.example.device.dto.DeviceQueryDTO;
import com.example.device.dto.DeviceBorrowDTO;
import com.example.device.entity.Device;
import com.example.device.service.DeviceService;
import com.example.device.vo.DeviceVO;
import com.example.device.vo.BorrowedDeviceVO;
import org.example.core.common.BaseResponse;
import org.example.core.common.ResultUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    /**
     * 分页查询设备
     * @param query
     * @return
     */
    @GetMapping("/page")
    public BaseResponse<IPage<DeviceVO>> queryDevicePage(DeviceQueryDTO query) {
        return ResultUtils.success(deviceService.queryDevicePage(query));
    }

    /**
     * 添加设备
     * @param device
     */
    @PostMapping
    public BaseResponse<Boolean> addDevice(@RequestBody Device device) {
        return ResultUtils.success(deviceService.addDevice(device));
    }

    /**
     * 更新设备
     * @param device
     */
    @PutMapping
    public BaseResponse<Boolean> updateDevice(@RequestBody Device device) {
        return ResultUtils.success(deviceService.updateDevice(device));
    }

    /**
     * 删除设备
     * @param id
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteDevice(@PathVariable Long id) {
        return ResultUtils.success(deviceService.deleteDevice(id));
    }

    /**
     * 教师借用设备
     * @param teacherId
     * @param borrowDTO
     */
    @PostMapping("/borrow/{teacherId}")
    public BaseResponse<Boolean> borrowDevices(@PathVariable Long teacherId, @RequestBody DeviceBorrowDTO borrowDTO) {
        return ResultUtils.success(deviceService.borrowDevices(teacherId, borrowDTO));
    }

    /**
     * 管理员查询借用设备列表
     * @return
     */
    @GetMapping("/borrowed")
    public BaseResponse<List<BorrowedDeviceVO>> queryBorrowedDevices() {
        return ResultUtils.success(deviceService.queryBorrowedDevices());
    }

    /**
     * 审批设备借用
     * @param dto
     */
    @PostMapping("/approve")
    public BaseResponse<Boolean> approveDeviceBorrow(@RequestBody DeviceApproveDTO dto) {
        return ResultUtils.success(deviceService.approveDeviceBorrow(dto));
    }
} 