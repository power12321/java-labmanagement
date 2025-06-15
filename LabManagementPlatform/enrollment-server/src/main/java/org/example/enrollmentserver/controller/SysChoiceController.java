package org.example.enrollmentserver.controller;

import com.alibaba.fastjson.JSONObject;
import org.example.core.common.BaseResponse;
import org.example.core.common.ResultUtils;
import org.example.enrollmentserver.service.SysChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping
public class SysChoiceController {

    @Autowired
    private SysChoiceService choiceService;

    /**
     * 选课
     * @param request
     * @param courseIds
     * @return
     */
    @GetMapping("/getChoiceByStudentId")
    public BaseResponse<String> getChoiceByStudentId(HttpServletRequest request, @RequestParam List<Long> courseIds) {
        return ResultUtils.success(choiceService.getChoiceByStudentId(request, courseIds));
    }

    /**
     * 退课
     * @param choiceIds 选课主键ID
     * @return
     */
    @GetMapping("/deleteChoiceByStudentId")
    public BaseResponse<Boolean> deleteChoiceByStudentId(@RequestParam List<Long> choiceIds) {
        return ResultUtils.success(choiceService.deleteChoiceByStudentId(choiceIds));
    }

    /**
     * 学生查询自己的选课
     * @param request
     * @return
     */
    @GetMapping("/queryChoiceByStudentId")
    public BaseResponse<List<JSONObject>> queryChoiceByStudentId(HttpServletRequest request) {
        return ResultUtils.success(choiceService.queryChoiceByStudentId(request));
    }

    /**
     * 插入打分和评价
     * @param jsonObject 包含选课ID、分数和评价内容的JSON对象
     * @return 返回插入的信息
     */
    @PostMapping("/insertScoreAndEvaluate")
    public BaseResponse<String> insertScoreAndEvaluate(@RequestBody JSONObject jsonObject,HttpServletRequest request)  {
        return ResultUtils.success(choiceService.insertScoreAndEvaluate(jsonObject,request));
    }
}
