package org.example.authserver.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.example.authserver.model.request.SysUserRequest;
import org.example.authserver.model.response.UserResponse;
import org.example.authserver.service.SysUserService;
import org.example.core.common.BaseResponse;
import org.example.core.common.ResultUtils;
import org.example.core.util.PageUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class SysUserController {

    private final SysUserService userService;

    /**
     * 注册用户信息
     * @param request 用户请求信息
     * @return 成功信息
     */
    @PostMapping("/register")
    public BaseResponse<String> register(@RequestBody @Valid SysUserRequest request) {
        return ResultUtils.success(userService.register(request));
    }

    /**
     * 用户登录
     * @param jsonObject 登录信息
     * @return 登录结果信息
     */
    @PostMapping("/login")
    public BaseResponse<JSONObject> login(@RequestBody JSONObject jsonObject) {
        return ResultUtils.success(userService.login(jsonObject));
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    @GetMapping(value="/getLocalUserInfo",produces = "application/json; charset=UTF-8")
    public BaseResponse<JSONObject> getLocalUserInfo(HttpServletRequest request) {
        return ResultUtils.success(userService.getLocalUserInfo(request));
    }

    /**
     * 退出登录
     * @param request 请求信息
     * @return 成功信息
     */
    @GetMapping("/userLogoff")
    public BaseResponse<Boolean> userLogoff(HttpServletRequest request) {
        return ResultUtils.success(userService.userLogoff(request));
    }

    /**
     * 修改审核状态
     * @param userId 用户ID
     * @param status 0-不通过 1-通过
     * @return 是否成功
     */
    @GetMapping("/updateReviewStatus")
    public BaseResponse<Boolean> updateReviewStatus(Long userId,Integer status) {
        return ResultUtils.success(userService.updateReviewStatus(userId,status));
    }

    /**
     * 审核管理列表
     * @param jsonObject 分页信息
     * @return 审核管理列表
     */
    @PostMapping("/queryReviewByPage")
    public BaseResponse<PageUtil<UserResponse>> queryReviewByPage(@RequestBody JSONObject jsonObject) {
        return ResultUtils.success(userService.queryReviewByPage(jsonObject));
    }

    /**
     * 查询教师列表
     * @param jsonObject 分页信息
     * @return 教师列表
     */
    @PostMapping("/queryTeacherByPage")
    public BaseResponse<PageUtil<JSONObject>> queryTeacherByPage(@RequestBody JSONObject jsonObject) {
        return ResultUtils.success(userService.queryTeacherByPage(jsonObject));
    }

    /**
     * 查询学生列表
     * @param jsonObject 分页信息
     * @return 学生列表
     */
    @PostMapping("/queryStudentByPage")
    public BaseResponse<PageUtil<JSONObject>> queryStudentByPage(@RequestBody JSONObject jsonObject) {
        return ResultUtils.success(userService.queryStudentByPage(jsonObject));
    }
}
