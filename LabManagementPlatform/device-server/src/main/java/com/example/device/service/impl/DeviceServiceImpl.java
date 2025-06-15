package com.example.device.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.device.dto.DeviceApproveDTO;
import com.example.device.dto.DeviceQueryDTO;
import com.example.device.dto.DeviceBorrowDTO;
import com.example.device.entity.Device;
import com.example.device.mapper.DeviceMapper;
import com.example.device.service.DeviceService;
import com.example.device.vo.DeviceVO;
import com.example.device.vo.BorrowedDeviceVO;
import org.example.core.domain.SysUser;
import org.example.core.exception.BusinessException;
import org.example.core.mapper.SysUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DeviceServiceImpl implements DeviceService {

    @Resource
    private DeviceMapper deviceMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<DeviceVO> queryDevicePage(DeviceQueryDTO query) {
        Page<Device> page = new Page<>(query.getPageNum(), query.getPageSize());
        IPage<Device> devicePage = deviceMapper.selectDevicePage(page, query);

        LambdaQueryWrapper<SysUser> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SysUser::getRoleId, 1L);
        List<SysUser> sysUsers = sysUserMapper.selectList(wrapper1);
        Map<Long, String> teacherNameMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getName));

        return devicePage.convert(device -> {
            DeviceVO vo = new DeviceVO();
            BeanUtils.copyProperties(device, vo);
            vo.setTeacherName(teacherNameMap.get(device.getTeacherId()));
            return vo;
        });
    }

    @Override
    public Boolean addDevice(Device device) {
        // 检查设备名称、设备编号、设备分类是否已存在，如果存在则抛出异常
        checkExists("device_name", device.getDeviceName(), "设备名称已存在");
        checkExists("device_code", device.getDeviceCode(), "设备编号已存在");
        device.setStatus(0); // 初始状态为待借用
        try {
            deviceMapper.insert(device);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // 封装通用的检查方法
    private void checkExists(String fieldGetter, String device, String errorMessage) {
        QueryWrapper<Device> wrapper = new QueryWrapper<>();
        wrapper.eq(fieldGetter, device);
        Device result = deviceMapper.selectOne(wrapper);
        if (result != null) {
            throw new BusinessException(-1, errorMessage);
        }
    }

    @Override
    public Boolean updateDevice(Device device) {
        // 检查设备名称、设备编号、设备分类是否已存在，如果存在则抛出异常
        checkExists("device_name", device.getDeviceName(), "设备名称已存在", device.getDeviceId());
        checkExists("device_code", device.getDeviceCode(), "设备编号已存在", device.getDeviceId());
        try {
            deviceMapper.updateById(device);
            // 如果设备状态为0（待借用），则清除教师ID
            if (device.getStatus() != null && device.getStatus() == 0) {
                UpdateWrapper<Device> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("teacher_id", null);
                updateWrapper.eq("device_id", device.getDeviceId());
                deviceMapper.update(null, updateWrapper);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void checkExists(String fieldName, String fieldValue, String errorMessage, Long currentDeviceId) {
        QueryWrapper<Device> wrapper = new QueryWrapper<>();
        wrapper.eq(fieldName, fieldValue);
        // 排除当前要更新的设备
        if (currentDeviceId != null) {
            wrapper.ne("device_id", currentDeviceId);
        }
        Device result = deviceMapper.selectOne(wrapper);
        if (result != null) {
            throw new BusinessException(-1, errorMessage);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDevice(Long id) {
        Device device = deviceMapper.selectById(id);
        if (device != null && device.getStatus() != 0) {
            throw new BusinessException(-1, "该设备已被教师借用或申请借用中，无法删除");
        }
        try {
            deviceMapper.deleteById(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean borrowDevices(Long teacherId, DeviceBorrowDTO borrowDTO) {
        if (CollectionUtils.isEmpty(borrowDTO.getDeviceIds())) {
            return true;
        }
        try {
            List<Device> devices = deviceMapper.selectBatchIds(borrowDTO.getDeviceIds());
            for (Device device : devices) {
                if (device.getStatus() != 0) {
                    throw new BusinessException(-1, "设备" + device.getDeviceName() + "当前不可借用");
                }
                device.setTeacherId(teacherId);
                device.setStatus(2); // 设置为申请借用中状态
                deviceMapper.updateById(device);
            }
        } catch (BusinessException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<BorrowedDeviceVO> queryBorrowedDevices() {
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.isNotNull(Device::getTeacherId);
        wrapper.eq(Device::getStatus, 2);// 申请借用中状态
        List<Device> devices = deviceMapper.selectList(wrapper);

        LambdaQueryWrapper<SysUser> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SysUser::getRoleId, 1L);
        List<SysUser> sysUsers = sysUserMapper.selectList(wrapper1);
        Map<Long, String> teacherNameMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getUserId, SysUser::getName));

        // 按教师ID分组
        Map<Long, List<Device>> teacherDeviceMap = devices.stream()
                .collect(Collectors.groupingBy(Device::getTeacherId));

        List<BorrowedDeviceVO> result = new ArrayList<>();
        teacherDeviceMap.forEach((teacherId, deviceList) -> {
            BorrowedDeviceVO vo = new BorrowedDeviceVO();
            vo.setTeacherId(teacherId);
            vo.setTeacherName(teacherNameMap.get(teacherId));
            vo.setDeviceIds(deviceList.stream().map(Device::getDeviceId).collect(Collectors.toList()));
            vo.setDeviceNames(deviceList.stream().map(Device::getDeviceName).collect(Collectors.toList()));
            result.add(vo);
        });

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean approveDeviceBorrow(DeviceApproveDTO dto) {
        try {
            List<Long> deviceIds = dto.getDeviceIds();
            deviceIds.forEach(deviceId -> {
                Device device = deviceMapper.selectById(deviceId);
                if (device == null) {
                    throw new BusinessException(-1, "设备不存在");
                }

                Integer status;
                if (dto.isApproved()) {
                    status = 1; // 设置为借用中
                } else {
                    status = 0; // 设置为待借用
                }

                UpdateWrapper<Device> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("status", status);
                if (!dto.isApproved()) {
                    updateWrapper.set("teacher_id", null);
                }
                updateWrapper.eq("device_id", deviceId);
                deviceMapper.update(null, updateWrapper);
            });
        } catch (Exception e) {
            return false;
        }
        return true;
    }
} 