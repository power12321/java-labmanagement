package org.example.labserver.mapper;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.labserver.model.domain.SysCourse;

import java.util.List;

/**
* @description 针对表【sys_course(课程表)】的数据库操作Mapper
*/
@Mapper
public interface SysCourseMapper extends BaseMapper<SysCourse> {

    /**
     * 查询课程的分数和评价
     * @param courseId 课程id
     * @return 课程的分数和评价
     */
    List<JSONObject> SelectScoreAndEvaluateList(Long courseId);


    /**
     * 查询课程的课程表
     * @param courseId
     * @return
     */
    JSONObject SelectScheduleOne(Long courseId);

}




