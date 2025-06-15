package org.example.labserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.core.common.ErrorCode;
import org.example.core.domain.SysChoice;
import org.example.core.domain.SysDictData;
import org.example.core.exception.BusinessException;
import org.example.core.mapper.SysChoiceMapper;
import org.example.core.mapper.SysDictDataMapper;
import org.example.core.util.PageUtil;
import org.example.core.util.StringUtils;
import org.example.labserver.mapper.SysCourseMapper;
import org.example.labserver.model.domain.SysCourse;
import org.example.labserver.model.request.CourseQueryRequest;
import org.example.labserver.model.request.CourseRequest;
import org.example.labserver.model.response.CourseQueryResponse;
import org.example.labserver.service.SysCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @description 针对表【sys_course(课程表)】的数据库操作Service实现
*/
@Service
public class SysCourseServiceImpl extends ServiceImpl<SysCourseMapper, SysCourse>
    implements SysCourseService {

    @Autowired
    private SysChoiceMapper choiceMapper;

    @Autowired
    private SysCourseMapper courseMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Override
    public String addOrUpdateCourse(CourseRequest courseRequest) {
        if (courseRequest.getCourseId() == null) {
            // 新增实验课程信息
            LambdaQueryWrapper<SysCourse> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysCourse::getNumber, courseRequest.getNumber());
            SysCourse sysCourse = this.getOne(queryWrapper);
            if (sysCourse != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "实验课程编号已存在");
            }
            SysCourse courseEntity = new SysCourse();
            BeanUtils.copyProperties(courseRequest, courseEntity);
            this.save(courseEntity);
        } else {
            // 修改课程信息
            SysCourse courseEntity = this.getById(courseRequest.getCourseId());
            if (courseEntity == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR.getCode(), "实验课程不存在");
            }
            BeanUtils.copyProperties(courseRequest, courseEntity);
            this.updateById(courseEntity);
        }
        return "success";
    }

    @Override
    public Boolean deleteCourseByIds(List<Long> ids) {
        ids.forEach(id -> {
            SysCourse courseEntity = this.getById(id);
            if (courseEntity == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR.getCode(), "实验课程不存在");
            }
            LambdaQueryWrapper<SysChoice> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SysChoice::getCourseId, id);
            Long count = choiceMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR.getCode(), "该实验课程下已有学生选课，无法删除");
            }
            JSONObject jsonObject = courseMapper.SelectScheduleOne(id);
            if (jsonObject != null)  {
                throw new BusinessException(ErrorCode.FORBIDDEN_ERROR.getCode(), "该实验课程下已有课程安排，无法删除");
            }
            this.removeById(id);
        });
        return true;
    }

    @Override
    public PageUtil<CourseQueryResponse> queryCourseByPage(CourseQueryRequest courseQueryRequest) {
        LambdaQueryWrapper<SysCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(courseQueryRequest.getNumber()), SysCourse::getNumber, courseQueryRequest.getNumber());
        queryWrapper.like(StringUtils.isNotEmpty(courseQueryRequest.getCourseName()), SysCourse::getCourseName, courseQueryRequest.getCourseName());
        queryWrapper.eq( StringUtils.isNotEmpty(courseQueryRequest.getCollege()), SysCourse::getCollege, courseQueryRequest.getCollege());
        queryWrapper.eq( StringUtils.isNotEmpty(courseQueryRequest.getMajor()), SysCourse::getMajor, courseQueryRequest.getMajor());
        queryWrapper.orderByDesc(SysCourse::getCreateTime);
        List<SysCourse> courseList = this.list(queryWrapper);
        List<CourseQueryResponse> courseQueryResponseList = courseList.stream().map(course -> {
            CourseQueryResponse courseQueryResponse = new CourseQueryResponse();
            BeanUtils.copyProperties(course, courseQueryResponse);
            courseQueryResponse.setCollegeId(course.getCollege());
            courseQueryResponse.setMajorId(course.getMajor());

            LambdaQueryWrapper<SysDictData> collegeWrapper = new LambdaQueryWrapper<>();
            collegeWrapper.eq(SysDictData::getDictValue, course.getCollege());
            collegeWrapper.eq(SysDictData::getDictType, "college");
            SysDictData college = dictDataMapper.selectOne(collegeWrapper);
            courseQueryResponse.setCollegeLabel(college.getDictLabel());

            LambdaQueryWrapper<SysDictData> majorWrapper = new LambdaQueryWrapper<>();
            majorWrapper.eq(SysDictData::getDictValue, course.getMajor());
            majorWrapper.eq(SysDictData::getDictType, "major");
            SysDictData major = dictDataMapper.selectOne(majorWrapper);
            courseQueryResponse.setMajorLabel(major.getDictLabel());

            return courseQueryResponse;
        }).collect(Collectors.toList());
        return new PageUtil<>(courseQueryRequest.getCurrent(), courseQueryRequest.getSize(), courseQueryResponseList);
    }
}




