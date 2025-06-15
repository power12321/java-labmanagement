import request from '@/utils/request'

// 查询出勤列表
export function listAvailability(query) {
  return request({
    url: '/system/availability/list',
    method: 'get',
    params: query
  })
}

// 查询出勤详细
export function getAvailability(availabilityId) {
  return request({
    url: '/system/availability/' + availabilityId,
    method: 'get'
  })
}

// 新增出勤
export function addAvailability(data) {
  return request({
    url: '/system/availability',
    method: 'post',
    data: data
  })
}

// 修改出勤
export function updateAvailability(data) {
  return request({
    url: '/system/availability',
    method: 'put',
    data: data
  })
}

// 删除出勤
export function delAvailability(availabilityId) {
  return request({
    url: '/system/availability/' + availabilityId,
    method: 'delete'
  })
}
