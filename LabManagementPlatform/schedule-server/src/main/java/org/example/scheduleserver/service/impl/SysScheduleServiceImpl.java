package org.example.scheduleserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.example.core.common.ErrorCode;
import org.example.core.domain.SysChoice;
import org.example.core.domain.SysDictData;
import org.example.core.domain.SysUser;
import org.example.core.exception.BusinessException;
import org.example.core.mapper.SysChoiceMapper;
import org.example.core.mapper.SysDictDataMapper;
import org.example.core.mapper.SysUserMapper;
import org.example.core.util.PageUtil;
import org.example.core.util.StringUtils;
import org.example.scheduleserver.mapper.SysScheduleMapper;
import org.example.scheduleserver.mapper.SysSubmitMapper;
import org.example.scheduleserver.model.domain.SysSchedule;
import org.example.scheduleserver.model.domain.SysSubmit;
import org.example.scheduleserver.model.response.ScheduleResponse;
import org.example.scheduleserver.model.response.StudentScheduleResponse;
import org.example.scheduleserver.service.SysScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description 针对表【sys_schedule(排课表)】的数据库操作Service实现
 */
@Service
public class SysScheduleServiceImpl extends ServiceImpl<SysScheduleMapper, SysSchedule>
        implements SysScheduleService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private SysScheduleMapper scheduleMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysChoiceMapper choiceMapper;

    @Autowired
    private SysSubmitMapper submitMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<SysDictData> queryCollegeOrMajorList(String value) {
        if (StringUtils.isEmpty(value)) {
            //value为空的话，查询所有学院
            LambdaQueryWrapper<SysDictData> collegeWrapper = new LambdaQueryWrapper<>();
            collegeWrapper.eq(SysDictData::getDictType, "college");
            return dictDataMapper.selectList(collegeWrapper);
        } else {
            //value不为空的话，查询学院下的专业
            LambdaQueryWrapper<SysDictData> majorWrapper = new LambdaQueryWrapper<>();
            majorWrapper.eq(SysDictData::getDictType, "major");
            majorWrapper.eq(SysDictData::getRemark, value);
            return dictDataMapper.selectList(majorWrapper);
        }
    }

    @Override
    public String addOrUpdateSchedule(SysSchedule sysSchedule) {
        if (sysSchedule.getScheduleId() == null) {
            // 新增
            if (hasConflict(sysSchedule)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "课程时间冲突，无法新增");
            }
            baseMapper.insert(sysSchedule);
        } else {
            // 修改
            if (hasConflict(sysSchedule)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "课程时间冲突，无法修改");
            }
            baseMapper.updateById(sysSchedule);
        }
        return "success";
    }

    @Override
    public PageUtil<ScheduleResponse> queryScheduleByPage(JSONObject jsonObject) {
        LambdaQueryWrapper<SysSchedule> queryWrapper = new LambdaQueryWrapper<>();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(jsonObject.getString("teacherName"))) {
            // 模糊查询教师姓名
            LambdaQueryWrapper<SysUser> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.like(SysUser::getName, jsonObject.getString("teacherName"));
            queryWrapper1.eq(SysUser::getReviewStatus, 2);
            List<SysUser> sysUsers = userMapper.selectList(queryWrapper1);
            if (!sysUsers.isEmpty()) {
                queryWrapper.in(SysSchedule::getUserId, sysUsers.stream().map(SysUser::getUserId).collect(Collectors.toList()));
            } else {
                // 没有匹配的教师
                queryWrapper.eq(SysSchedule::getUserId, null);
            }
        }
        if (jsonObject.getLong("teacherId") != null) {
            queryWrapper.eq(SysSchedule::getUserId, jsonObject.getLong("teacherId"));
        }
        queryWrapper.orderByDesc(SysSchedule::getScheduleId);
        List<SysSchedule> sysSchedules = scheduleMapper.selectList(queryWrapper);
        List<ScheduleResponse> result = sysSchedules.stream().map(sysSchedule -> {
            ScheduleResponse scheduleResponse = new ScheduleResponse();
            BeanUtils.copyProperties(sysSchedule, scheduleResponse);
            LambdaQueryWrapper<SysUser> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(SysUser::getUserId, sysSchedule.getUserId());
            SysUser sysUser = userMapper.selectOne(teacherWrapper);
            scheduleResponse.setTeacherName(sysUser.getName());
            JSONObject courseObj = scheduleMapper.selectCourseById(sysSchedule.getCourseId());
            scheduleResponse.setCourseName(courseObj.getString("course_name"));
            scheduleResponse.setCourseNumber(courseObj.getString("number"));
            //打分和排课列表
            LambdaQueryWrapper<SysChoice> choiceWrapper = new LambdaQueryWrapper<>();
            choiceWrapper.eq(SysChoice::getCourseId, sysSchedule.getCourseId());
            List<SysChoice> sysChoices = choiceMapper.selectList(choiceWrapper);
            List<JSONObject> list = sysChoices.stream().map(sysChoice -> {
                JSONObject obj = new JSONObject();
                LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(SysUser::getUserId, sysChoice.getStudentId());
                SysUser user = userMapper.selectOne(userWrapper);
                obj.put("studentId", user.getUserId());
                obj.put("studentName", user.getName());
                obj.put("score", sysChoice.getScore());
                obj.put("evaluate", sysChoice.getEvaluate());
                obj.put("uploadResource", sysChoice.getUploadResource());



                return obj;
            }).collect(Collectors.toList());
            scheduleResponse.setScoreAndEvaluateList(list);

            //学生提交的实验
            List<JSONObject> arrayList = new ArrayList<>();
            LambdaQueryWrapper<SysSubmit> submitWrapper = new LambdaQueryWrapper<>();
            submitWrapper.eq(SysSubmit::getCourseId, sysSchedule.getCourseId());
            List<SysSubmit> sysSubmits = submitMapper.selectList(submitWrapper);
            if (!sysSubmits.isEmpty()) {
                sysSubmits.forEach(sysSubmit -> {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("userId", sysSubmit.getUserId());
                    LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
                    wrapper.eq(SysUser::getUserId, sysSubmit.getUserId());
                    SysUser user = sysUserMapper.selectOne(wrapper);
                    jsonObject1.put("userName", user.getName());
                    jsonObject1.put("submitResource", sysSubmit.getSubmitResource());
                    arrayList.add(jsonObject1);
                });
                scheduleResponse.setSubmitResources(arrayList);
            }

            return scheduleResponse;
        }).collect(Collectors.toList());
        return new PageUtil<>(jsonObject.getInteger("current"), jsonObject.getInteger("size"), result);
    }

    @Override
    public Boolean deleteScheduleByIds(List<Long> ids) {
        ids.forEach(id -> {
            SysSchedule sysSchedule = scheduleMapper.selectById(id);
            if (sysSchedule != null) {
                LambdaQueryWrapper<SysChoice> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SysChoice::getCourseId, sysSchedule.getCourseId());
                Long count = choiceMapper.selectCount(wrapper);
                if (count > 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR.getCode(), "该课程已被学生选择，无法删除");
                }
                ;
            }
            scheduleMapper.deleteById(id);
        });
        return true;
    }

    @Override
    public PageUtil<StudentScheduleResponse> queryScheduleByStudentId(JSONObject jsonObject, HttpServletRequest request) {
        long userId = Long.parseLong(request.getHeader("userId"));
        LambdaQueryWrapper<SysChoice> choiceWrapper = new LambdaQueryWrapper<>();
        choiceWrapper.eq(SysChoice::getStudentId, userId);
        List<SysChoice> sysChoices = choiceMapper.selectList(choiceWrapper);
        //校验逻辑
        if (sysChoices.isEmpty()) {
            return new PageUtil<>(jsonObject.getInteger("current"), jsonObject.getInteger("size"), new ArrayList<>());
        }
        List<Long> list = sysChoices.stream().map(SysChoice::getCourseId).collect(Collectors.toList());

        LambdaQueryWrapper<SysSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
        scheduleWrapper.in(SysSchedule::getCourseId, list);
        List<SysSchedule> sysSchedules = scheduleMapper.selectList(scheduleWrapper);

        List<StudentScheduleResponse> result = sysSchedules.stream().map(sysSchedule -> {
            StudentScheduleResponse scheduleResponse = new StudentScheduleResponse();
            BeanUtils.copyProperties(sysSchedule, scheduleResponse);
            LambdaQueryWrapper<SysUser> teacherWrapper = new LambdaQueryWrapper<>();
            teacherWrapper.eq(SysUser::getUserId, sysSchedule.getUserId());
            SysUser sysUser = userMapper.selectOne(teacherWrapper);
            scheduleResponse.setTeacherName(sysUser.getName());
            JSONObject courseObj = scheduleMapper.selectCourseById(sysSchedule.getCourseId());
            scheduleResponse.setCourseName(courseObj.getString("course_name"));
            scheduleResponse.setCourseNumber(courseObj.getString("number"));
            scheduleResponse.setCourseId(sysSchedule.getCourseId());
            LambdaQueryWrapper<SysSubmit> submitWrapper = new LambdaQueryWrapper<>();
            submitWrapper.eq(SysSubmit::getCourseId, sysSchedule.getCourseId());
            submitWrapper.eq(SysSubmit::getUserId, userId);
            SysSubmit sysSubmit = submitMapper.selectOne(submitWrapper);
            //学生提交的实验
            if (ObjectUtils.isNotEmpty(sysSubmit)) {
                scheduleResponse.setSubmitResource(sysSubmit.getSubmitResource());
            }
            //实验课程指导书,实验报告模版上传
            scheduleResponse.setUploadResource(sysSchedule.getUploadResource());
            //实验概论
            scheduleResponse.setContent(sysSchedule.getContent());

            QueryWrapper<SysChoice> wrapper = new QueryWrapper<>();
            wrapper.eq("course_id", sysSchedule.getCourseId());
            wrapper.eq("student_id", userId);
            SysChoice sysChoice = choiceMapper.selectOne(wrapper);
            if (sysChoice != null) {
                scheduleResponse.setScore(sysChoice.getScore());
                scheduleResponse.setEvaluate(sysChoice.getEvaluate());
            } else {
                scheduleResponse.setEvaluate("");
            }

            scheduleResponse.setStartTime(sysSchedule.getStartTime());
            scheduleResponse.setEndTime(sysSchedule.getEndTime());
            return scheduleResponse;
        }).collect(Collectors.toList());

        return new PageUtil<>(jsonObject.getInteger("current"), jsonObject.getInteger("size"), result);
    }

    @Override
    public String publishExperiment(Long id, String[] uploadResource, String content) {
        SysSchedule sysSchedule = new SysSchedule();
        sysSchedule.setScheduleId(id);
        sysSchedule.setContent(content);
        String finalString = String.join(",", uploadResource);
        sysSchedule.setUploadResource(finalString);
        scheduleMapper.updateById(sysSchedule);
        return "发布成功";
    }

    @Override
    public String submitExperiment(Long courseId, String[] uploadResource, HttpServletRequest request) {
        Long userId = Long.parseLong(request.getHeader("userId"));
        SysSubmit sysSubmit = new SysSubmit();
        sysSubmit.setUserId(userId);
        sysSubmit.setCourseId(courseId);
        sysSubmit.setSubmitResource(String.join(",", uploadResource));
        submitMapper.insert(sysSubmit);
        return "提交成功";
    }




    /**
     * 灵活的课程时间安排和冲突检测
     *
     * @param sysSchedule
     * @return
     */
    private boolean hasConflict(SysSchedule sysSchedule) {
        QueryWrapper<SysSchedule> queryWrapper = new QueryWrapper<>();
        // 添加教师 ID 的判断条件
        queryWrapper.eq("user_id", sysSchedule.getUserId());
        queryWrapper.and(wrapper -> wrapper
                .and(nested -> nested.between("start_time", sysSchedule.getStartTime(), sysSchedule.getEndTime()))
                .or(nested -> nested.between("end_time", sysSchedule.getStartTime(), sysSchedule.getEndTime()))
                .or(nested -> nested.le("start_time", sysSchedule.getStartTime()).ge("end_time", sysSchedule.getEndTime()))
        );
        if (sysSchedule.getScheduleId() != null) {
            // 修改时排除当前记录
            queryWrapper.ne("schedule_id", sysSchedule.getScheduleId());
        }
        List<SysSchedule> conflictSchedules = baseMapper.selectList(queryWrapper);
        return !conflictSchedules.isEmpty();
    }



    /**
     * 学生选课排行榜，top10
     */
    @Override
    public Map<String, Integer> echartTop10() {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", 2);
        List<SysUser> sysUsers = sysUserMapper.selectList(queryWrapper);

        Map<String, Integer> studentCourseCountMap = new HashMap<>();
        for (SysUser user : sysUsers) {
            QueryWrapper<SysChoice> choiceWrapper = new QueryWrapper<>();
            choiceWrapper.eq("student_id", user.getUserId());
            Long count = choiceMapper.selectCount(choiceWrapper);
            studentCourseCountMap.put(user.getName(), Math.toIntExact(count));
        }

        // 按照选课数量排序并取前10名
        return studentCourseCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public Map<String, Integer> teacherEchartTop10() {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", 1);
        List<SysUser> sysUsers = sysUserMapper.selectList(queryWrapper);
        Map<String, Integer> teacherCourseCountMap = new HashMap<>();

        for (SysUser user : sysUsers) {
            // 查出该老师所有的课程
            QueryWrapper<SysSchedule> sysScheduleQueryWrapper = new QueryWrapper<>();
            sysScheduleQueryWrapper.eq("user_id", user.getUserId());
            List<SysSchedule> sysSchedules = scheduleMapper.selectList(sysScheduleQueryWrapper);

            int totalCount = 0;
            for (SysSchedule schedule : sysSchedules) {
                // 根据 course_id 查询该课程被多少人选了
                QueryWrapper<SysChoice> choiceWrapper = new QueryWrapper<>();
                choiceWrapper.eq("course_id", schedule.getCourseId());
                totalCount += choiceMapper.selectCount(choiceWrapper);
            }
            teacherCourseCountMap.put(user.getName(), totalCount);
        }

        // 按照选课数量排序并取前10名
        return teacherCourseCountMap.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .limit(10)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

}




