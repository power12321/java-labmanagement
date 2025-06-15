package org.example.labserver.controller;

import org.example.core.common.BaseResponse;
import org.example.core.common.ResultUtils;
import org.example.core.util.PageUtil;
import org.example.labserver.model.request.CourseQueryRequest;
import org.example.labserver.model.request.CourseRequest;
import org.example.labserver.model.response.CourseQueryResponse;
import org.example.labserver.service.SysCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping
public class SysCourseController {

    @Autowired
    private SysCourseService courseService;

    /**
     * 添加或更新实验课程
     * @param courseRequest 课程请求
     * @return 成功信息
     */
    @PostMapping("/addOrUpdateCourse")
    public BaseResponse<String> addOrUpdateCourse(@RequestBody @Valid CourseRequest courseRequest)  {
        return ResultUtils.success(courseService.addOrUpdateCourse(courseRequest));
    }

    /**
     * 删除实验课程
     * @param ids 课程id
     * @return 成功信息
     */
    @GetMapping("/deleteCourseByIds")
    public BaseResponse<Boolean> deleteCourseByIds(@RequestParam List<Long> ids) {
        return ResultUtils.success(courseService.deleteCourseByIds(ids));
    }

    /**
     * 分页查询实验课程列表
     * @return 课程列表
     */
    @PostMapping("/queryCourseByPage")
    public BaseResponse<PageUtil<CourseQueryResponse>> queryCourseByPage(@RequestBody CourseQueryRequest courseQueryRequest)  {
        return ResultUtils.success(courseService.queryCourseByPage(courseQueryRequest));
    }
}
