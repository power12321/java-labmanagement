package org.example.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.core.domain.SysDictData;

/**
* @description 针对表【sys_dict_data(字典数据表)】的数据库操作Mapper
*/

@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

}




