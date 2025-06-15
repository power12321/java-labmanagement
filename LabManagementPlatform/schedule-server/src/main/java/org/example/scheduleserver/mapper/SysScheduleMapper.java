package org.example.scheduleserver.mapper;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.scheduleserver.model.domain.SysSchedule;

/**
* @description 针对表【sys_schedule(排课表)】的数据库操作Mapper
*/
@Mapper
public interface SysScheduleMapper extends BaseMapper<SysSchedule> {

    JSONObject selectCourseById(Long courseId);
}




