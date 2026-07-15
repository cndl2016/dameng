// 查询告警模板列表 wys 2024/08/15
import request from '@/utils/request'

// 查询版本列表
export function getListVersion(data) {
  return request({
    url: '/base/version/getListVersion',
    method: 'get',
    params: data
  })
}

// 版本启用禁用状态修改
export function enableVersion(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/base/version/enableVersion',
    method: 'put',
    data: data,
  })
}

// 新增修改版本
export function saveOrUpdateVersion(data) {
  return request({
    url: '/base/version/saveOrUpdateVersion',
    method: 'post',
    headers: {"Content-Type": "multipart/form-data"},
    data: data
  })
}

// 删除版本
export function deleteVersionById(id) {
  return request({
    url: '/base/version/delete/' + id,
    method: 'delete'
  })
}

//下载版本文件
export function downloadFile(data) {
    return request({
      url: '/base/version/downloadFile/' + data,
      method: 'post',
      responseType: 'blob'
    })
}

// 获取实例版本列表 实例管理页面 姚宇吉 2024/08/14
export function getInsVersionList() {
  return request({
    url: '/base/version/getInsVersionList',
    method: 'get'
  })
}

// 部署时校验版本信息
export function checkVersionForInstall(id) {
  return request({
    url: '/base/version/checkVersionForInstall/' + id,
    method: 'get'
  })
}

