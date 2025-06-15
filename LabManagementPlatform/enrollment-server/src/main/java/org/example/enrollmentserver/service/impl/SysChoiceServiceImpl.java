package org.example.enrollmentserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.core.common.ErrorCode;
import org.example.core.domain.SysChoice;
import org.example.core.domain.SysDictData;
import org.example.core.exception.BusinessException;
import org.example.core.mapper.SysChoiceMapper;
import org.example.core.mapper.SysDictDataMapper;
import org.example.enrollmentserver.mapper.CourseMapper;
import org.example.enrollmentserver.model.domain.Course;
import org.example.enrollmentserver.service.SysChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
* @description 针对表【sys_choice(学生选课表)】的数据库操作Service实现
*/
@Service
public class SysChoiceServiceImpl extends ServiceImpl<SysChoiceMapper, SysChoice>
    implements SysChoiceService {

    @Autowired
    private SysChoiceMapper choiceMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Override
    public String getChoiceByStudentId(HttpServletRequest request, List<Long> courseIds) {
//        long electiveCount = sysCourseMapper.countElectiveCourses(courseIds);
//        if (electiveCount > 2) {
//            return "选修课程数量不能超过 2 个";
//        }
        Long userId = Long.parseLong(request.getHeader("userId"));
        // 统计选修课程数量
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.in("course_id", courseIds).eq("type", 1);
        long electiveCount = courseMapper.selectCount(wrapper);

        if (electiveCount > 2) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "选修课程数量不能超过 2 个");
        }

        //判断是否已经选过该课程
        QueryWrapper<SysChoice> choiceWrapper = new QueryWrapper<>();
        choiceWrapper.eq("student_id", userId).in("course_id", courseIds);
        Long count = choiceMapper.selectCount(choiceWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "请勿重复选课");
        }

        courseIds.forEach(courseId -> {
            SysChoice sysChoice = new SysChoice();
            sysChoice.setStudentId(userId);
            sysChoice.setCourseId(courseId);
            this.save(sysChoice);
        });
        return "success";
    }

    @Override
    public Boolean deleteChoiceByStudentId(List<Long> choiceIds) {
        choiceIds.forEach(choiceId -> {
            choiceMapper.deleteById(choiceId);
        });
        return true;
    }

    @Override
    public List<JSONObject> queryChoiceByStudentId(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("userId"));
        QueryWrapper<SysChoice> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", userId);
        List<SysChoice> sysChoices = choiceMapper.selectList(wrapper);
        return sysChoices.stream().map(sysChoice -> {
            JSONObject jsonObject = new JSONObject();
            Course course = courseMapper.selectById(sysChoice.getCourseId());
            jsonObject.put("courseId", course.getCourseId());
            jsonObject.put("choiceId", sysChoice.getChoiceId());
            jsonObject.put("courseName", course.getCourseName());
            jsonObject.put("courseType", course.getType());
            jsonObject.put("period", course.getPeriod());
            jsonObject.put("credits", course.getCredits());
            LambdaQueryWrapper<SysDictData> collegeWrapper = new LambdaQueryWrapper<>();
            collegeWrapper.eq(SysDictData::getDictType, "college");
            collegeWrapper.eq(SysDictData::getDictValue, course.getCollege());
            SysDictData sysDictData = dictDataMapper.selectOne(collegeWrapper);
            jsonObject.put("college", sysDictData.getDictLabel());
            LambdaQueryWrapper<SysDictData> majorWrapper = new LambdaQueryWrapper<>();
            majorWrapper.eq(SysDictData::getDictType, "major");
            majorWrapper.eq(SysDictData::getDictValue, course.getMajor());
            sysDictData = dictDataMapper.selectOne(majorWrapper);
            jsonObject.put("major", sysDictData.getDictLabel());
            return jsonObject;
        }).collect(Collectors.toList());
    }

    @Override
    public String insertScoreAndEvaluate(JSONObject jsonObject,HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("userId"));
        LambdaUpdateWrapper<SysChoice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysChoice::getCourseId, jsonObject.getLong("choiceId"));
        updateWrapper.eq(SysChoice::getStudentId, userId);
        updateWrapper.set(SysChoice::getScore, jsonObject.getInteger("score"));
        updateWrapper.set(SysChoice::getEvaluate, jsonObject.getString("evaluate"));
        updateWrapper.set(SysChoice::getUploadResource, jsonObject.getString("uploadResource"));
        choiceMapper.update(null, updateWrapper);
        return "success";
    }
}




