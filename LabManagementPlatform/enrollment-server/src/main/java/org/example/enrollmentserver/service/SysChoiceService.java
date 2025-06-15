package org.example.enrollmentserver.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.core.domain.SysChoice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @description 针对表【sys_choice(学生选课表)】的数据库操作Service
*/
public interface SysChoiceService extends IService<SysChoice> {

    /**
     * 选课
     * @param request
     * @param courseIds
     * @return
     */
    String getChoiceByStudentId(HttpServletRequest request, List<Long> courseIds);

    /**
     * 退课
     * @param choiceIds 选课主键ID
     * @return
     */
    Boolean deleteChoiceByStudentId(List<Long> choiceIds);

    /**
     * 学生查询自己的选课
     * @param request
     * @return
     */
    List<JSONObject> queryChoiceByStudentId(HttpServletRequest request);

    /**
     * 插入打分和评价
     * @param jsonObject 包含选课ID、分数和评价内容的JSON对象
     * @return 返回插入的信息
     */
    String insertScoreAndEvaluate(JSONObject jsonObject,HttpServletRequest request);

}
