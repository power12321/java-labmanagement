import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listParentservice(query) {
  return request({
    url: '/system/parentservice/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getParentservice(parentserviceorder) {
  return request({
    url: '/system/parentservice/' + parentserviceorder,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addParentservice(data) {
  return request({
    url: '/system/parentservice',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateParentservice(data) {
  return request({
    url: '/system/parentservice',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delParentservice(parentserviceorder) {
  return request({
    url: '/system/parentservice/' + parentserviceorder,
    method: 'delete'
  })
}
