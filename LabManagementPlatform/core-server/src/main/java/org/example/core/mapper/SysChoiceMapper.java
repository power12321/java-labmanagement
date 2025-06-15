package org.example.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.core.domain.SysChoice;

import java.util.List;

/**
* @description 针对表【sys_choice(学生选课表)】的数据库操作Mapper
*/
@Mapper
public interface SysChoiceMapper extends BaseMapper<SysChoice> {

    /**
     * 统计选修课程数量
     * @param courseIds 课程 ID 列表
     * @return 选修课程数量
     */
    long countElectiveCourses(@Param("courseIds") List<Long> courseIds);

}




