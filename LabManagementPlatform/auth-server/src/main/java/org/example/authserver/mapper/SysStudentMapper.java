package org.example.authserver.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.authserver.model.domain.SysStudent;

/**
* @description 针对表【sys_student(学生详情表)】的数据库操作Mapper
*/
@Mapper
public interface SysStudentMapper extends BaseMapper<SysStudent> {

}




