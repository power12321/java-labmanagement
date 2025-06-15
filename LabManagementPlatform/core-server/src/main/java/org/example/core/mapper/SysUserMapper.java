package org.example.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.core.domain.SysUser;

/**
* @description 针对表【sys_user(用户信息表)】的数据库操作Mapper
*/
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}




