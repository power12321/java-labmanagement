package org.example.enrollmentserver.mapper;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.enrollmentserver.model.domain.Course;

import java.util.List;

/**
* @description 针对表【sys_course(课程表)】的数据库操作Mapper
*/
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

}




