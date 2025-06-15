import request from '@/utils/request'


export function queryReviewByPageApi(data) {
    return request({
        url: '/auth/queryReviewByPage',
        method: 'post',
        data: data
    })
}
export function queryStudentByPageApi(data) {
    return request({
        url: '/auth/queryStudentByPage',
        method: 'post',
        data: data
    })
}
export function queryTeacherByPageApi(data) {
    return request({
        url: '/auth/queryTeacherByPage',
        method: 'post',
        data: data
    })
}
export function queryCourseByPageApi(data) {
    return request({
        url: '/course/queryCourseByPage',
        method: 'post',
        data: data
    })
}
export function addOrUpdateCourseApi(data) {
    return request({
        url: '/course/addOrUpdateCourse',
        method: 'post',
        data: data
    })
}



export function deleteCourseByIdsApi(data) {
    return request({
        url: '/course/deleteCourseByIds',
        method: 'get',
        params: data
    })
}

export function deleteScheduleByIdsApi(data) {
    return request({
        url: '/schedule/deleteScheduleByIds',
        method: 'get',
        params: data
    })
}
export function queryCollegeOrMajorListApi(data) {
    return request({
        url: '/schedule/queryCollegeOrMajorList',
        method: 'get',
        params: data

    })
}
export function queryScheduleByPageApi(data) {
    return request({
        url: '/schedule/queryScheduleByPage',
        method: 'post',
        data: data
    })
}

export function addOrUpdateScheduleApi(data) {
    return request({
        url: '/schedule/addOrUpdateSchedule',
        method: 'post',
        data: data
    })
}

export function queryChoiceByStudentIdApi(data) {
    return request({
        url: '/enrollment/queryChoiceByStudentId',
        method: 'get',


    })
}
export function queryScheduleByStudentIdApi(data) {
    return request({
        url: '/schedule/queryScheduleByStudentId',
        method: 'post',
        data: data

    })
}
export function getChoiceByStudentIdApi(data) {
    return request({
        url: '/enrollment/getChoiceByStudentId',
        method: 'get',
        params: data

    })
}

export function deleteChoiceByStudentIdApi(data) {
    return request({
        url: '/enrollment/deleteChoiceByStudentId',
        method: 'get',
        params: data

    })
}
export function insertScoreAndEvaluateApi(data) {
    return request({
        url: '/enrollment/insertScoreAndEvaluate',
        method: 'post',
        data: data

    })
}
export function updateReviewStatusApi(data) {
    return request({
        url: '/auth/updateReviewStatus',
        method: 'get',
        params: data

    })
}



export function echartTop10(data) {
    return request({
        url: '/schedule/echartTop10',
        method: 'get',
        params: data

    })
}

export function teacherEchartTop10(data) {
    return request({
        url: '/schedule/teacherEchartTop10',
        method: 'get',
        params: data

    })
}

export function getDevicePage(data) {
    return request({
        url: '/device/page',
        method: 'get',
        params: data

    })
}
export function pushDeviceAdd(data) {
    return request({
        url: '/device',
        method: 'post',
        data: data
    })
}

export function pushDeviceUp(data) {
    return request({
        url: '/device',
        method: 'put',
        data: data
    })
}

export function delDevice(data) {
    return request({
        url: `/device/${data.ids}`,
        method: 'delete',
    })
}

export function teDevice(data) {
    return request({
        url: `/device/borrow/${data.teacherId}`,
        method: 'post',
        data: data
    })
}

export function getDevices() {
    return request({
        url: `/device/borrowed`,
        method: 'get',
    })
}

export function getApprove(data) {
    return request({
        url: `/device/approve`,
        method: 'post',
        data: data
    })
}