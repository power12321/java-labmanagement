package org.example.authserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.authserver.mapper.SysStudentMapper;
import org.example.authserver.model.domain.SysStudent;
import org.example.authserver.model.request.SysUserRequest;
import org.example.authserver.model.response.UserResponse;
import org.example.authserver.service.SysUserService;
import org.example.core.common.ErrorCode;
import org.example.core.domain.SysDictData;
import org.example.core.domain.SysUser;
import org.example.core.exception.BusinessException;
import org.example.core.mapper.SysDictDataMapper;
import org.example.core.mapper.SysUserMapper;
import org.example.core.util.AESUtils;
import org.example.core.util.EncodingUtils;
import org.example.core.util.JwtUtil;
import org.example.core.util.PageUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final SysUserMapper userMapper;

    private final SysStudentMapper studentMapper;

    private final SysDictDataMapper dictDataMapper;

    @Value("${auth.secret}")
    private String secret;

    @Override
    public String register(SysUserRequest request) {
        //1.手机号校验
        String regex = "^1[3-9]\\d{9}$";
        String phone = request.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            if (!phone.matches(regex)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "手机号输入有误，请重新输入");
            }
        }
        //2.密码加密
        String encryptPassword = AESUtils.encrypt(secret, request.getPassword());

        //3.验证该用户是否已经存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, request.getAccount());
        queryWrapper.eq(SysUser::getStatus, 0);
        SysUser sysUser = userMapper.selectOne(queryWrapper);
        if (sysUser != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "该用户已经存在，请重新输入");
        }

        //4.插入数据
        SysUser newUser = new SysUser();
        BeanUtils.copyProperties(request, newUser);
        newUser.setPassword(encryptPassword);
        newUser.setReviewStatus(0);//审核状态为未审核
        try {
            userMapper.insert(newUser);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.DATABASE_ERROR.getCode(), "用户信息插入数据库失败");
        }
        if (request.getRoleId() == 2L) {
            //学生的话，需要插入到学生表中
            SysStudent sysStudent = new SysStudent();
            BeanUtils.copyProperties(request, sysStudent);
            sysStudent.setUserId(newUser.getUserId());
            try {
                studentMapper.insert(sysStudent);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.DATABASE_ERROR.getCode(), "学生详情插入数据库失败");
            }
        }
        return "已提交注册申请，请等待管理员审核！";
    }

    @Override
    public JSONObject login(JSONObject jsonObject) {
        String account = jsonObject.getString("account");
        String password = jsonObject.getString("password");
        // 校验
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "账户或密码不能为空");
        }
        //查询用户是否存在
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        SysUser user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR.getCode(), "用户不存在");
        }
        if (user.getStatus() == 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR.getCode(), "账号已被禁用，请联系管理员");
        }
        if (user.getReviewStatus() == 1) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR.getCode(), "账号审核未通过，请联系管理员");
        }
        if (user.getReviewStatus() == 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR.getCode(), "账号还未审核，请联系管理员");
        }
        //密码是否正确
        String decrypt = AESUtils.decrypt(secret, user.getPassword());
        if (!decrypt.equals(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "密码错误，请重新输入！");
        }
        //给这个用户生成token
        JSONObject userObj = new JSONObject();
        userObj.put("userId", user.getUserId());
        userObj.put("roleId", user.getRoleId());
        userObj.put("account", user.getAccount());
        userObj.put("name", user.getName());
        String token = JwtUtil.createToken(userObj, secret);
        userObj.put("token", token);
        return userObj;
    }

    @Override
    public JSONObject getLocalUserInfo(HttpServletRequest request) {
        JSONObject userObj = new JSONObject();
        String encodedName = request.getHeader("name");
        String decodedName = EncodingUtils.decodeChinese(encodedName);
        userObj.put("userId", request.getHeader("userId"));
        userObj.put("roleId", request.getHeader("roleId"));
        userObj.put("account", request.getHeader("account"));
        userObj.put("name", decodedName);
        return userObj;
    }

    @Override
    public Boolean userLogoff(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        JwtUtil.invalidateToken(token);
        return true;
    }

    @Override
    public Boolean updateReviewStatus(Long userId, Integer status) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        if (status == 0) {
            sysUser.setReviewStatus(1);
        } else if (status == 1) {
            sysUser.setReviewStatus(2);
        }
        userMapper.updateById(sysUser);
        return true;
    }

    @Override
    public PageUtil<UserResponse> queryReviewByPage(JSONObject jsonObject) {
        Integer current = jsonObject.getInteger("current");
        Integer size = jsonObject.getInteger("size");
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getStatus, 0);
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        List<SysUser> userList = userMapper.selectList(queryWrapper);
        List<UserResponse> userResponses = userList.stream().map(user -> {
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);
            return userResponse;
        }).collect(Collectors.toList());
        return new PageUtil<>(current, size, userResponses);
    }

    @Override
    public PageUtil<JSONObject> queryTeacherByPage(JSONObject jsonObject) {
        Integer current = jsonObject.getInteger("current");
        Integer size = jsonObject.getInteger("size");
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        //查询审核通过的教师
        queryWrapper.eq(SysUser::getRoleId, 1);
        queryWrapper.eq(SysUser::getReviewStatus, 2);
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        List<SysUser> userList = userMapper.selectList(queryWrapper);
        List<JSONObject> userResponses = userList.stream().map(user -> {
            JSONObject userObj = new JSONObject();
            userObj.put("userId", user.getUserId());
            userObj.put("account", user.getAccount());
            userObj.put("name", user.getName());
            userObj.put("sex", user.getSex());
            userObj.put("phone", user.getPhone());
            userObj.put("remark", user.getRemark());
            return userObj;
        }).collect(Collectors.toList());
        return new PageUtil<>(current, size, userResponses);
    }

    /**
     * 查询学生列表-做了性能优化，通过在循环里直接从Map中获取关联数据，避免重复查询，减少了数据库查询次数
     *
     * @param jsonObject 分页信息
     * @return
     */
    @Override
    public PageUtil<JSONObject> queryStudentByPage(JSONObject jsonObject) {
        Integer current = jsonObject.getInteger("current");
        Integer size = jsonObject.getInteger("size");
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        //查询审核通过的学生
        queryWrapper.eq(SysUser::getRoleId, 2);
        queryWrapper.eq(SysUser::getReviewStatus, 2);
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        List<SysUser> userList = userMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(userList)) {
            return new PageUtil<>(current, size, new ArrayList<>());
        }

        // 提取所有用户 ID
        List<Long> userIds = userList.stream().map(SysUser::getUserId).collect(Collectors.toList());
        // 批量查询学生信息
        LambdaQueryWrapper<SysStudent> studentQueryWrapper = new LambdaQueryWrapper<>();
        studentQueryWrapper.in(SysStudent::getUserId, userIds);
        List<SysStudent> studentList = studentMapper.selectList(studentQueryWrapper);
        Map<Long, SysStudent> studentMap = studentList.stream().collect(Collectors.toMap(SysStudent::getUserId, student -> student));

        // 提取所有学院和专业的值
        Set<String> collegeValues = new HashSet<>();
        Set<String> majorValues = new HashSet<>();
        for (SysStudent student : studentList) {
            collegeValues.add(student.getCollege());
            majorValues.add(student.getMajor());
        }

        // 批量查询学院字典数据
        LambdaQueryWrapper<SysDictData> collegeQueryWrapper = new LambdaQueryWrapper<>();
        collegeQueryWrapper.in(SysDictData::getDictValue, collegeValues);
        collegeQueryWrapper.eq(SysDictData::getDictType, "college");
        List<SysDictData> collegeDictList = dictDataMapper.selectList(collegeQueryWrapper);
        Map<String, String> collegeDictMap = collegeDictList.stream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel));

        // 批量查询专业字典数据
        LambdaQueryWrapper<SysDictData> majorQueryWrapper = new LambdaQueryWrapper<>();
        majorQueryWrapper.in(SysDictData::getDictValue, majorValues);
        majorQueryWrapper.eq(SysDictData::getDictType, "major");
        List<SysDictData> majorDictList = dictDataMapper.selectList(majorQueryWrapper);
        Map<String, String> majorDictMap = majorDictList.stream().collect(Collectors.toMap(SysDictData::getDictValue, SysDictData::getDictLabel));

        // 组装结果
        List<JSONObject> userResponses = userList.stream().map(user -> {
            JSONObject userObj = new JSONObject();
            userObj.put("userId", user.getUserId());
            userObj.put("account", user.getAccount());
            userObj.put("name", user.getName());
            userObj.put("sex", user.getSex());
            userObj.put("phone", user.getPhone());
            userObj.put("remark", user.getRemark());

            SysStudent student = studentMap.get(user.getUserId());
            if (student != null) {
                userObj.put("grade", student.getGrade());
                userObj.put("college", collegeDictMap.get(student.getCollege()));
                userObj.put("major", majorDictMap.get(student.getMajor()));
            }
            return userObj;
        }).collect(Collectors.toList());
        return new PageUtil<>(current, size, userResponses);
    }
}




