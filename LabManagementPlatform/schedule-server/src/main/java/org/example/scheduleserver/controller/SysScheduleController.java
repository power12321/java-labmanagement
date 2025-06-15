package org.example.scheduleserver.controller;

import com.alibaba.fastjson.JSONObject;
import org.example.core.common.BaseResponse;
import org.example.core.common.ResultUtils;
import org.example.core.domain.SysDictData;
import org.example.core.util.PageUtil;
import org.example.scheduleserver.model.domain.SysSchedule;
import org.example.scheduleserver.model.response.ScheduleResponse;
import org.example.scheduleserver.model.response.StudentScheduleResponse;
import org.example.scheduleserver.service.SysScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class SysScheduleController {

    @Autowired
    private SysScheduleService scheduleService;

    /**
     * 查询学院或专业列表
     * @param value 为空时查询所有学院，不为空时查询学院下的专业
     * @return 学院或专业列表
     */
    @GetMapping("/queryCollegeOrMajorList")
    public BaseResponse<List<SysDictData>> queryCollegeOrMajorList(String value)  {
        return ResultUtils.success(scheduleService.queryCollegeOrMajorList(value));
    }

    /**
     * 添加或更新实验课排课
     * @param sysSchedule 排课信息
     * @return 结果
     */
    @PostMapping("/addOrUpdateSchedule")
    public BaseResponse<String> addOrUpdateSchedule(@RequestBody @Valid SysSchedule sysSchedule)  {
        return ResultUtils.success(scheduleService.addOrUpdateSchedule(sysSchedule));
    }

    /**
     * 分页查询实验排课
     * @param jsonObject 排课查询条件
     * @return 结果
     */
    @PostMapping("/queryScheduleByPage")
    public BaseResponse<PageUtil<ScheduleResponse>> queryScheduleByPage(@RequestBody JSONObject jsonObject)  {
        return ResultUtils.success(scheduleService.queryScheduleByPage(jsonObject));
    }

    /**
     * 批量删除实验排课
     * @param ids 排课id
     * @return 结果
     */
    @GetMapping("/deleteScheduleByIds")
    public BaseResponse<Boolean> deleteScheduleByIds(@RequestParam List<Long> ids)  {
        return ResultUtils.success(scheduleService.deleteScheduleByIds(ids));
    }

    /**
     * 学生查询自己选过的课并且已经被排过的课
     * @param jsonObject
     * @return
     */
    @PostMapping("/queryScheduleByStudentId")
    public BaseResponse<PageUtil<StudentScheduleResponse>> queryScheduleByStudentId(@RequestBody JSONObject jsonObject, HttpServletRequest request)  {
        return ResultUtils.success(scheduleService.queryScheduleByStudentId(jsonObject,request));
    }

    /**
     * 发布实验（实验课程指导书,实验报告模版上传）
     * @param id 排课id
     * @param uploadResource 资源
     * @param content 实验概论
     * @return
     */
    @GetMapping("/publishExperiment")
    public BaseResponse<String> publishExperiment(Long id,String[] uploadResource,String content)  {
        return ResultUtils.success(scheduleService.publishExperiment(id,uploadResource,content));
    }

    /**
     * 提交实验
     * @param courseId 课程id
     * @param uploadResource 资源
     * @return
     */
    @GetMapping("/submitExperiment")
    public BaseResponse<String> submitExperiment(Long courseId,String[] uploadResource,HttpServletRequest request)  {
        return ResultUtils.success(scheduleService.submitExperiment(courseId,uploadResource,request));
    }


    /**
     * 学生选课排行榜，top10
     */
    @GetMapping("/echartTop10")
    public BaseResponse<Map<String,Integer>> echartTop10()  {
        return ResultUtils.success(scheduleService.echartTop10());
    }

    /**
     * 学生选课排行榜，top10
     */
    @GetMapping("/teacherEchartTop10")
    public BaseResponse<Map<String,Integer>> teacherEchartTop10()  {
        return ResultUtils.success(scheduleService.teacherEchartTop10());
    }

}
