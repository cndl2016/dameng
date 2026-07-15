/*域管理页面 api接口 wys 2024/10/31*/
import request from '@/utils/request'

// 查询域列表
export function listDomain(query) {
  return request({
    url: '/system/domain/list',
    method: 'get',
    params: query
  })
}

// 查询域详细
export function getDomain(domainId) {
  return request({
    url: '/system/domain/' + domainId,
    method: 'get'
  })
}

// 查询域用户
export function getDomainUser(domainId) {
  return request({
    url: '/system/domain/getDomainUser/' + domainId,
    method: 'get',
    timeout: 30000
  })
}

// 新增域
export function addDomain(data) {
  return request({
    url: '/system/domain',
    method: 'post',
    data: data
  })
}

// 导入域用户
export function importDomainUser(data) {
  return request({
    url: '/system/domain/importDomainUser',
    method: 'post',
    data: data
  })
}

// 修改域
export function updateDomain(data) {
  return request({
    url: '/system/domain',
    method: 'put',
    data: data
  })
}

// 删除域
export function delDomain(domainId) {
  return request({
    url: '/system/domain/' + domainId,
    method: 'delete'
  })
}

// 同步域用户
export function syncDomainUser(data) {
  return request({
    url: '/system/domain/syncDomainUser',
    method: 'post',
    data: data
  })
}

// 同步任务
export function handleSyncTask(data) {
  return request({
    url: '/system/domain/handleSyncTask',
    method: 'post',
    data: data
  })
}


// 获取域同步用户信息
export function getSyncUserList(domainId) {
  return request({
    url: '/system/domain/getSyncUserList/' + domainId,
    method: 'get'
  })
}
