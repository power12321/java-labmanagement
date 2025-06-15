import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listWork(query) {
  return request({
    url: '/system/work/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getWork(employeeId) {
  return request({
    url: '/system/work/' + employeeId,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addWork(data) {
  return request({
    url: '/system/work',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateWork(data) {
  return request({
    url: '/system/work',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delWork(employeeId) {
  return request({
    url: '/system/work/' + employeeId,
    method: 'delete'
  })
}
