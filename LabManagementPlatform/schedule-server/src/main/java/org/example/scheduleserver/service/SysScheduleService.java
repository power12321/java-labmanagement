package org.example.scheduleserver.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.core.domain.SysDictData;
import org.example.core.util.PageUtil;
import org.example.scheduleserver.model.domain.SysSchedule;
import org.example.scheduleserver.model.response.ScheduleResponse;
import org.example.scheduleserver.model.response.StudentScheduleResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @description 针对表【sys_schedule(排课表)】的数据库操作Service
 */
public interface SysScheduleService extends IService<SysSchedule> {

    /**
     * 查询学院或专业列表
     *
     * @param value 为空时查询所有学院，不为空时查询学院下的专业
     * @return 学院或专业列表
     */
    List<SysDictData> queryCollegeOrMajorList(String value);

    /**
     * 添加或更新排课
     *
     * @param sysSchedule 排课信息
     * @return 结果
     */
    String addOrUpdateSchedule(SysSchedule sysSchedule);

    /**
     * 分页查询排课
     *
     * @param jsonObject 排课查询条件
     * @return 结果
     */
    PageUtil<ScheduleResponse> queryScheduleByPage(JSONObject jsonObject);

    /**
     * 批量删除排课
     *
     * @param ids 排课id
     * @return 结果
     */
    Boolean deleteScheduleByIds(List<Long> ids);

    /**
     * 学生查询自己选过的课并且已经被排过的课
     *
     * @param jsonObject
     * @return
     */
    PageUtil<StudentScheduleResponse> queryScheduleByStudentId(JSONObject jsonObject, HttpServletRequest request);

    /**
     * 发布实验（实验课程指导书,实验报告模版上传）
     * @param id 排课id
     * @param uploadResource 资源
     * @param content 实验概论
     * @return
     */
    String publishExperiment(Long id,String[] uploadResource,String content);

    /**
     * 提交实验
     * @param courseId 课程id
     * @param uploadResource 资源
     * @return
     */
    String submitExperiment(Long courseId,String[] uploadResource,HttpServletRequest request);

    /**
     * 学生选课排行榜，top10
     */
    Map<String, Integer> echartTop10();

    /**
     * 学生选课排行榜，top10
     */
    Map<String, Integer> teacherEchartTop10();
}
