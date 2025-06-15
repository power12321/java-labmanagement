package org.example.authserver.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.authserver.model.request.SysUserRequest;
import org.example.authserver.model.response.UserResponse;
import org.example.core.domain.SysUser;
import org.example.core.util.PageUtil;

import javax.servlet.http.HttpServletRequest;

public interface SysUserService extends IService<SysUser> {

    /**
     * 注册用户
     * @param request 用户请求信息
     * @return 成功信息
     */
    String register(SysUserRequest request);

    /**
     * 用户登录
     * @param jsonObject 登录信息
     * @return 登录结果信息
     */
    JSONObject login(JSONObject jsonObject);

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    JSONObject getLocalUserInfo(HttpServletRequest request);

    /**
     * 退出登录
     * @param request 请求信息
     * @return 成功信息
     */
    Boolean userLogoff(HttpServletRequest request);

    /**
     * 修改审核状态
     * @param userId 用户ID
     * @param status 0-不通过 1-通过
     * @return 是否成功
     */
    Boolean updateReviewStatus(Long userId,Integer status);

    /**
     * 审核管理列表
     * @param jsonObject 分页信息
     * @return 审核管理列表
     */
    PageUtil<UserResponse> queryReviewByPage(JSONObject jsonObject);

    /**
     * 查询教师列表
     * @param jsonObject 分页信息
     * @return 教师列表
     */
    PageUtil<JSONObject> queryTeacherByPage(JSONObject jsonObject);

    /**
     * 查询学生列表
     * @param jsonObject 分页信息
     * @return 学生列表
     */
    PageUtil<JSONObject> queryStudentByPage(JSONObject jsonObject);
}
