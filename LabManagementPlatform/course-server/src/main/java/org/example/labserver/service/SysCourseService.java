package org.example.labserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.core.util.PageUtil;
import org.example.labserver.model.domain.SysCourse;
import org.example.labserver.model.request.CourseQueryRequest;
import org.example.labserver.model.request.CourseRequest;
import org.example.labserver.model.response.CourseQueryResponse;

import java.util.List;

/**
* @description 针对表【sys_course(课程表)】的数据库操作Service
*/
public interface SysCourseService extends IService<SysCourse> {

    /**
     * 添加或更新实验课程
     * @param courseRequest 课程请求
     * @return 成功信息
     */
    String addOrUpdateCourse(CourseRequest courseRequest);

    /**
     * 删除课程
     * @param ids 课程id
     * @return 成功信息
     */
    Boolean deleteCourseByIds(List<Long> ids);

    /**
     * 分页查询实验课程列表
     * @return 课程列表
     */
    PageUtil<CourseQueryResponse> queryCourseByPage(CourseQueryRequest courseQueryRequest);
}
